package com.kumaran.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kumaran.entity.Document;

@Transactional
public interface FileRepository extends JpaRepository<Document, Long>{

	Document findByDocName(String fileName);

}
