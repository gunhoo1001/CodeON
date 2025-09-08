package com.spring.app.ai.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentDTO {
    private Long id;
    private String tableName;
    private List<String> columnNames;  // 여러 컬럼명
    private List<String> values;       // 각 컬럼에 맞는 값
}
