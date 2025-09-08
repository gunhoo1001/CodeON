package com.spring.app.calendar.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.spring.app.calendar.domain.CalendarAjaxDTO;
import com.spring.app.calendar.domain.CalendarDTO;

public interface CalendarService {

	// === 일정 등록 처리 ===
	int addCalendarEvent(Map<String, Object> paraMap);

	// 등록된 캘린더를 화면에 보여주는 거 만들자~ //
	List<CalendarAjaxDTO> selectCalendar(String fk_userid);

	// === 일정 상세보기 ===
	// Map<String, String> detailCalendar(String calendarSeq);

	// === 일정 상세보기 ===
	Map<String, String> detailCalendar(int calendarSeq);

	// === 일정삭제하기 ===
	int deleteCalendar(String calendarSeq) throws Throwable;

	// 일정 수정하기
	int editCalendar_end(CalendarDTO cvo) throws Throwable;

	// 부서별 조회기능
	List<CalendarAjaxDTO> selectDeptCalendar(int fkDepartmentSeq);

	// 사내일정 조회
	List<CalendarAjaxDTO> selectCompanyCalendar();

	// 메인화면에 일정 넣기.
	List<CalendarAjaxDTO> getTodayCompanyEvents(LocalDate today);

	
	

	




}
