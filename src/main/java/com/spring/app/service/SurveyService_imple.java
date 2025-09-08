// src/main/java/com/spring/app/service/SurveyService_imple.java
package com.spring.app.service;

import com.spring.app.domain.*;
import com.spring.app.model.SurveyDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurveyService_imple implements SurveyService {

    private final SurveyDAO surveyDAO;

    @Override @Transactional
    public Long createSurveyWithTargets(SurveyDTO dto) {
        if (dto.getOwnerMemberSeq() == null) throw new IllegalArgumentException("ownerMemberSeq required");
        if (dto.getTitle() == null || dto.getTitle().isBlank()) throw new IllegalArgumentException("title required");
        LocalDate s = dto.getStartDate(), e = dto.getEndDate();
        if (s == null || e == null) throw new IllegalArgumentException("start/end required");
        if (e.isBefore(s)) throw new IllegalArgumentException("endDate must be >= startDate");
        if (dto.getStatusCode() == null) dto.setStatusCode("OPEN");

        int n = surveyDAO.insertSurvey(dto);
        if (n != 1) throw new IllegalStateException("insert failed");
        Long sid = dto.getSurveyId();

        List<SurveyTargetDTO> targets = new ArrayList<>();
        String tt = dto.getTargetType();
        if ("ALL".equals(tt)) {
            targets.add(SurveyTargetDTO.builder().surveyId(sid).targetType("ALL").build());
        } else if ("DEPT".equals(tt) && dto.getTargetDept() != null) {
            targets.add(SurveyTargetDTO.builder().surveyId(sid).targetType("DEPT").deptSeq(dto.getTargetDept()).build());
        }
        if (!targets.isEmpty()) surveyDAO.insertSurveyTargets(targets);

        return sid;
    }

    @Override public List<SurveyDTO> getVisibleSurveys(String status, Long memberSeq, Long deptSeq) {
        return surveyDAO.selectSurveysVisible(status, memberSeq, deptSeq);
    }

    @Override public SurveyDTO getSurvey(Long surveyId) {
        return surveyDAO.selectSurveyById(surveyId);
    }

    @Override public List<SurveyTargetDTO> getTargets(Long surveyId) {
        return surveyDAO.selectSurveyTargets(surveyId);
    }

    @Override public boolean canView(Long surveyId, Long memberSeq, Long deptSeq) {
        return surveyDAO.canViewSurvey(surveyId, memberSeq, deptSeq) > 0;
    }

    // ===== 응답/통계 =====
    @Override public boolean hasResponded(Long surveyId, Long memberSeq) {
        return surveyDAO.existsResponseByMember(surveyId, memberSeq) > 0;
    }

    @Override @Transactional
    public void submitResponse(Long surveyId, Long memberSeq, String answersJson) {
        // 1) 설문 상태/기간 확인
        SurveyDTO s = surveyDAO.selectSurveyById(surveyId);
        if (s == null) throw new IllegalArgumentException("설문이 존재하지 않습니다.");
        if (!"OPEN".equalsIgnoreCase(s.getStatusCode()))
            throw new IllegalStateException("마감된 설문입니다.");
        LocalDate today = LocalDate.now();
        if (today.isBefore(s.getStartDate()) || today.isAfter(s.getEndDate()))
            throw new IllegalStateException("응답 기간이 아닙니다.");

        // 2) 중복 응답 확인
        if (hasResponded(surveyId, memberSeq))
            throw new IllegalStateException("이미 응답함");

        // 3) 저장
        SurveyRespDTO dto = SurveyRespDTO.builder()
                .surveyId(surveyId)
                .respMemberSeq(memberSeq)
                .answersJson(answersJson)
                .build();
        try {
            surveyDAO.insertResponse(dto);
        } catch (DuplicateKeyException e) {
            // 동시제출 등 유니크 위반시
            throw new IllegalStateException("이미 응답함");
        }
    }

    @Override
    public SurveyStatsDTO getStats(Long surveyId) {
        SurveyDTO s = surveyDAO.selectSurveyById(surveyId);
        if (s == null) throw new IllegalArgumentException("설문이 존재하지 않습니다.");

        // 문항 정의
        List<Map<String,Object>> questions = parseQuestionsJson(s.getQuestionsJson());

        // 응답 JSON 목록
        List<String> answersList = surveyDAO.selectAnswersJsonList(surveyId);
        int total = answersList.size();

        // 문항별 집계 준비
        List<QuestionStatDTO> stats = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            Map<String,Object> q = questions.get(i);
            String type = String.valueOf(q.getOrDefault("type","TEXT")).toUpperCase();
            String title = String.valueOf(q.getOrDefault("title",""));
            LinkedHashMap<String,Integer> counts = new LinkedHashMap<>();
            if (!"TEXT".equals(type)) {
                List<?> opts = (List<?>) q.get("options");
                if (opts != null) {
                    for (Object o : opts) counts.put(String.valueOf(o), 0);
                }
            }
            stats.add(QuestionStatDTO.builder()
                    .index(i+1).type(type).title(title)
                    .counts(counts.isEmpty()?null:counts)
                    .responses(0)
                    .build());
        }

        // 집계 (모든 응답 누적)
        for (String ansJson : answersList) {
            Map<String,Object> ans = parseJsonToMap(ansJson);
            for (int i = 0; i < stats.size(); i++) {
                String key = "q" + (i+1);
                Object v = ans.get(key);
                QuestionStatDTO qs = stats.get(i);
                if (v == null) continue;
                qs.setResponses(qs.getResponses()+1);

                if ("SINGLE".equals(qs.getType())) {
                    String opt = String.valueOf(v);
                    bump(qs, opt);
                } else if ("MULTI".equals(qs.getType())) {
                    if (v instanceof Collection<?>) {
                        for (Object o : (Collection<?>) v) bump(qs, String.valueOf(o));
                    } else {
                        bump(qs, String.valueOf(v));
                    }
                }
            }
        }

        // ------- [추가] 요약카드 숫자 계산 -------
        int eligible = surveyDAO.countEligibleMembers(surveyId);
        int notAnswered = Math.max(0, eligible - total);

        return SurveyStatsDTO.builder()
                .surveyId(surveyId)
                .totalResponses(total)
                .questions(stats)
                .eligible(eligible)          // ★
                .notAnswered(notAnswered)    // ★
                .build();
    }

    // ===== helpers =====
    @SuppressWarnings("unchecked")
    private List<Map<String,Object>> parseQuestionsJson(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        Map<String,Object> parsed = parseJsonToMap(json);
        if (parsed != null && !parsed.isEmpty()) {
            return parsed.keySet().stream().sorted().map(k -> (Map<String,Object>) parsed.get(k)).collect(Collectors.toList());
        }
        // 배열형도 지원
        Object arr = parseJson(json);
        if (arr instanceof List<?>) return (List<Map<String,Object>>) arr;
        return Collections.emptyList();
    }

    private void bump(QuestionStatDTO qs, String opt) {
        if (qs.getCounts() == null) qs.setCounts(new LinkedHashMap<>());
        qs.getCounts().put(opt, qs.getCounts().getOrDefault(opt, 0) + 1);
    }

    // 아주 가벼운 JSON 파서 (Jackson/Gson이 프로젝트에 있다면 그걸 쓰세요)
    private Map<String,Object> parseJsonToMap(String json) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().readValue(json, Map.class);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
    private Object parseJson(String json) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().readValue(json, Object.class);
        } catch (Exception e) { return null; }
    }
    
    
}
