package com.spring.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.app.domain.MemberDTO;
import com.spring.app.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/login/")
@RequiredArgsConstructor
public class LoginController {

    private final MemberService memberService;

    /** 로그인 페이지 이동 */
    @GetMapping("loginStart")
    public String login() {
        return "login"; 
        // /WEB-INF/views/login.jsp
    }

    /** 로그인 처리 */
    @PostMapping("loginEnd")
    public String loginEnd(@RequestParam(name = "memberUserId") String memberUserId,
                           @RequestParam(name = "memberPwd") String memberPwd,
                           HttpServletRequest request) {

        // DB에서 사용자 조회
        MemberDTO mbrDto = memberService.getMemberByUserId(memberUserId);

        if (mbrDto == null || !memberPwd.equals(mbrDto.getMemberPwd())) {
            String message = "로그인 실패!!";
            String loc = request.getContextPath() + "/login/loginStart"; 

            request.setAttribute("message", message);
            request.setAttribute("loc", loc);
            return "msg";
        }

        // 세션에 로그인 사용자 정보 저장
        HttpSession session = request.getSession();
        session.setAttribute("loginuser", mbrDto);

        // 디버깅 로그 추가
        //System.out.println(">>> 로그인 성공");
       //System.out.println("세션에 저장된 loginuser: " + session.getAttribute("loginuser"));
        //System.out.println("사용자 이름: " + mbrDto.getMemberName());
       // System.out.println("사용자 아이디: " + mbrDto.getMemberUserid());

        return "redirect:" + request.getContextPath() + "/index"; 
    }


    /** 로그아웃 처리 */
    @GetMapping("logout")
    public String logout(HttpServletRequest request) {

        HttpSession session = request.getSession();
        session.invalidate();

        String message = "로그아웃 되었습니다.";
        String loc = request.getContextPath() + "/";  

        request.setAttribute("message", message);
        request.setAttribute("loc", loc);
        return "msg";
    }
}
