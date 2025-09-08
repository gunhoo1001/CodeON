package com.spring.app.chatting.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// === (#웹채팅관련3) ===
@Controller
@RequestMapping("/chatting/")
public class ChattingController {

	@GetMapping("multichat")
	public String requiredLogin_multichat(HttpServletRequest request, HttpServletResponse response) { 
		
		return "chatting/multichat";
	}
	
}
