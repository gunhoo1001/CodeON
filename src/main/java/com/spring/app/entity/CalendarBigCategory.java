package com.spring.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TBL_CALENDAR_BIG_CATEGORY")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class CalendarBigCategory {

    @Id
    @Column(name = "big_category_seq")
    private Long bigCategorySeq;

    @Column(name = "big_category_name", nullable = false, length = 100)
    private String bigCategoryName;
}
