package com.spring.app.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TBL_SIGNLINE_MEMBER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignlineMember {

	  @Id
	  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "signlineMemberSeqGen")
	  @SequenceGenerator(name = "signlineMemberSeqGen", sequenceName = "SIGNLINE_MEMBER_SEQ", allocationSize = 1)
	  @Column(name = "SIGNLINE_MEMBER_SEQ")
	  private Long signlineMemberSeq;

	  // ★ 부모 FK — 반드시 "쓰기 가능"이어야 함 (insertable/updatable 기본값 true)
	  @ManyToOne(fetch = FetchType.LAZY)
	  @JoinColumn(name = "FK_SIGNLINE_SEQ", nullable = false)
	  private Signline signline;

	  // ★ 결재자 FK — 스키마 컬럼과 정확히 일치
	  @Column(name = "FK_MEMBER_SEQ", nullable = false)
	  private Integer fkMemberSeq;

	  @Column(name = "LINE_ORDER", nullable = false)
	  private Integer lineOrder;

	  // (조회 전용) 회원 연관 — 같은 컬럼 공유하므로 읽기 전용
	  @ManyToOne(fetch = FetchType.LAZY)
	  @JoinColumn(name = "FK_MEMBER_SEQ", referencedColumnName = "MEMBER_SEQ",
	              insertable = false, updatable = false)
	  private Member member;
    
    public SignlineMember toDTO() {
        return SignlineMember.builder()
                .signline(this.signline)
                .fkMemberSeq(this.fkMemberSeq)
                .lineOrder(this.lineOrder)
                .build();
    }
    
}
