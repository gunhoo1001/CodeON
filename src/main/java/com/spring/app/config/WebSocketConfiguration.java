package com.spring.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import com.spring.app.chatting.controller.WebsocketEchoHandler;

import lombok.RequiredArgsConstructor;

// === (#웹채팅관련5) ===
@Configuration
@EnableWebSocket  // 웹소켓을 활성화 시키는 것이다.
@RequiredArgsConstructor
public class WebSocketConfiguration implements WebSocketConfigurer {

	private final WebsocketEchoHandler websocketEchoHandler;
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(websocketEchoHandler, "/chatting/multichatstart").setAllowedOrigins("*").addInterceptors(new HttpSessionHandshakeInterceptor());         
		// WebSocketConfigurer 를 구현한 WebSocketConfiguration 클래스에서 사용할 hanlder 클래스와 URL 경로를 설정해준다.
        // .setAllowedOrigins("*")는 ws프로토콜 /chatting/multichatstart 하위의 모든 uri에서 websocketEchoHandler 를 사용한다는 의미이다.
		// addInterceptors(new HttpSessionHandshakeInterceptor()) 는 WebsocketEchoHandler websocketEchoHandler 에 접근하기 전에 
        // 먼저 HttpSession에 접근하여 HttpSession에 저장된 값을 읽어 들여 WebsocketEchoHandler websocketEchoHandler 에서 사용할 수 있도록 처리해주는 것으로
		// HttpSession 을 가져오기 위한 인터셉터를 추가하는 것이다.
	}

}
