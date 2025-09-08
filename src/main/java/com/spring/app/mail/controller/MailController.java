package com.spring.app.mail.controller;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.spring.app.common.FileManager;
import com.spring.app.common.MyUtil;
import com.spring.app.domain.MemberDTO;
import com.spring.app.mail.domain.MailDTO;
import com.spring.app.mail.domain.MailUserStatusDTO;
import com.spring.app.mail.service.MailService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/mail/")
@RequiredArgsConstructor
public class MailController {
	
	private final MailService service;
	private final FileManager fileManager;
	
	@GetMapping("list")
	public ModelAndView list(ModelAndView mav, HttpServletRequest request,
	        HttpServletResponse response,
	        @RequestParam(name="searchWord", defaultValue = "") String searchWord,
	        @RequestParam(name="filter", defaultValue = "all") String filter,
	        @RequestParam(name="currentShowPageNo", defaultValue = "1") String currentShowPageNo) {

	    List<MailDTO> mailList = null;

	    HttpSession session = request.getSession();
	    MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
	    
	    // 로그인 확인
	    if(loginuser == null) {
	        mav.setViewName("redirect:/index");
	        return mav;
	    }

	    Map<String, String> paraMap = new HashMap<>();
	    paraMap.put("searchWord", searchWord);
	    paraMap.put("filter", filter);
	    paraMap.put("loginUserEmail", loginuser.getMemberEmail());

	    // === 페이징 처리 ===
	    int totalCount = service.getTotalCount(paraMap);  // 검색어 + 필터 조건 반영된 총 게시물 수
	    int sizePerPage = 10;
	    int totalPage = (int) Math.ceil((double) totalCount/sizePerPage);

	    paraMap.put("currentShowPageNo", currentShowPageNo);

	    mailList = service.mailListSearch_withPaging(paraMap); 

	    mav.addObject("mailList", mailList);

	    // === 페이지바 만들기 ===
	    int blockSize = 10;
	    int loop = 1;
	    int pageNo = ((Integer.parseInt(currentShowPageNo) - 1)/blockSize) * blockSize + 1;

	    String pageBar = "<ul style='list-style:none;'>";
	    String url = "list";

	    // [맨처음][이전]
	    pageBar += "<li style='display:inline-block; width:70px; font-size:12px;'>"
	            + "<a href='" + url + "?searchWord=" + searchWord 
	            + "&filter=" + filter
	            + "&currentShowPageNo=1'>[<<]</a></li>";

	    if (pageNo != 1) {
	        pageBar += "<li style='display:inline-block; width:50px; font-size:12px;'>"
	                + "<a href='" + url + "?searchWord=" + searchWord 
	                + "&filter=" + filter
	                + "&currentShowPageNo=" + (pageNo-1) + "'>[<]</a></li>";
	    }

	    while(!(loop > blockSize || pageNo > totalPage)) {
	        if (pageNo == Integer.parseInt(currentShowPageNo)) {
	            pageBar += "<li style='display:inline-block; width:30px; font-size:12px; border:1px solid gray; color:red; padding:2px 4px;'>"
	                    + pageNo + "</li>";
	        } else {
	            pageBar += "<li style='display:inline-block; width:30px; font-size:12px;'>"
	                    + "<a href='" + url + "?searchWord=" + searchWord 
	                    + "&filter=" + filter
	                    + "&currentShowPageNo=" + pageNo + "'>" + pageNo + "</a></li>";
	        }
	        loop++;
	        pageNo++;
	    }

	    // [다음][마지막]
	    if (pageNo <= totalPage) {
	        pageBar += "<li style='display:inline-block; width:50px; font-size:12px;'>"
	                + "<a href='" + url + "?searchWord=" + searchWord 
	                + "&filter=" + filter
	                + "&currentShowPageNo=" + pageNo + "'>[>]</a></li>";
	    }
	    
	    pageBar += "<li style='display:inline-block; width:70px; font-size:12px;'>"
	            + "<a href='" + url + "?searchWord=" + searchWord 
	            + "&filter=" + filter
	            + "&currentShowPageNo=" + totalPage + "'>[>>]</a></li>";

	    pageBar += "</ul>";

	    mav.addObject("pageBar", pageBar);
	    mav.addObject("totalCount", totalCount);
	    mav.addObject("currentShowPageNo", currentShowPageNo);
	    mav.addObject("sizePerPage", sizePerPage);

	    String listURL = MyUtil.getCurrentURL(request);
	    Cookie cookie = new Cookie("listURL", listURL);
	    cookie.setMaxAge(24*60*60);
	    cookie.setPath("/mail/");
	    response.addCookie(cookie);

	    mav.setViewName("/mail/list");
	    return mav;
	}

	@GetMapping("send")
	public ModelAndView send(
	        ModelAndView mav,
	        HttpServletRequest request,
	        HttpServletResponse response,
	        @RequestParam(name="searchWord", defaultValue="") String searchWord,
	        @RequestParam(name="currentShowPageNo", defaultValue="1") String currentShowPageNo) {

	    HttpSession session = request.getSession();
	    MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");

	    // 로그인 확인
	    if(loginuser == null) {
	        mav.setViewName("redirect:/index");
	        return mav;
	    }

	    // 검색 및 로그인 사용자 이메일 기준
	    Map<String, String> paraMap = new HashMap<>();
	    paraMap.put("searchWord", searchWord);
	    paraMap.put("loginUserEmail", loginuser.getMemberEmail());
	    paraMap.put("currentShowPageNo", currentShowPageNo);

	    // === 페이징 처리 ===
	    int totalCount = service.getSentMailTotalCount(paraMap); // 로그인 사용자 보낸 메일 총 개수
	    int sizePerPage = 10;
	    int totalPage = (int)Math.ceil((double) totalCount / sizePerPage);

	    List<MailDTO> mailList = service.getSentMailListWithPaging(paraMap);

	    mav.addObject("mailList", mailList);

	    // === 페이지바 생성 ===
	    int blockSize = 10;
	    int loop = 1;
	    int pageNo = ((Integer.parseInt(currentShowPageNo)-1)/blockSize)*blockSize + 1;

	    String pageBar = "<ul style='list-style:none;'>";
	    String url = "send";

	    // [처음][이전]
	    pageBar += "<li style='display:inline-block; width:70px;'><a href='" + url + "?searchWord=" + searchWord + "&currentShowPageNo=1'>[<<]</a></li>";
	    if(pageNo != 1){
	        pageBar += "<li style='display:inline-block; width:50px;'><a href='" + url + "?searchWord=" + searchWord + "&currentShowPageNo=" + (pageNo-1) + "'>[<]</a></li>";
	    }

	    while(!(loop > blockSize || pageNo > totalPage)) {
	        if(pageNo == Integer.parseInt(currentShowPageNo)) {
	            pageBar += "<li style='display:inline-block; width:30px; border:1px solid gray; color:red; text-align:center;'>" + pageNo + "</li>";
	        } else {
	            pageBar += "<li style='display:inline-block; width:30px; text-align:center;'><a href='" + url + "?searchWord=" + searchWord + "&currentShowPageNo=" + pageNo + "'>" + pageNo + "</a></li>";
	        }
	        loop++;
	        pageNo++;
	    }

	    // [다음][마지막]
	    if(pageNo <= totalPage){
	        pageBar += "<li style='display:inline-block; width:50px;'><a href='" + url + "?searchWord=" + searchWord + "&currentShowPageNo=" + pageNo + "'>[>]</a></li>";
	    }
	    pageBar += "<li style='display:inline-block; width:70px;'><a href='" + url + "?searchWord=" + searchWord + "&currentShowPageNo=" + totalPage + "'>[>>]</a></li>";
	    pageBar += "</ul>";

	    mav.addObject("pageBar", pageBar);
	    mav.addObject("totalCount", totalCount);
	    mav.addObject("currentShowPageNo", currentShowPageNo);
	    mav.addObject("sizePerPage", sizePerPage);

	    // 현재 URL 쿠키 저장
	    String listURL = request.getRequestURL().toString() + "?" + request.getQueryString();
	    Cookie cookie = new Cookie("listURL", listURL);
	    cookie.setMaxAge(24*60*60);
	    cookie.setPath("/mail/");
	    response.addCookie(cookie);

	    mav.setViewName("/mail/send"); // JSP 경로
	    return mav;
	}

	
	
	@GetMapping("view")
    public ModelAndView viewMail(@RequestParam("emailSeq") String emailSeq, HttpServletRequest request, ModelAndView mav) {
		
	    String referer = request.getHeader("Referer"); // 이전 페이지 URL
	    mav.addObject("prevPage", referer);
	    
		MailDTO mail = service.selectOne(emailSeq);
		mav.addObject("mail", mail);
		mav.setViewName("mail/view");
        return mav;
    }
	
	@GetMapping("receive")
	public ModelAndView receive(
	        ModelAndView mav,
	        HttpServletRequest request,
	        HttpServletResponse response,
	        @RequestParam(name="searchWord", defaultValue="") String searchWord,
	        @RequestParam(name="currentShowPageNo", defaultValue="1") String currentShowPageNo) {

	    HttpSession session = request.getSession();
	    MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");

	    // 로그인 확인
	    if (loginuser == null) {
	        mav.setViewName("redirect:/index");
	        return mav;
	    }

	    // 검색어 + 로그인 사용자 이메일 기준
	    Map<String, String> paraMap = new HashMap<>();
	    paraMap.put("searchWord", searchWord);
	    paraMap.put("loginUserEmail", loginuser.getMemberEmail());
	    paraMap.put("currentShowPageNo", currentShowPageNo);

	    // === 페이징 처리 ===
	    int totalCount = service.getReceivedMailTotalCount(paraMap); // 받은 메일 총 개수
	    int sizePerPage = 10;
	    int totalPage = (int) Math.ceil((double) totalCount / sizePerPage);

	    List<MailDTO> mailList = service.getReceivedMailListWithPaging(paraMap);

	    mav.addObject("mailList", mailList);

	    // === 페이지바 생성 ===
	    int blockSize = 10;
	    int loop = 1;
	    int pageNo = ((Integer.parseInt(currentShowPageNo) - 1) / blockSize) * blockSize + 1;

	    String pageBar = "<ul style='list-style:none;'>";
	    String url = "receive";

	    // [처음][이전]
	    pageBar += "<li style='display:inline-block; width:70px;'><a href='" + url + "?searchWord=" + searchWord + "&currentShowPageNo=1'>[<<]</a></li>";
	    if (pageNo != 1) {
	        pageBar += "<li style='display:inline-block; width:50px;'><a href='" + url + "?searchWord=" + searchWord + "&currentShowPageNo=" + (pageNo - 1) + "'>[<]</a></li>";
	    }

	    while (!(loop > blockSize || pageNo > totalPage)) {
	        if (pageNo == Integer.parseInt(currentShowPageNo)) {
	            pageBar += "<li style='display:inline-block; width:30px; border:1px solid gray; color:red; text-align:center;'>" + pageNo + "</li>";
	        } else {
	            pageBar += "<li style='display:inline-block; width:30px; text-align:center;'><a href='" + url + "?searchWord=" + searchWord + "&currentShowPageNo=" + pageNo + "'>" + pageNo + "</a></li>";
	        }
	        loop++;
	        pageNo++;
	    }

	    // [다음][마지막]
	    if (pageNo <= totalPage) {
	        pageBar += "<li style='display:inline-block; width:50px;'><a href='" + url + "?searchWord=" + searchWord + "&currentShowPageNo=" + pageNo + "'>[>]</a></li>";
	    }
	    pageBar += "<li style='display:inline-block; width:70px;'><a href='" + url + "?searchWord=" + searchWord + "&currentShowPageNo=" + totalPage + "'>[>>]</a></li>";
	    pageBar += "</ul>";

	    mav.addObject("pageBar", pageBar);
	    mav.addObject("totalCount", totalCount);
	    mav.addObject("currentShowPageNo", currentShowPageNo);
	    mav.addObject("sizePerPage", sizePerPage);

	    // 현재 URL 쿠키 저장 (상세보기에서 뒤로가기 활용)
	    String listURL = request.getRequestURL().toString() + "?" + request.getQueryString();
	    Cookie cookie = new Cookie("listURL", listURL);
	    cookie.setMaxAge(24 * 60 * 60);
	    cookie.setPath("/mail/");
	    response.addCookie(cookie);

	    mav.setViewName("/mail/receive"); // JSP 경로
	    return mav;
	}

	
	@GetMapping("write")
	public ModelAndView write(HttpServletRequest request,
					          HttpServletResponse response,
					          ModelAndView mav) {
		
		mav.setViewName("mail/write");
		return mav;
	}
	

	
	@PostMapping("write")
	public ModelAndView write(ModelAndView mav, MailDTO mailDto,
			 @RequestParam(value="importantStatus", required=false) String importantStatus, HttpServletRequest request) {

		MultipartFile attach = mailDto.getAttach();

		// === 사용자(클라이언트)가 쓴 글에 파일이 첨부되어 있으면 클라이언트가 올리려고 하는 첨부파일을 가져와서 WAS 의 disk 상에 파일을 올려주어야 한다.
		if (!attach.isEmpty()) {
			// attach(첨부파일)이 있으면
			/*
	            1. 사용자가 보낸 첨부파일을 WAS(톰캣)의 특정 폴더에 저장해주어야 한다.
	            >>> 파일이 업로드 되어질 특정 경로(폴더)지정해주기 
	                우리는 WAS 의 /myspring/src/main/webapp/resources/files 라는 폴더를 생성해서 여기로 업로드 해주도록 할 것이다. 
            */
			
			// WAS의 webapp 의 절대경로를 알아와야 한다
			HttpSession session = request.getSession();
			String root = session.getServletContext().getRealPath("/");
			
			// System.out.println(root);
			// C:\NCS\worksapce_spring_boot_17\myspring\src\main\webapp\
			
			String path = root + "resources" + File.separator + "files";
			// path 가 첨부파일이 저장될 WAS(톰캣)의 폴더가 된다.
		    // System.out.println("~~~ 확인용 path ==> " + path);
		    // ~~~ 확인용 path ==> C:\NCS\worksapce_spring_boot_17\myspring\src\main\webapp\resources\files
			
			/*
            	2. 파일첨부를 위한 변수의 설정 및 값을 초기화 한 후 파일 올리기
			*/
			
			System.out.println("path");
	        String newFileName = "";
	        // WAS(톰캣)의 디스크에 저장될 파일명
	         
	        byte[] bytes = null;
	        // 첨부파일의 내용물을 담는 것
	         
	        long fileSize = 0;
	        // 첨부파일의 크기
	        
	        try {
	        	bytes = attach.getBytes();
				// 첨부파일의 내용물을 읽어오는 것
				
				String originalFilename = attach.getOriginalFilename();
				// attach.getOriginalFilename() 이 첨부파일명의 파일명(예: 강아지.png) 이다. 
				
				// System.out.println("~~~ 확인용 originalFilename => " + originalFilename);
				// ~~~ 확인용 originalFilename => berkelekle단가라포인트03.jpg
				
				// 첨부되어진 파일을 업로드 하는 것이다.
				newFileName = fileManager.doFileUpload(bytes, originalFilename, path);
				
				// System.out.println(newFileName);
				// 20250725123914_9e2962fb90f0410aa5a3781fb444d4aa.jpg
				
				// BoardDto boardDto 에 fileName 값과 orgFilename 값과 fileSize 값을 넣어주기
				mailDto.setEmailFilename(newFileName);
				// WAS의 disk 상에 저장된 파일명(20250725123914_9e2962fb90f0410aa5a3781fb444d4aa.jpg)
				
				mailDto.setEmailOrgFilename(originalFilename);
				// 게시판 페이지에서 첨부된 파일(강아지.png)을 보여줄 때 사용.
	            // 또한 사용자가 파일을 다운로드 할때 사용되어지는 파일명으로 사용.
				
				fileSize = attach.getSize(); // 첨부파일의 크기(단위는 byte임)
				mailDto.setEmailFilesize(String.valueOf(fileSize));
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
	        
		}
		
	    // 발신자/수신자 상태 리스트 세팅
	    List<MailUserStatusDTO> statusList = new ArrayList<>();

	    // 발신자
	    MailUserStatusDTO sender = new MailUserStatusDTO();
	    sender.setMemberEmail(mailDto.getSendMemberEmail());
	    sender.setReadStatus("1"); // 발신자는 읽음
	    sender.setImportantStatus(importantStatus);
	    statusList.add(sender);

	    // 수신자 (콤마 구분 여러 명)
	    String[] receivers = mailDto.getReceiveMemberEmail().split(",");
	    for(String rEmail : receivers) {
	        rEmail = rEmail.trim();
	        if(rEmail.isEmpty()) continue;

	        MailUserStatusDTO receiver = new MailUserStatusDTO();
	        receiver.setMemberEmail(rEmail);
	        receiver.setReadStatus("0"); // 수신자는 안읽음
	        receiver.setImportantStatus(importantStatus);
	        statusList.add(receiver);
	    }
	    mailDto.setUserStatusList(statusList);

	    int n;
	    if (attach.isEmpty()) {
	        n = service.write(mailDto);          // 파일 없는 경우
	    } else {
	        n = service.write_withFile(mailDto); // 파일 있는 경우
	    }

	    if (n == 1) {
	        mav.setViewName("redirect:/mail/list");
	    }
	    return mav;
	}
	
	@PostMapping("updateImportant")
	@ResponseBody
	public Map<String, Integer> updateImportant(HttpSession session, @RequestParam Map<String, String> paraMap) {
		
		MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
		String loginUserEmail = loginuser.getMemberEmail();
		paraMap.put("memberEmail", loginUserEmail);
	    // 중요 표시를 변경하고 결과 값 반환
	    int result = service.updateImportant(paraMap);
	    
	    // 결과 반환 (n: 1이면 성공, 0이면 실패)
	    Map<String, Integer> response = new HashMap<>();
	    response.put("n", result); // result가 1이면 성공, 0이면 실패
	    
	    return response;
	}
	
	@PostMapping("updateReadStatus")
	@ResponseBody
	public Map<String, Integer> updateReadStatus(HttpSession session, @RequestParam Map<String, String> paraMap) {
		MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
		String loginUserEmail = loginuser.getMemberEmail();
		paraMap.put("memberEmail", loginUserEmail);
		
		int result = service.updateReadStatus(paraMap);
		Map<String, Integer> response = new HashMap<>();
		response.put("n", result);
		
		return response;
	}
	
	@GetMapping("getCount")
	@ResponseBody
	public Map<String, String> getCount(HttpSession session) {
		
		MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
		String loginUserEmail = loginuser.getMemberEmail();
		
		Map<String, String> response = new HashMap<>();
		
		String count = service.getCount(loginUserEmail);
		String totalCount = service.getTotalCount(loginUserEmail);
		
		response.put("count", count);
		response.put("totalCount", totalCount);
		
		return response;
	}
	
//	@GetMapping("getReceiveCount")
//	@ResponseBody
//	public Map<String, String> getReceiveCount() {
//		Map<String, String> response = new HashMap<>();
//		
//		String receiveCount = service.getReceiveCount();
//		String totalReceiveCount = service.getTotalReceiveCount();
//		
//		response.put("receiveCount", receiveCount);
//		response.put("totalReceiveCount", totalReceiveCount);
//		
//		return response;
//	}
	
	// 메일 여러개 삭제하기
	@PostMapping("deleteMails")
	@ResponseBody
	public Map<String, Object> deleteMails(@RequestParam("emailSeqList") List<Long> emailSeqList) {
		
		service.deleteByEmailSeqList(emailSeqList);
	    int n = service.deleteMails(emailSeqList);
		
		Map<String, Object> response = new HashMap<>();
		response.put("n", n);
		
		return response;
	}
	
	// 메일 하나 삭제하기
	@PostMapping("deleteMail")
	@ResponseBody
	public Map<String, Object> deleteMail(@RequestParam("emailSeq") String emailSeq) {
		
		service.deleteByEmailSeq(emailSeq);
		int n = service.deleteMail(emailSeq);
		
		Map<String, Object> response = new HashMap<>();
		response.put("n", n);
		
		return response;
	}
	
	// 첨부파일 다운로드 받기
	@GetMapping("download")
	public void requiredLogin_downloadComment(HttpServletRequest request,
			   						   		  HttpServletResponse response) {
		
		String seq = request.getParameter("emailSeq");

	    response.setContentType("text/html; charset=UTF-8");
	    
	    PrintWriter out = null;
	    // out 은 웹브라우저에 기술하는 대상체라고 생각하자.
	    
	    Map<String, String> paraMap = new HashMap<>();
	    paraMap.put("seq", seq);
	    
	    MailDTO mailDto = service.selectOne(seq);
	    
	    try {
		    if (mailDto == null || (mailDto != null && mailDto.getEmailFilename() == null)) {
		    	out = response.getWriter();
	            // out 은 웹브라우저에 기술하는 대상체라고 생각하자.
	            
	            out.println("<script type='text/javascript'>alert('파일다운로드가 불가합니다.'); history.back();</script>");
	            return;
		    } else {
		    	// 정상적으로 다운로드가 되어질 경우
		    	String fileName = mailDto.getEmailFilename();
		    	// 바로 WAS 디스크에 저장된 파일명이다.
		    	
		    	String orgFilename= mailDto.getEmailOrgFilename();
		    	// 다룬로드시 보여줄 파일명
		    	
		    	/*
	               첨부파일이 저장되어있는 WAS(톰캣) 디스크 경로명을 알아와야만 다운로드를 해줄 수 있다.
	               이 경로는 우리가 파일첨부를 위해서 @PostMapping("add") 에서 설정해두었던 경로와 똑같아야 한다.    
	            */
	            // WAS 의 webapp 의 절대경로를 알아와야 한다.
	            HttpSession session = request.getSession();
	            String root = session.getServletContext().getRealPath("/");
	            
				// System.out.println(root);
				// C:\NCS\worksapce_spring_boot_17\myspring\src\main\webapp\
				
				String path = root + "resources" + File.separator + "files";
				// path 가 첨부파일이 저장될 WAS(톰캣)의 폴더가 된다.
			    // System.out.println("~~~ 확인용 path ==> " + path);
			    // ~~~ 확인용 path ==> C:\NCS\worksapce_spring_boot_17\myspring\src\main\webapp\resources\files
				
				// *** file 다운로드하기 *** //
				boolean flag = false; // file 다운로드 성공, 실패인지 여부를 알려주는 용도
				// file 다운로드 성공시 flag 는 true,
	            // file 다운로드 실패시 flag 는 false 를 가진다.
				flag = fileManager.doFileDownload(fileName, orgFilename, path, response);
				
				if(!flag) {
	               // 다운로드가 실패한 경우 메시지를 띄운다.
	               out = response.getWriter();
	               // out 은 웹브라우저에 기술하는 대상체라고 생각하자.
	               
	               out.println("<script type='text/javascript'>alert('파일다운로드가 실패되었습니다.'); history.back();</script>");
	            }
		    }
		    
	    } catch (Exception e) {
	    	try {
		    	out = response.getWriter();
	            // out 은 웹브라우저에 기술하는 대상체라고 생각하자.
	            
	            out.println("<script type='text/javascript'>alert('파일다운로드가 불가합니다.'); history.back();</script>");
	    	} catch (Exception e1) {
	    		e1.printStackTrace();
	    	}
	    }
	}
	
    // 답장 화면으로 이동
    @GetMapping("resend")
    public String resendMail(@RequestParam("emailSeq") String emailSeq,
                             @RequestParam("sendMemberEmail") String sendMemberEmail,
                             Model model) {

        // 원본 메일 불러오기
        MailDTO originalMail = service.selectOne(emailSeq);

        // 답장용 기본값 세팅
        MailDTO replyMail = new MailDTO();
        replyMail.setReceiveMemberEmail(sendMemberEmail); // 받는사람 = 원본 보낸사람
        replyMail.setEmailTitle("RE: " + originalMail.getEmailTitle()); // 제목 앞에 "RE:" 붙이기

        // JSP로 넘겨줌
        model.addAttribute("replyMail", replyMail);
        model.addAttribute("originalMail", originalMail);

        return "mail/resend";
    }
}

