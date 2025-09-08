package com.spring.app.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spring.app.domain.MemberDTO;          // 세션에 들어있는 타입
import com.spring.app.domain.MemberProfileDTO;   // 조회+수정 겸용 DTO
import com.spring.app.entity.Department;
import com.spring.app.service.MyPageService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;
    
    // ✅ email 필드는 바인딩 자체를 금지
    @InitBinder
    void initBinder(org.springframework.web.bind.WebDataBinder binder) {
        binder.setDisallowedFields("email");
        // 또는 binder.setAllowedFields("memberSeq","name","mobile");  // 화이트리스트 방식
    }

    /** 마이페이지 조회 */
    @GetMapping("")
    public String main(Model model,
                       @SessionAttribute(name = "loginuser", required = false) MemberDTO loginuser,
                       HttpServletRequest request,
                       RedirectAttributes ra) {

        if (loginuser == null) {
            ra.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:" + request.getContextPath() + "/login/loginStart";
        }

        Integer loginMemberSeq = loginuser.getMemberSeq();

        // 프로필 정보 조회
        MemberProfileDTO profile = myPageService.getProfile(loginMemberSeq);
        model.addAttribute("profile", profile);

        // 부서 목록 조회
        List<Department> departments = myPageService.getDepartments();
        model.addAttribute("departments", departments);
        model.addAttribute("profileDeptName", profile.getDeptName());

        return "mypage/main";
    }

    /** 개인정보 수정 (이름/이메일/휴대폰) */
    @PostMapping("/update")
    public String update(@ModelAttribute MemberProfileDTO form,
                         @SessionAttribute(name = "loginuser", required = false) MemberDTO loginuser,
                         HttpServletRequest request,
                         RedirectAttributes ra,
                         Model model) {

        if (loginuser == null) {
            ra.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:" + request.getContextPath() + "/login/loginStart";
        }
        Integer loginMemberSeq = loginuser.getMemberSeq();

        try {
        	// 정보 수정
            myPageService.updateProfile(loginMemberSeq, form);
            ra.addFlashAttribute("message", "수정 완료되었습니다.");
            return "redirect:" + request.getContextPath() + "/mypage";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            // 실패 시 다시 폼 랜더링을 위한 데이터 재주입
            MemberProfileDTO profile = myPageService.getProfile(loginMemberSeq);
            model.addAttribute("profile", profile);

            List<Department> departments = myPageService.getDepartments();
            model.addAttribute("departments", departments);
            model.addAttribute("profileDeptName", profile.getDeptName());
            model.addAttribute("message", ex.getMessage());

            return "mypage/main";
        }
    }
    
    @GetMapping("/password")
    public String passwordPage(
            @SessionAttribute(name = "loginuser", required = false) com.spring.app.domain.MemberDTO loginuser,
            HttpServletRequest request,
            RedirectAttributes ra) {

        if (loginuser == null) {
            ra.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:" + request.getContextPath() + "/login/loginStart";
        }
        return "mypage/password";
    }
    
    @PostMapping("/password")
    public String changePassword(@RequestParam("currentPwd") String currentPwd,
    							 @RequestParam("newPwd") String newPwd,
    							 @RequestParam("newPwdConfirm") String newPwdConfirm,
                                 @SessionAttribute(name = "loginuser", required = false) com.spring.app.domain.MemberDTO loginuser,
                                 HttpServletRequest request,
                                 RedirectAttributes ra) {

        if (loginuser == null) {
            ra.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:" + request.getContextPath() + "/login/loginStart";
        }
        if (!newPwd.equals(newPwdConfirm)) {
            ra.addFlashAttribute("message", "새 비밀번호와 확인이 일치하지 않습니다.");
            ra.addFlashAttribute("error", true);
            return "redirect:" + request.getContextPath() + "/mypage/password";
        }
        try {
        	// 비밀번호 변경
            myPageService.changePassword(loginuser.getMemberSeq(), currentPwd, newPwd);

            // 선택: 보안 강화를 위해 세션 재생성(재로그인 유도)
            request.getSession().invalidate();

            ra.addFlashAttribute("message", "비밀번호가 변경되었습니다. 다시 로그인해주세요.");
            return "redirect:" + request.getContextPath() + "/login/loginStart";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            ra.addFlashAttribute("error", true);
            return "redirect:" + request.getContextPath() + "/mypage/password";
        }
    }
}
