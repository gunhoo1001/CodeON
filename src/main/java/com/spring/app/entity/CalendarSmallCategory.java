package com.spring.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TBL_CALENDAR_SMALL_CATEGORY")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class CalendarSmallCategory {

    @Id
    @Column(name = "small_category_seq")
    private Long smallCategorySeq;

    @Column(name = "fk_calendar_seq", nullable = false)
    private Long fkCalendarSeq;

    @Column(name = "fk_member_seq", nullable = false)
    private Long fkMemberSeq;

    @Column(name = "small_category_name", nullable = false, length = 100)
    private String smallCategoryName;

    // === 연관관계 설정 ===

    @ManyToOne
    @JoinColumn(name = "fk_calendar_seq", referencedColumnName = "big_category_seq", insertable = false, updatable = false)
    private CalendarBigCategory bigCategory;

    @ManyToOne
    @JoinColumn(name = "fk_member_seq", referencedColumnName = "member_seq", insertable = false, updatable = false)
    private Member member;
}
