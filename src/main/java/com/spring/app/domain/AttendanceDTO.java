package com.spring.app.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttendanceDTO {
    private Long attendanceSeq;
    private MemberDTO member;
    private LocalDate workDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer overtime; // minutes
}

