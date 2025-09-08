package com.spring.app.controller;

import com.spring.app.entity.Department;
import com.spring.app.entity.Grade;
import com.spring.app.service.HrAttendanceService;
import com.spring.app.domain.AttendanceRowView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@Controller
@RequestMapping("/member") // URL prefix: /member
@RequiredArgsConstructor
public class HrAttendanceController {

    private final HrAttendanceService hrAttendanceService;

    /** 전사 근태 관리 페이지 (뷰: /WEB-INF/views/member/attend.jsp) */
    @GetMapping("attend")
    public String attend(Model model,
                         @RequestParam(name = "month", required = false)
                         @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
                         @RequestParam(name = "dept", required = false) Long deptSeq,
                         @RequestParam(name = "grade", required = false) Long gradeSeq,
                         @RequestParam(name = "page", defaultValue = "1") int page) {

        // 기본 월: 이번 달
        if (month == null) month = YearMonth.now();

        // 월 네비게이션
        YearMonth prevMonth = month.minusMonths(1);
        YearMonth nextMonth = month.plusMonths(1);

        // 페이징 (UI는 1-base, PageRequest는 0-base)
        final int size = 20;
        PageRequest pageRequest = PageRequest.of(Math.max(page - 1, 0), size);

        // 데이터 조회
        Page<AttendanceRowView> rows = hrAttendanceService.getAttendancePage(month, deptSeq, gradeSeq, pageRequest);
        List<Department> departments = hrAttendanceService.getDepartments();
        List<Grade> grades = hrAttendanceService.getGrades();

        // JSP 사용 모델
        model.addAttribute("rows", rows.getContent());
        model.addAttribute("page", page);
        model.addAttribute("totalPages", rows.getTotalPages());

        model.addAttribute("departments", departments);
        model.addAttribute("grades", grades);
        model.addAttribute("selectedDept", deptSeq);
        model.addAttribute("selectedGrade", gradeSeq);

        model.addAttribute("currentMonth", month);
        model.addAttribute("prevMonth", prevMonth);
        model.addAttribute("nextMonth", nextMonth);

        return "member/attend";
    }
    
    @PostMapping("attend/downloadExcelFile")
    public String downloadExcelFile(
            @RequestParam(name = "month") @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM") java.time.YearMonth month,
            @RequestParam(name = "dept", required = false) Long deptSeq,      // Department.departmentSeq: Long
            @RequestParam(name = "grade", required = false) Integer gradeSeq, // Grade.gradeSeq: int → Integer
            org.springframework.ui.Model model) {

        hrAttendanceService.attendance_to_Excel(month, deptSeq, gradeSeq, model);
        return "excelDownloadView"; // ✅ 기존 ExcelView 구현체 사용
    }
}
