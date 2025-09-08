package com.spring.app.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SurveyTargetDTO {
    private Long targetId;     // PK: SEQ_SURVEY_TARGET
    private Long surveyId;     // FK: TBL_SURVEY.SURVEY_ID
    private String targetType; // 'ALL' | 'DEPT' | 'MEMBER'
    private Long deptSeq;      // nullable when not DEPT
    private Long memberSeq;    // nullable when not MEMBER
}
