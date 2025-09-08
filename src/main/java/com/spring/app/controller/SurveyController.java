// src/main/java/com/spring/app/controller/SurveyController.java
package com.spring.app.controller;

import com.spring.app.domain.MemberDTO;
import com.spring.app.domain.MemberProfileDTO;
import com.spring.app.domain.SurveyDTO;
import com.spring.app.service.MyPageService;
import com.spring.app.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/survey")
@RequiredArgsConstructor
public class SurveyController {

    private final MyPageService myPageService;
    private final SurveyService surveyService;

    @GetMapping
    public String index() {
        return "redirect:/survey/main";	// 리다이렉트
    }
    // 설문 메인 화면
    @GetMapping("/main")
    public String main(Model model,
                       @SessionAttribute(name = "loginuser", required = false) MemberDTO loginuser) {

        if (loginuser != null) {
        	// 로그인한 사용자 프로필 조회 (부서, 직급 등)
            MemberProfileDTO profile = myPageService.getProfile(loginuser.getMemberSeq());
            model.addAttribute("userName",  loginuser.getMemberName());
            model.addAttribute("gradeName", profile != null ? profile.getGradeName() : "-");
            model.addAttribute("deptName",  profile != null ? profile.getDeptName()  : "-");

          
            Long memberSeq = (long) loginuser.getMemberSeq();
            Long deptSeq   = (long) loginuser.getFkDepartmentSeq();
            List<SurveyDTO> list = surveyService.getVisibleSurveys(null, memberSeq, deptSeq);
            model.addAttribute("surveys", list);
        } else {
        	// 로그인하지 않은 경우 설문 목록은 비움
            model.addAttribute("surveys", Collections.emptyList());
        }
        return "survey/main"; // /WEB-INF/views/survey/main.jsp
    }
}
