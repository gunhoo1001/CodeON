package com.spring.app.chatting.domain;

import com.google.gson.Gson;

import lombok.Getter;
import lombok.Setter;

// === (#웹채팅관련7) === 
@Getter
@Setter
public class MessageDTO {

	private String message;
	private String type; // all 이면 전체에게 채팅메시지를 보냄. one 이면 특정 사용자에게 채팅메시지를 보냄. 
	private String to;   // 특정 웹소켓 id 
	
	// =========================================================== //

	public static MessageDTO convertMessage(String source) {
		
		Gson gson = new Gson();
		
		MessageDTO messageDto = gson.fromJson(source, MessageDTO.class);
		// source 는 JSON 형태로 되어진 문자열
		// gson.fromJson(source, MessageDTO.class); 은 
		// JSON 형태로 되어진 문자열 source를 실제 MessageDTO 객체로 변환해준다.
		
		return messageDto;
	}
	
}







