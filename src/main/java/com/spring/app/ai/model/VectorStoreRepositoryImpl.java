package com.spring.app.ai.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.spring.app.ai.domain.DocumentDTO;

@Repository
public class VectorStoreRepositoryImpl implements VectorStoreRepository {

    private static class VectorDoc {
        DocumentDTO doc;
        float[] embedding;

        VectorDoc(DocumentDTO doc, float[] embedding) {
            this.doc = doc;
            this.embedding = embedding;
        }
    }

    private final List<VectorDoc> store = new ArrayList<>();

    @Override
    public void save(DocumentDTO doc, float[] embedding) {
        store.add(new VectorDoc(doc, embedding));
    }

    @Override
    public List<DocumentDTO> search(float[] queryEmbedding, int topK) {
        return store.stream()
                .sorted(Comparator.comparingDouble(v -> -cosineSimilarity(v.embedding, queryEmbedding)))
                .limit(topK)
                .map(v -> v.doc)
                .toList();
    }

    private double cosineSimilarity(float[] a, float[] b) {
        double dot = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB) + 1e-10);
    }
}
