package com.spring.app.domain;

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
public class AnnualLeaveDTO {

    private int memberSeq; // PK이자 FK
    private int totalLeave;
    private int usedLeave;
    private int remainingLeave;
}
