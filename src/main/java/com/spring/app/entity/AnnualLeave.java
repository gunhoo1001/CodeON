package com.spring.app.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.spring.app.domain.AnnualLeaveDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TBL_ANNUAL_LEAVE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnualLeave {

    @Id
    @Column(name = "member_seq")
    private int memberSeq;   // PK이자 FK

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId   //  PK를 FK로 공유
    @JoinColumn(name = "member_seq")
    @JsonIgnore // 이 필드를 JSON 직렬화에서 제외
    private Member member;

    @Column(name = "total_leave", nullable = false)
    private int totalLeave;

    @Column(name = "used_leave", nullable = false)
    private int usedLeave;

    @Column(name = "remaining_leave", nullable = false)
    private int remainingLeave;
    
    public AnnualLeaveDTO toDTO() {
    	return AnnualLeaveDTO.builder()
    			.memberSeq(this.memberSeq)
    			.totalLeave(this.totalLeave)
    			.usedLeave(this.usedLeave)
    			.remainingLeave(this.remainingLeave)
    			.build();
    }
}
