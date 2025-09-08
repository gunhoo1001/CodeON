package com.spring.app.entity;

import java.time.LocalDate;

import com.spring.app.calendar.domain.CalendarDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="TBL_CALENDAR")
@Getter               // private 으로 설정된 필드 변수를 외부에서 접근하여 사용하도록 getter()메소드를 만들어 주는 것.
@Setter               // private 으로 설정된 필드 변수를 외부에서 접근하여 수정하도록 setter()메소드를 만들어 주는 것.
@AllArgsConstructor   // 모든 필드 값을 파라미터로 받는 생성자를 만들어주는 것
@NoArgsConstructor    // 파라미터가 없는 기본생성자를 만들어주는 것
@Builder			  // 생성자 대신, 필요한 값만 선택해서 체이닝 방식으로 객체를 만들 수 있게 해주는 것.
@ToString
public class Calendar {

	@Id
	@Column(name="calendar_seq", columnDefinition = "NUMBER")	// columnDefinition 은 DB 컬럼의 정보를 직접 주는 것이다. 예를 들어 columnDefinition = "Nvarchar2(20) default '사원'" 인 것이다. 
	@SequenceGenerator(name="SEQ_CALENDAR_GENERATOR", sequenceName = "seq_board", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CALENDAR_GENERATOR")
	private long calendarSeq;
	
    @Column(name = "fk_member_seq", nullable = false, updatable = false)
    private Long fkMemberSeq;

    @Column(name = "fk_big_category_seq", nullable = false)
    private Long fkBigCategorySeq;

    @Column(name = "fk_small_category_seq", nullable = false)
    private Long fkSmallCategorySeq;

    @Column(name = "calendar_start", nullable = false)
    private LocalDate calendarStart;

    @Column(name = "calendar_end", nullable = false)
    private LocalDate calendarEnd;

    @Column(name = "calendar_name", length = 100)
    private String calendarName;

    @Column(name = "calendar_content", length = 500)
    private String calendarContent;

    @Column(name = "calendar_color", length = 50)
    private String calendarColor;

    @Column(name = "calendar_location", length = 50)
    private String calendarLocation;

    @Column(name = "calendar_user", length = 50)
    private String calendarUser; // 공유 대상자 문자열(예: "user1,user2")

    // === 연관 관계 ===

    @ManyToOne
    @JoinColumn(name = "fk_member_seq", referencedColumnName = "member_seq", insertable = false, updatable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "fk_big_category_seq", referencedColumnName = "big_category_seq", insertable = false, updatable = false)
    private CalendarBigCategory bigCategory;

    @ManyToOne
    @JoinColumn(name = "fk_small_category_seq", referencedColumnName = "small_category_seq", insertable = false, updatable = false)
    private CalendarSmallCategory smallCategory;

    // === 등록일 자동 설정 ===
    @PrePersist
    public void prePersist() {
        if (this.calendarStart == null) this.calendarStart = LocalDate.now();
        if (this.calendarEnd == null) this.calendarEnd = LocalDate.now();
    }

    // === Entity → DTO 변환 ===
    public CalendarDTO toDTO() {
        return CalendarDTO.builder()
                .calendarSeq(this.calendarSeq)
                .fkMemberSeq(this.fkMemberSeq)
                .fkBigCategorySeq(this.fkBigCategorySeq)
                .fkSmallCategorySeq(this.fkSmallCategorySeq)
                .calendarStart(this.calendarStart.toString())
                .calendarEnd(this.calendarEnd.toString())
                .calendarName(this.calendarName)
                .calendarContent(this.calendarContent)
                .calendarColor(this.calendarColor)
                .calendarLocation(this.calendarLocation)
                .calendarUser(this.calendarUser)
                .member(this.member)
                .bigCategory(this.bigCategory)
                .smallCategory(this.smallCategory)
                .build();
    }

	
}
