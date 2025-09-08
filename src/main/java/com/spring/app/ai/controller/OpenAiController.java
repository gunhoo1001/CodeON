package com.spring.app.ai.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.app.ai.service.OpenAiService;
import com.spring.app.domain.AttendanceDTO;
import com.spring.app.domain.DraftDTO;
import com.spring.app.domain.MemberDTO;
import com.spring.app.domain.VacationDTO;
import com.spring.app.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ai/")
@RequiredArgsConstructor
public class OpenAiController {

	private final OpenAiService openAiService;
	private final MemberService memberService;
	private final IntegratedHrService hrService;
	
	@GetMapping("memberChat")
	public String memberChat() {
		List<MemberDTO> members = memberService.findAll();
		
		return openAiService.memberChat(members);
	}
	
	@GetMapping("hrChat")
	public String hrChat(@RequestParam(name = "question", required = false, defaultValue = "HR 데이터 분석 요청") String question) {
	    List<MemberDTO> members = hrService.findAllMembers();
	    List<VacationDTO> vacations = hrService.findAllVacations();
	    List<DraftDTO> drafts = hrService.findAllDrafts();
	    List<AttendanceDTO> attendances = hrService.findAllAttendances();

	    // 사용자 질문 전달
	    return openAiService.analyzeHrData(members, vacations, drafts, attendances, question);
	}

	
}
