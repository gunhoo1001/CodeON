package com.spring.app.attendance.model;

import com.spring.app.attendance.domain.AttendanceRecord;
import com.spring.app.domain.AnnualLeaveDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface AttendanceDAO {
    AttendanceRecord selectByMemberAndDate(int memberSeq, LocalDate workDate);
    int insertStart(int memberSeq, LocalDate workDate);
    int updateStartIfNull(int memberSeq, LocalDate workDate);
    int updateEnd(int memberSeq, LocalDate workDate, LocalDateTime endTime, Integer overtime);
    List<AttendanceRecord> selectMonthly(int memberSeq, YearMonth ym);
    Map<String,Object> selectMonthlySummary(int memberSeq, YearMonth ym);
    AnnualLeaveDTO selectAnnualLeaveByMemberSeq(int memberSeq);
}
