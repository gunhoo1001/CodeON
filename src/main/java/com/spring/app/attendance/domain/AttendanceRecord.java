package com.spring.app.attendance.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter @Setter @ToString
public class AttendanceRecord {
 private Long attendanceSeq;		// 근태번호
 private Integer memberSeq;			// 사원번호
 private LocalDate workDate;		// 근무일
 private LocalDateTime startTime;	// 출근시간
 private LocalDateTime endTime;		// 퇴근시간
 private Integer overtime;			// 연장근무시간
 
 private Integer workedMinutes; // 분 단위(근무시간계산용)
 private String workedTimeStr;  // "HH:mm" 포맷 문자열(근무시간계산용)

 private static final DateTimeFormatter D = DateTimeFormatter.ofPattern("yyyy-MM-dd");
 private static final DateTimeFormatter T = DateTimeFormatter.ofPattern("HH:mm:ss");

 public String getWorkDateStr() {
     return workDate != null ? workDate.format(D) : null;
 }
 public String getStartTimeStr() {
     return startTime != null ? startTime.format(T) : null;
 }
 public String getEndTimeStr() {
     return endTime != null ? endTime.format(T) : null;
 }
}
