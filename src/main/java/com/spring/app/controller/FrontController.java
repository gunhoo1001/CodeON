package com.spring.app.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spring.app.attendance.domain.AttendanceRecord;
import com.spring.app.attendance.service.AttendanceService;
import com.spring.app.board.domain.BoardDTO;
import com.spring.app.board.service.BoardService;
import com.spring.app.calendar.domain.CalendarAjaxDTO;
import com.spring.app.calendar.service.CalendarService;
import com.spring.app.domain.MemberDTO;
import com.spring.app.domain.MemberProfileDTO;
import com.spring.app.entity.DraftLine;
import com.spring.app.model.DraftLineRepository;
import com.spring.app.service.MyPageService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class FrontController {

    private final AttendanceService attendanceService;
    private final MyPageService myPageService;
    private final BoardService boardService;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private final DraftLineRepository draftLineRepository;
    private final CalendarService calendarService;
    
    @GetMapping("")
    public String start() {
        return "redirect:/login/loginStart";
    }

    @GetMapping("index")
    public String index(Model model,
                        @SessionAttribute(name = "loginuser", required = false) MemberDTO loginuser,
                        HttpServletRequest request,
                        RedirectAttributes ra) {

        if (loginuser == null) {
            ra.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:" + request.getContextPath() + "/login/loginStart";
        }

        int memberSeq = loginuser.getMemberSeq();
        String userName = loginuser.getMemberName();
        int userDeptSeq = loginuser.getFkDepartmentSeq();

        MemberProfileDTO profile = myPageService.getProfile(memberSeq);
        String gradeName = (profile != null && profile.getGradeName() != null) ? profile.getGradeName() : "-";

        LocalDate today = LocalDate.now(KST);

        // ✅ 오늘 사내 일정 가져오기
        List<CalendarAjaxDTO> todayCompanyEvents = calendarService.getTodayCompanyEvents(today);
        model.addAttribute("todayCompanyEvents", todayCompanyEvents);

        // 근태 정보
        List<AttendanceRecord> monthList = attendanceService.getMonthly(memberSeq, YearMonth.now());
        AttendanceRecord todayRec = monthList.stream()
                .filter(r -> today.toString().equals(r.getWorkDateStr()))
                .findFirst()
                .orElse(null);

        String startTimeStr = (todayRec != null && todayRec.getStartTimeStr() != null) ? todayRec.getStartTimeStr() : "-";
        String endTimeStr   = (todayRec != null && todayRec.getEndTimeStr() != null) ? todayRec.getEndTimeStr()   : "-";

        model.addAttribute("userName", userName);
        model.addAttribute("gradeName", gradeName);
        model.addAttribute("todayStr", today.toString());
        model.addAttribute("startTimeStr", startTimeStr);
        model.addAttribute("endTimeStr", endTimeStr);
        model.addAttribute("attendanceList", monthList);

        // 결재 대기 문서
        List<DraftLine> inbox = draftLineRepository.findInbox((long) memberSeq);
        if (inbox.size() > 5) inbox = inbox.subList(0, 5);
        model.addAttribute("pendingLines", inbox);

        // 최근공지
        List<BoardDTO> noticeList = boardService.selectRecentNotices(userDeptSeq, 5);
        model.addAttribute("noticeList", noticeList);

        return "index"; // /WEB-INF/views/index.jsp
    }

}

