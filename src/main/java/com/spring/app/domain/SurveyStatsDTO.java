// src/main/java/com/spring/app/domain/SurveyStatsDTO.java
package com.spring.app.domain;

import lombok.*;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SurveyStatsDTO {
    private Long surveyId;
    private int totalResponses;          // 설문 전체 응답 수
    private List<QuestionStatDTO> questions; // 문항별 집계
    
 // ▼ [추가] 요약카드용 필드
    private Integer eligible;     // 전체 대상자 수
    private Integer notAnswered;  // 미참여(eligible - totalResponses)
}
