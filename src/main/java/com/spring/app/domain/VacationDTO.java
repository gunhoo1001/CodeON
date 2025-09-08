package com.spring.app.domain;

import java.time.LocalDate;

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
public class VacationDTO {
    private Long draftSeq;
    private String vacationTitle;
    private String vacationType;
    private LocalDate vacationStart;
    private LocalDate vacationEnd;
    private String vacationContent;
    private MemberDTO member; // 연차 신청 사원 정보
}
