package com.spring.app.calendar.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.spring.app.calendar.domain.BigCategoryDTO;
import com.spring.app.calendar.domain.CalendarAjaxDTO;
import com.spring.app.calendar.domain.CalendarDTO;
import com.spring.app.calendar.domain.SmallCategoryDTO;
import com.spring.app.calendar.service.CalendarService;
import com.spring.app.calendar.service.CategoryService;
import com.spring.app.common.MyUtil;
import com.spring.app.domain.MemberDTO;
import com.spring.app.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/Calendar")
public class CalendarController {

    private final CalendarService service;
    private final CategoryService categoryService;

    @Autowired
    public CalendarController(CalendarService service, CategoryService categoryService) {
        this.service = service;
        this.categoryService = categoryService;
    }

    // === 일정 목록 페이지 ===
    @GetMapping("/list")
    public String calendarList() {
        return "Calendar/list";
    }

    
    // === 일정 등록 폼 ===
    // === 일정 등록 폼 ===
    @GetMapping("/addCalendarForm")
    public ModelAndView showAddCalendarForm(ModelAndView mav, HttpServletRequest request) {
        HttpSession session = request.getSession();
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginuser");

        // 로그인 체크
        if (loginUser == null) {
            mav.addObject("message", "로그인이 필요합니다.");
            mav.addObject("loc", request.getContextPath() + "/login/loginStart");
            mav.setViewName("msg");
            return mav;
        }

        // URL 파라미터 bigCategorySeq 확인
        String bigCategorySeq = request.getParameter("bigCategorySeq");

        // ✅ 사내 일정 등록 제한 (1 = 사내 캘린더 → 인사팀만 가능)
        if ("1".equals(bigCategorySeq) && loginUser.getFkDepartmentSeq() != 10) {
            mav.addObject("message", "사내 일정은 인사팀만 등록할 수 있습니다.");
            mav.addObject("loc", request.getContextPath() + "/Calendar/list");
            mav.setViewName("msg");
            return mav;
        }

        // ✅ 부서 일정 등록 제한 (2 = 부서 캘린더 → 과장 이상만 가능)
        if ("2".equals(bigCategorySeq) && loginUser.getFkGradeSeq() < 3) {
            mav.addObject("message", "부서 일정은 과장급 이상만 등록 가능합니다.");
            mav.addObject("loc", request.getContextPath() + "/Calendar/list");
            mav.setViewName("msg");
            return mav;
        }

        // === 전체 대분류/소분류 조회 ===
        List<BigCategoryDTO> bigCategoryList = categoryService.getAllBigCategories();
        List<SmallCategoryDTO> smallCategoryList = categoryService.getAllSmallCategories();

        // ✅ 여기서 필터링
        if (bigCategorySeq != null && !bigCategorySeq.isEmpty()) {
            int seq = Integer.parseInt(bigCategorySeq);
            bigCategoryList.removeIf(cat -> cat.getBigCategorySeq() != seq);
        }

        mav.addObject("bigCategoryList", bigCategoryList);
        mav.addObject("smallCategoryList", smallCategoryList);
        mav.addObject("selectedBigCategory", bigCategorySeq);

        mav.setViewName("Calendar/addCalendarForm");
        return mav;
    }





    // === 일정 등록 처리하기 === // 
    @PostMapping("/addCalendarForm")
    public ModelAndView addCalendarEvent(ModelAndView mav, HttpServletRequest request) {
        HttpSession session = request.getSession();
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginuser");

        if (loginUser == null) {
            mav.addObject("message", "로그인이 필요합니다.");
            mav.addObject("loc", request.getContextPath() + "/login/loginStart");
            mav.setViewName("msg");
            return mav;
        }

        try {
            // 파라미터 가져오기
            String memberSeq = String.valueOf(loginUser.getMemberSeq());
            String startDate = request.getParameter("startDate");
            String endDate = request.getParameter("endDate");
            String title = request.getParameter("title");
            String calendarType = request.getParameter("calendarType");
            String shareTargets = request.getParameter("shareEmployees");
            String calendarLocation = request.getParameter("calendarLocation");
            String content = request.getParameter("content");
            String bigCategorySeq = request.getParameter("bigCategorySeq");
            String smallCategorySeq = request.getParameter("smallCategorySeq");
            
            
            
            
            // 필수 체크
            if (startDate == null || startDate.isEmpty() || endDate == null || endDate.isEmpty()) {
                throw new IllegalArgumentException("시작일과 종료일을 입력하세요.");
            }
            if (bigCategorySeq == null || bigCategorySeq.isEmpty()) {
                throw new IllegalArgumentException("대분류를 선택하세요.");
            }

            // String → Timestamp 변환
            Timestamp startTS = Timestamp.valueOf(startDate.replace("T", " ") + ":00");
            Timestamp endTS = Timestamp.valueOf(endDate.replace("T", " ") + ":00");

            // Map에 담기
            Map<String, Object> paraMap = new HashMap<>();
            paraMap.put("memberSeq", memberSeq);
            paraMap.put("calendarUser", memberSeq);
            paraMap.put("calendarStart", startTS);
            paraMap.put("calendarEnd", endTS);
            paraMap.put("title", title);
            paraMap.put("calendarType", calendarType);
            paraMap.put("calendarLocation", calendarLocation);
            paraMap.put("shareTargets", shareTargets);
            paraMap.put("content", content);
            paraMap.put("bigCategorySeq", bigCategorySeq);
            paraMap.put("smallCategorySeq", smallCategorySeq);

            int n = service.addCalendarEvent(paraMap);

            mav.addObject("message", (n > 0) ? "일정 등록에 성공하였습니다." : "일정 등록에 실패하였습니다.");
            mav.addObject("loc", request.getContextPath() + "/Calendar/list");

        } catch (Exception e) {
            mav.addObject("message", "오류 발생: " + e.getMessage());
            mav.addObject("loc", request.getContextPath() + "/Calendar/addCalendarForm");
        }

        mav.setViewName("msg");
        return mav;
    }
    
   
    // 개인 일정 보여주기 //
    @ResponseBody
    @GetMapping(value="selectCalendar")
    public List<CalendarAjaxDTO> selectCalendar(HttpServletRequest request) {
        // 요청 파라미터로 사번(calendar_user) 전달
    	HttpSession session = request.getSession();
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginuser");
        int calendarUser = loginUser.getMemberSeq();
        
        List<CalendarAjaxDTO> calendarList = service.selectCalendar(String.valueOf(calendarUser));
       
        
        return calendarList;
    }

    // === 일정 상세보기 ===
    @GetMapping(value="detailCalendar")
    public ModelAndView detailCalendar(ModelAndView mav, HttpServletRequest request) {
        
    	
        // 1. 요청 파라미터
    	String calendarSeqStr = request.getParameter("calendarSeq");

        // 2. 이전 페이지로 돌아가기용 URL (필요 시)
        String listGoBackURL = request.getParameter("listGoBackURL");
        mav.addObject("listGoBackURL", listGoBackURL);

        // 3. 현재 URL 저장 (상세보기에서 수정페이지 이동 시 필요)
        String goBackURL = MyUtil.getCurrentURL(request);
        mav.addObject("goBackURL", goBackURL);

        try {
            int calendarSeq = Integer.parseInt(calendarSeqStr); // String → int

            Map<String, String> map = service.detailCalendar(calendarSeq);
            //System.out.println("calendarType 확인: " + map.get("calendarType"));
            
            mav.addObject("map", map);

            //System.out.println("calendarSeq=" + calendarSeq);
            //System.out.println("detail map=" + map);

            
            mav.setViewName("Calendar/detailCalendar");
        } catch (NumberFormatException e) {
            mav.setViewName("redirect:/calendar/calendarList");
        }

        
        
        return mav;
    }

    // === 일정 삭제하기 ===
    @ResponseBody
    @PostMapping("/deleteCalendar")
    public String deleteCalendar(HttpServletRequest request) throws Throwable {
        
        String calendarSeq = request.getParameter("calendarSeq"); // 파라미터 받기
        
        int n = service.deleteCalendar(calendarSeq); // 삭제 실행 (성공시 1, 실패시 0)
        
        JSONObject jsObj = new JSONObject();
        jsObj.put("n", n);
        
        return jsObj.toString();
    }
    
    // === 일정 수정하기 ===
    @PostMapping("/editCalendar")
    public ModelAndView editCalendar(ModelAndView mav, HttpServletRequest request) {

        String calendarSeqStr = request.getParameter("calendarSeq");
        HttpSession session = request.getSession(false);
        MemberDTO loginUser = (session != null) ? (MemberDTO) session.getAttribute("loginuser") : null;

        if (loginUser == null) {
            mav.setViewName("redirect:/login");
            return mav;
        }

        try {
            int calendarSeq = Integer.parseInt(calendarSeqStr);
            String gobackURL_detailCalendar = request.getParameter("gobackURL_detailCalendar");

            
            Map<String, String> map = service.detailCalendar(calendarSeq);
            int calendarUser = Integer.parseInt(map.get("fkMemberSeq").toString());

            System.out.println("calendarUser 파라미터 = " + calendarUser);
            System.out.println("상세조회 결과 = " + map);
            System.out.println(calendarSeqStr);
            
            if (loginUser.getMemberSeq() != calendarUser) {
                mav.addObject("message", "다른 사용자가 작성한 일정은 수정할 수 없습니다.");
                mav.addObject("loc", "javascript:history.back()");
                mav.setViewName("msg");
            } else {
                mav.addObject("map", map);
                mav.addObject("gobackURL_detailCalendar", gobackURL_detailCalendar);
                
                mav.setViewName("Calendar/editCalendar");
            }

        } catch (NumberFormatException e) {
            mav.setViewName("redirect:/Calendar/list");
        }

        return mav;
    }

    // === 일정 수정 완료하기 ===
    @PostMapping("/editCalendar_end")
    public ModelAndView editCalendar_end(CalendarDTO cvo, HttpServletRequest request, ModelAndView mav) {

        try {
            // 서비스에서 일정 수정
            int n = service.editCalendar_end(cvo);

            if(n == 1) {
                mav.addObject("message", "일정을 수정하였습니다.");
                // 수정 완료 후 상세페이지나 목록으로 이동
                mav.addObject("loc", request.getContextPath() + "/Calendar/detailCalendar?calendarSeq=" + cvo.getCalendarSeq());
            } else {
                mav.addObject("message", "일정 수정에 실패하였습니다.");
                mav.addObject("loc", "javascript:history.back()");
            }

            mav.setViewName("msg");

        } catch (Throwable e) {
            e.printStackTrace();
            mav.setViewName("redirect:/Calendar/list");
        }
        
        return mav;
    }

    /**
     * ✅ 부서 일정 조회
     * - fk_department_seq = 로그인한 사원의 부서 번호 기준
     */
    @GetMapping("/selectDeptCalendar")
    @ResponseBody
    public List<CalendarAjaxDTO> selectDeptCalendar(HttpSession session) {
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginuser");
        
        
        
        if (loginUser == null) {
            return List.of();
        }
        return service.selectDeptCalendar(loginUser.getFkDepartmentSeq());
    }

    
    // 사내 일정 조회
    @GetMapping("/selectCompanyCalendar")
    @ResponseBody
    public List<CalendarAjaxDTO> selectCompanyCalendar() {
    	
        
        
    	List<CalendarAjaxDTO> companyEvents = service.selectCompanyCalendar();
        
        return companyEvents;
    }



    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
