package com.spring.app.ai.model;

import java.util.List;

import com.spring.app.ai.domain.DocumentDTO;

public interface VectorStoreRepository {
    void save(DocumentDTO doc, float[] embedding);
    List<DocumentDTO> search(float[] queryEmbedding, int topK);
}
