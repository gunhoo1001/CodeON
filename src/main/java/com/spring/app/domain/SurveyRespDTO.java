// src/main/java/com/spring/app/domain/SurveyRespDTO.java
package com.spring.app.domain;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SurveyRespDTO {
    private Long respId;
    private Long surveyId;
    private Long respMemberSeq;
    private String answersJson;  // {"q1":"A","q2":["B","C"],"q3":"서술"}
    // respDate는 DB default(SYSTIMESTAMP) 사용 → 굳이 매핑하지 않음
}
