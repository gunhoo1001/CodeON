package com.spring.app.attendance.controller;

import com.spring.app.attendance.domain.AttendanceRecord;
import com.spring.app.attendance.domain.WorkSummary;
import com.spring.app.attendance.service.AttendanceService;
import com.spring.app.domain.MemberDTO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/member/")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter YMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @GetMapping("work")
    public String workPage(Model model,
                           HttpSession session,
                           @RequestParam(value = "month", required = false)
                           @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {	// URL 파라미터 month 값을 받음

        MemberDTO login = (MemberDTO) session.getAttribute("loginuser");	// 로그인 체크
        if (login == null) {
            return "redirect:/login/loginStart";
        }

        int memberSeq = login.getMemberSeq();	    // 로그인한 유저의 정보를 변수에 담음
        String userName = login.getMemberName();	// 로그인한 유저의 정보를 변수에 담음
        if (month == null) month = YearMonth.now();

        // 월별 근태 데이터 조회
        List<AttendanceRecord> list = attendanceService.getMonthly(memberSeq, month);
        
        // 누적근무시간, 근무일수, 연장근무 조회
        WorkSummary summary = attendanceService.getMonthlySummary(memberSeq, month);
        
        // 연차 조회
        var leave = attendanceService.getAnnualLeave(memberSeq);
        
        // JSP로 데이터 전달
        model.addAttribute("userName", userName);
        model.addAttribute("attendanceList", list);
        model.addAttribute("currentMonth", month);
        model.addAttribute("todayStr", LocalDate.now(ZONE).format(YMD));
        model.addAttribute("summary", summary);
        model.addAttribute("leave", leave);   
        return "member/work";
    }

    @PostMapping("startWork")
    public String startWork(HttpSession session) {
        MemberDTO login = (MemberDTO) session.getAttribute("loginuser");
        if (login == null) return "redirect:/login/loginStart";
        attendanceService.startWork(login.getMemberSeq());	// 출근하기 
        return "redirect:/member/work";
    }

    @PostMapping("endWork")
    public String endWork(HttpSession session) {
        MemberDTO login = (MemberDTO) session.getAttribute("loginuser");
        if (login == null) return "redirect:/login/loginStart";	// 퇴근하기 
        attendanceService.endWork(login.getMemberSeq());
        return "redirect:/member/work";
    }
}
