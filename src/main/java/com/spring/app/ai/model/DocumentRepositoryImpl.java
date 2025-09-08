package com.spring.app.ai.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.spring.app.ai.domain.DocumentDTO;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DocumentRepositoryImpl implements DocumentRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void saveAll(List<DocumentDTO> documents) {
        for (DocumentDTO doc : documents) {
            jdbcTemplate.update(
                "INSERT INTO DOCUMENT(ID, TABLE_NAME, COLUMN_NAME, VALUE) " +
                "VALUES(DOCUMENT_SEQ.NEXTVAL, ?, ?, ?)",
                doc.getTableName(),
                String.join(",", doc.getColumnNames()), // 리스트 → CSV
                String.join(",", doc.getValues())       // 리스트 → CSV
            );
        }
    }

    @Override
    @Transactional
    public void saveDocument(DocumentDTO doc) {
        jdbcTemplate.update(
            "INSERT INTO DOCUMENT(ID, TABLE_NAME, COLUMN_NAME, VALUE) VALUES(DOCUMENT_SEQ.NEXTVAL, ?, ?, ?)",
            doc.getTableName(),
            String.join(",", doc.getColumnNames()), // 리스트 → CSV
            String.join(",", doc.getValues())       // 리스트 → CSV
        );
    }

    @Override
    public List<DocumentDTO> findAll() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM DOCUMENT");
        List<DocumentDTO> list = new ArrayList<>();

        for (Map<String, Object> row : rows) {
            list.add(DocumentDTO.builder()
                    .id(((Number) row.get("ID")).longValue())
                    .tableName((String) row.get("TABLE_NAME"))
                    .columnNames(List.of(((String) row.get("COLUMN_NAME")).split(","))) // CSV → List
                    .values(List.of(((String) row.get("VALUE")).split(",")))            // CSV → List
                    .build());
        }
        return list;
    }

    @Override
    public List<DocumentDTO> fetchAllTables() {
        List<DocumentDTO> documents = new ArrayList<>();
        List<String> tables = jdbcTemplate.queryForList("SELECT table_name FROM user_tables", String.class);

        for (String table : tables) {
            List<String> columns = jdbcTemplate.queryForList(
                "SELECT column_name FROM user_tab_columns WHERE table_name = ?", String.class, table);

            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT * FROM " + table + " WHERE ROWNUM <= 5"); // 샘플링

            for (Map<String, Object> row : rows) {
                List<String> values = new ArrayList<>();
                for (String column : columns) {
                    Object value = row.get(column);
                    values.add(value != null ? value.toString() : "NULL");
                }

                documents.add(DocumentDTO.builder()
                        .tableName(table)
                        .columnNames(columns) // 컬럼 전체
                        .values(values)       // 값 전체
                        .build());
            }
        }
        return documents;
    }
}
