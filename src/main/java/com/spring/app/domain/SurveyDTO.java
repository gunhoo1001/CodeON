// src/main/java/com/spring/app/domain/SurveyDTO.java
package com.spring.app.domain;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SurveyDTO {
    private Long surveyId;
    private String title;
    private Long ownerMemberSeq;
    private LocalDate startDate;
    private LocalDate endDate;
    // REG_DATE는 매퍼에서 TO_CHAR로 문자열로 반환(타임존 오류 회피)
    private String regDate;
    private String statusCode;     // OPEN / CLOSED
    private String questionsJson;  // JSON 문자열

    // 생성 시 조사대상 전달용(컬럼 아님)
    private String targetType;     // ALL / DEPT / MEMBER
    private Long targetDept;       // targetType=DEPT
    private List<Long> targetMembers; // targetType=MEMBER
    
    // ====== 뷰/권한 표시용(응답 가능 여부) ======
    private transient Boolean canAnswer;        // 이 사용자가 지금 응답 가능?
    private transient Boolean answered;         // 이미 응답했는가?
    private transient String  notAllowedReason; // 응답 불가 사유(문자열)
}
