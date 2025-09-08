// src/main/java/com/spring/app/service/SurveyService.java
package com.spring.app.service;

import com.spring.app.domain.*;
import java.util.List;

public interface SurveyService {
    Long createSurveyWithTargets(SurveyDTO dto);

    List<SurveyDTO> getVisibleSurveys(String status, Long memberSeq, Long deptSeq);

    SurveyDTO getSurvey(Long surveyId);
    List<SurveyTargetDTO> getTargets(Long surveyId);

    boolean canView(Long surveyId, Long memberSeq, Long deptSeq);

    // === 응답/통계 ===
    boolean hasResponded(Long surveyId, Long memberSeq);
    void submitResponse(Long surveyId, Long memberSeq, String answersJson);

    SurveyStatsDTO getStats(Long surveyId);
    
}
