// src/main/java/com/spring/app/model/SurveyDAO.java
package com.spring.app.model;

import com.spring.app.domain.*;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface SurveyDAO {

    int insertSurvey(SurveyDTO dto);

    int insertSurveyTargets(@Param("list") List<SurveyTargetDTO> list);

    List<SurveyDTO> selectSurveysVisible(@Param("status") String status,
                                         @Param("memberSeq") Long memberSeq,
                                         @Param("deptSeq") Long deptSeq);

    SurveyDTO selectSurveyById(@Param("surveyId") Long surveyId);

    List<SurveyTargetDTO> selectSurveyTargets(@Param("surveyId") Long surveyId);

    int canViewSurvey(@Param("surveyId") Long surveyId,
                      @Param("memberSeq") Long memberSeq,
                      @Param("deptSeq")   Long deptSeq);

    // ====== 응답/통계용 추가 ======
    int insertResponse(SurveyRespDTO dto);

    int existsResponseByMember(@Param("surveyId") Long surveyId,
                               @Param("memberSeq") Long memberSeq);

    List<String> selectAnswersJsonList(@Param("surveyId") Long surveyId);
    
    // ▼ [추가] 요약카드 계산용
    int countEligibleMembers(@Param("surveyId") Long surveyId);
    int countResponsesBySurvey(@Param("surveyId") Long surveyId);
}
