package com.spring.app.calendar.domain;

import com.spring.app.entity.CalendarBigCategory;
import com.spring.app.entity.CalendarSmallCategory;
import com.spring.app.entity.Member;

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
public class CalendarDTO {
	
	private long calendarSeq;
	private long fkMemberSeq;
	private long fkBigCategorySeq;
	private long fkSmallCategorySeq;
	
	private String calendarStart;     
    private String calendarEnd;

    private String calendarName;
    private String calendarContent;
    private String calendarColor;
    private String calendarType;
    private String calendarLocation;
    private String calendarUser;   

    // 연관 객체
    private Member member;
    private CalendarBigCategory bigCategory;
    private CalendarSmallCategory smallCategory;
	
    
}
