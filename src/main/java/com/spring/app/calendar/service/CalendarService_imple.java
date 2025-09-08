package com.spring.app.calendar.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.spring.app.calendar.domain.CalendarAjaxDTO;
import com.spring.app.calendar.domain.CalendarDTO;
import com.spring.app.calendar.model.CalendarDAO;


@Service
public class CalendarService_imple implements CalendarService {


	    private final CalendarDAO dao;

	    // 생성자 주입
	    public CalendarService_imple(CalendarDAO dao) {
	        this.dao = dao;
	    }

	    //////////////////////////////////////////////////////////////
	    
	    
	    // === 일정 등록 처리 ===
	    @Override
	    public int addCalendarEvent(Map<String, Object> paraMap) {
	    	return dao.addCalendarEvent(paraMap); 
	    	
	    }

	    // 등록된 캘린더를 화면에 보여주는 거 만들자~ //
		@Override
		public List<CalendarAjaxDTO> selectCalendar(String calendarUser) {
			List<CalendarAjaxDTO> calendarList = dao.selectCalendar(calendarUser);
			return calendarList;
		}

		// === 일정 상세보기 ===
//		@Override
//		public Map<String, String> detailCalendar(String calendarSeq) {
//			Map<String, String> map = dao.detailCalendar(calendarSeq);
//			return map;
//		}

		// === 일정 상세보기 ===
		@Override
		public Map<String, String> detailCalendar(int calendarSeq) {
			Map<String, String> map = dao.detailCalendar(calendarSeq);
			return map;
		}

		// === 일정삭제하기 ===
		@Override
		public int deleteCalendar(String calendarSeq) throws Throwable {
			int n = dao.deleteCalendar(calendarSeq);
			return n;
		}

		// 일정 수정하기
		@Override
		public int editCalendar_end(CalendarDTO cvo) throws Throwable{
			int n = dao.editCalendar_end(cvo);
			return n;
		}

		// 부서별 조회기능
		@Override
		public List<CalendarAjaxDTO> selectDeptCalendar(int fkDepartmentSeq) {
		    return dao.selectDeptCalendar(fkDepartmentSeq);
		}

		// 사내일정 조회
		@Override
		public List<CalendarAjaxDTO> selectCompanyCalendar() {
		    return dao.selectCompanyCalendar();
		}

		//<!-- 오늘 사내 일정 조회 -->
		@Override
	    public List<CalendarAjaxDTO> getTodayCompanyEvents(LocalDate today) {
	        return dao.getTodayCompanyEvents(today);
	    }

		



		
		


}
	


