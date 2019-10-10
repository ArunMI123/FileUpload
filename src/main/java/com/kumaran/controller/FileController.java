package com.kumaran.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.kumaran.entity.Document;
import com.kumaran.repository.FileRepository;

@RestController
public class FileController {

	@Autowired
	FileRepository fileRepo;
	
	@PostMapping("/upload/db")
	public ResponseEntity uploadToDB(@RequestParam("file") MultipartFile file) {
		Document doc = new Document();
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		doc.setDocName(fileName);
		try {
			doc.setFile(file.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		fileRepo.save(doc);
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/download/")
				.path(fileName).path("/db")
				.toUriString();
		return ResponseEntity.ok(fileDownloadUri);
	}
	
	@GetMapping("/download/{fileName:.+}/db")
	public ResponseEntity downloadFromDB(@PathVariable String fileName) {
		Document document = fileRepo.findByDocName(fileName);
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType("application/json"))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
				.body(document.getFile());
	}
	
	@GetMapping(value = "/zip-download", produces="application/zip")
	public byte[] zipDownload(@RequestParam List<String> name, HttpServletResponse response) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);
		for (String fileName : name) {
			Document doc = fileRepo.findByDocName(fileName);
		    ZipEntry entry = new ZipEntry(fileName);
		    entry.setSize(doc.getFile().length);
		    zos.putNextEntry(entry);
		    zos.write(doc.getFile());
		}
		zos.closeEntry();
		zos.close();
	    return baos.toByteArray();
	}
	
	@PostMapping("/multi-upload")
	public ResponseEntity multiUpload(@RequestParam("files") MultipartFile[] files) {
		for(MultipartFile uploadedFile : files) {
			Document doc = new Document();
			doc.setDocName((uploadedFile.getOriginalFilename()));
			try {
				doc.setFile(uploadedFile.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
			fileRepo.save(doc);
        }
		return ResponseEntity.ok("success");
	}

}
