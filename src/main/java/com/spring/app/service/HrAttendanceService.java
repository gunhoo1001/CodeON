// src/main/java/com/spring/app/hr/service/HrAttendanceService.java
package com.spring.app.service;

import com.spring.app.entity.Department;
import com.spring.app.entity.Grade;
import com.spring.app.domain.AttendanceRowView;
import org.springframework.data.domain.*;
import org.springframework.ui.Model;

import java.time.YearMonth;
import java.util.List;

public interface HrAttendanceService {
    Page<AttendanceRowView> getAttendancePage(YearMonth month, Long deptSeq, Long gradeSeq, Pageable pageable);
    List<Department> getDepartments();
    List<Grade> getGrades();
	void attendance_to_Excel(YearMonth month, Long deptSeq, Integer gradeSeq, Model model);
}
