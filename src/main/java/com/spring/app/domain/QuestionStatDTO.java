// src/main/java/com/spring/app/domain/QuestionStatDTO.java
package com.spring.app.domain;

import lombok.*;
import java.util.LinkedHashMap;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class QuestionStatDTO {
    private int index;                 // 1-based
    private String type;               // SINGLE | MULTI | TEXT
    private String title;              // 문항 제목
    private LinkedHashMap<String, Integer> counts; // SINGLE/MULTI 옵션별 집계
    private int responses;             // 해당 문항 응답 수(널 제외)
   
}
