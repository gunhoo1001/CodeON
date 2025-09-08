package com.spring.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TBL_DRAFT")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Draft {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DRAFT_SEQ_GEN")
	@SequenceGenerator(
	    name = "DRAFT_SEQ_GEN",
	    sequenceName = "DRAFT_SEQ", 
	    allocationSize = 1
	)
	@Column(name = "draft_seq")
	private Long draftSeq;

    // FK: TBL_DRAFT_TYPE(draft_type_seq)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_draft_type_seq", nullable = false)
    private DraftType draftType;

    // FK: TBL_MEMBER(member_seq)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_member_seq", nullable = false)
    private Member member;  // 프로젝트의 Member 엔티티 사용

    @Column(name = "draft_title", nullable = false, length = 200)
    private String draftTitle;

    @Lob
    @Column(name = "draft_content", nullable = false, columnDefinition = "CLOB")
    private String draftContent;

    @Column(name = "draft_status")
    private Integer draftStatus;        // DEFAULT 0 (DB 기본값 사용)

    @Column(name = "is_emergency")
    private Integer isEmergency;        // DEFAULT 0 (DB 기본값 사용)

    @Column(name = "draft_regdate", insertable = false, updatable = false)
    private LocalDateTime draftRegdate; // DEFAULT SYSDATE (DB에서 채우도록)
}
