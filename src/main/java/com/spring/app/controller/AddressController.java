package com.spring.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.spring.app.domain.MemberDTO;
import com.spring.app.service.AddressService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {

	private final AddressService addressService;
    private static final int PAGE_SIZE = 10;

    @GetMapping
    public String list(@RequestParam(value="dept", required=false) Integer dept,
                       @RequestParam(value="q",    required=false) String kw,
                       @RequestParam(value="page", required=false, defaultValue="1") int page,
                       Model model) {
    	
    	// 부서 목록 조회(드롭다운 용)
        var departments = addressService.departments();	
        // 조건(부서, 키워드)에 맞는 주소록 검색 + 페이징
        var result = addressService.search(dept, kw, page, PAGE_SIZE);	

        model.addAttribute("departments", departments);			  // 부서 목록
        model.addAttribute("selectedDept", dept);			      // 선택된 부서
        model.addAttribute("keyword", kw);				          // 검색 키워드
        model.addAttribute("items", result.getContent()); 		  // 조회된 데이터 목록
        model.addAttribute("page", result.getNumber() + 1); 	  // 현재 페이지
        model.addAttribute("totalPages", result.getTotalPages()); // 전체 페이지 수
        return "address/list"; 
    }
}
