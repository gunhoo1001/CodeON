package com.spring.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.spring.app.domain.DraftLineDTO;

@Entity
@Table(name = "TBL_DRAFT_LINE")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class DraftLine {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DRAFT_LINE_SEQ_GEN")
    @SequenceGenerator(
	    name="DRAFT_LINE_SEQ_GEN",
	    sequenceName="DRAFT_LINE_SEQ", // DB 실제 시퀀스명과 동일하게
	    allocationSize=1
	)
    
    @Column(name = "draft_line_seq", nullable = false)
    private Long draftLineSeq;

    // FK: TBL_DRAFT(draft_seq)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_draft_seq", nullable = false)
    private Draft draft;

    // FK: TBL_MEMBER(member_seq)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_member_seq", nullable = false)
    private Member approver;
    
    @Column(name = "line_order", nullable = false)
    private Integer lineOrder;

    // 숫자 상태값(0:대기, 1:승인, 9:반려 등)
    @Column(name = "sign_status")
    private Integer signStatus;

    @Column(name = "sign_comment", length = 100)
    private String signComment;

    @Column(name = "sign_date")
    private LocalDateTime signDate;
    
    private static Long toLong(Number n) { return n == null ? null : n.longValue(); }

    public static DraftLineDTO toDto(DraftLine e){
        if (e == null) return null;
        return DraftLineDTO.builder()
            .draftLineSeq(e.getDraftLineSeq())                                  // 이미 Long
            .fkDraftSeq(e.getDraft()    != null ? toLong(e.getDraft().getDraftSeq())     : null)
            .fkMemberSeq(e.getApprover() != null ? toLong(e.getApprover().getMemberSeq()) : null)
            .lineOrder(e.getLineOrder())
            .signStatus(e.getSignStatus())
            .signComment(e.getSignComment())
            .signDate(e.getSignDate())
            .build();
    }
    
}

