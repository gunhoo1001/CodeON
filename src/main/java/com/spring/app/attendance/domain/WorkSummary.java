package com.spring.app.attendance.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WorkSummary {
    private int totalMinutes;        // 월 누적 근무 분
    private String totalMinutesStr;  // "HH:mm" 포맷
    private int workDays;            // 근무일수(출근·퇴근 모두 있는 날의 개수)
    private int totalOvertime;       // 월 누적 OT 분
}

