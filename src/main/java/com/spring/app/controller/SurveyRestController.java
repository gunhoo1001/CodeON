// src/main/java/com/spring/app/controller/SurveyRestController.java
package com.spring.app.controller;

import com.spring.app.domain.*;
import com.spring.app.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/surveys")
@RequiredArgsConstructor
public class SurveyRestController {

    private final SurveyService surveyService;

    @GetMapping
    public ResponseEntity<List<SurveyDTO>> list(
            @SessionAttribute(name = "loginuser", required = false) com.spring.app.domain.MemberDTO loginuser,
            @RequestParam(value = "status", required = false) String status
    ) {
        if (loginuser == null) return ResponseEntity.status(401).build();
        Long memberSeq = (long) loginuser.getMemberSeq();
        Long deptSeq   = (long) loginuser.getFkDepartmentSeq();
        return ResponseEntity.ok(surveyService.getVisibleSurveys(status, memberSeq, deptSeq));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(
            @SessionAttribute(name="loginuser", required=false) com.spring.app.domain.MemberDTO loginuser,
            @PathVariable("id") Long id) {
        if (loginuser == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");
        Long memberSeq = (long) loginuser.getMemberSeq();
        Long deptSeq   = (long) loginuser.getFkDepartmentSeq();
        if (!surveyService.canView(id, memberSeq, deptSeq))
            return ResponseEntity.status(403).body("대상자만 열람 가능합니다.");

        SurveyDTO s = surveyService.getSurvey(id);
        boolean answered = surveyService.hasResponded(id, memberSeq);

        // 응답 가능 여부 플래그 채워서 보내기
        boolean canAnswer =
            "OPEN".equalsIgnoreCase(s.getStatusCode()) &&
            !answered &&
            !java.time.LocalDate.now().isBefore(s.getStartDate()) &&
            !java.time.LocalDate.now().isAfter(s.getEndDate());

        s.setAnswered(answered);
        s.setCanAnswer(canAnswer);
        if (!canAnswer && !answered) {
            if (!"OPEN".equalsIgnoreCase(s.getStatusCode())) s.setNotAllowedReason("마감됨");
            else s.setNotAllowedReason("기간 외");
        }
        return ResponseEntity.ok(s);
    }

    @GetMapping("/{id}/targets")
    public ResponseEntity<?> targets(
            @SessionAttribute(name="loginuser", required=false) com.spring.app.domain.MemberDTO loginuser,
            @PathVariable("id") Long id) {
        if (loginuser == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");
        Long memberSeq = (long) loginuser.getMemberSeq();
        Long deptSeq   = (long) loginuser.getFkDepartmentSeq();
        if (!surveyService.canView(id, memberSeq, deptSeq))
            return ResponseEntity.status(403).body("대상자만 열람 가능합니다.");
        return ResponseEntity.ok(surveyService.getTargets(id));
    }

    @PostMapping
    public ResponseEntity<?> create(
            @SessionAttribute(name = "loginuser", required = false) com.spring.app.domain.MemberDTO loginuser,
            @RequestBody SurveyDTO body) {
        if (loginuser == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");
        body.setOwnerMemberSeq((long) loginuser.getMemberSeq());
        Long id = surveyService.createSurveyWithTargets(body);
        return ResponseEntity.created(URI.create("/api/surveys/" + id)).body(id);
    }

    // ===== 응답 제출 =====
    @PostMapping("/{id}/responses")
    public ResponseEntity<?> submitResponse(
            @SessionAttribute(name="loginuser", required=false) com.spring.app.domain.MemberDTO loginuser,
            @PathVariable("id") Long id,
            @RequestBody Map<String, String> req) {
        if (loginuser == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");
        Long memberSeq = (long) loginuser.getMemberSeq();
        String answersJson = req.get("answersJson");
        if (answersJson == null || answersJson.isBlank())
            return ResponseEntity.badRequest().body("answersJson required");

        surveyService.submitResponse(id, memberSeq, answersJson);
        return ResponseEntity.ok().build();
    }

    // ===== 통계 =====
    @GetMapping("/{id}/stats")
    public ResponseEntity<?> stats(
            @SessionAttribute(name="loginuser", required=false) com.spring.app.domain.MemberDTO loginuser,
            @PathVariable("id") Long id) {
        if (loginuser == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");
        SurveyStatsDTO stats = surveyService.getStats(id);
        return ResponseEntity.ok(stats);
    }
    
}
