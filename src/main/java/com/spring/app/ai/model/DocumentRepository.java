package com.spring.app.ai.model;

import java.util.List;

import com.spring.app.ai.domain.DocumentDTO;

public interface DocumentRepository {
    void saveAll(List<DocumentDTO> documents);
    void saveDocument(DocumentDTO doc);
    List<DocumentDTO> findAll();
    List<DocumentDTO> fetchAllTables(); // DB 전체 테이블 가져오기
}
