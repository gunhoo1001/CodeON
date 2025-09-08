package com.spring.app.calendar.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.spring.app.calendar.domain.CalendarAjaxDTO;
import com.spring.app.calendar.domain.CalendarDTO;

import lombok.RequiredArgsConstructor;


@Repository
@RequiredArgsConstructor
public class CalendarDAO_imple implements CalendarDAO {

	
	@Qualifier("sqlsession")
	private final SqlSessionTemplate sqlsession;

	// 등록 이벤트
	@Override
	public int addCalendarEvent(Map<String, Object> paraMap) {
		
			int n = sqlsession.insert("calendar.addCalendarEvent", paraMap); 
		
		return n; 
	}

	// 등록된 캘린더를 화면에 보여주는 거 만들자~ //
	@Override
	public List<CalendarAjaxDTO> selectCalendar(String calendarUser) {
		List<CalendarAjaxDTO> calendarList = sqlsession.selectList("calendar.selectCalendar", calendarUser);
		return calendarList;
	}
	
	// 일정 상세보기 ㄱㄱ
//	@Override
//	public Map<String, String> detailCalendar(String calendarSeq) {
//		Map<String, String> map = sqlsession.selectOne("calendar.detailCalendar" , calendarSeq);
//		return map;
//	}

	// 일정 상세보기 ㄱㄱ
	@Override
	public Map<String, String> detailCalendar(int calendarSeq) {
		Map<String, String> map = sqlsession.selectOne("calendar.detailCalendar" , calendarSeq);
		return map;
	}
	
	// 일정 삭제하기
	@Override
	public int deleteCalendar(String calendarSeq) throws Throwable {
		int n = sqlsession.delete("calendar.deleteCalendar", calendarSeq);
		return n;
	}

	// 일정 수정하기
	@Override
	public int editCalendar_end(CalendarDTO cvo) throws Throwable {
		int n = sqlsession.update("calendar.editCalendar_end", cvo);
		return n;
	}

	// 부서별 조회기능
	@Override
	public List<CalendarAjaxDTO> selectDeptCalendar(int fkDepartmentSeq) {
	    return sqlsession.selectList("calendar.selectDeptCalendar", fkDepartmentSeq);
	}

	// 사내일정 저회
	@Override
	public List<CalendarAjaxDTO> selectCompanyCalendar() {
	    return sqlsession.selectList("calendar.selectCompanyCalendar");
	}

	@Override
	public List<CalendarAjaxDTO> getTodayCompanyEvents(LocalDate today) {
		return sqlsession.selectList("calendar.getTodayCompanyEvents", today);
	}

	

	
	
	


	




	
}
