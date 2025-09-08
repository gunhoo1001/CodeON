package com.spring.app.chatting.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

// === (#웹채팅관련9) ===
@Getter
@Setter

// 먼저 Mongodb Compass 에서 mydb 속에 chatting 이라는 컬렉션을 만든후 아래의 작업을 해야 한다.!!!
/*
 @Document 
 ==> Spring Data MongoDB 에서만 사용할 수 있는 어노테이션으로, MongoDB Collection(컬렉션) 에 저장되는 도큐먼트로 사용할 Class 를 지정할 때 사용한다.
*/
@Document(collection = "chatting") // "chatting" 컬렉션(==테이블명) 에 저장되는 도큐먼트를 Mongo_messageDTO class 로 사용하겠다는 말이다. 
@Component
public class Mongo_messageDTO {

	@Id
	private String _id;     // "id" 또는 "_id" 라는 필드가 있으면 이 필드가 자동으로 도큐먼트의 "ObjectId" 인 "_id" 로 사용된다. 
	
	private String message;
	private String type;    // all 이면 전체에게 채팅메시지를 보냄. one 이면 특정 사용자에게 채팅메시지를 보냄.
	private String to;      // 특정 웹소켓 id 
	
	private String userid;      // 사용자ID
	private String name;        // 사용자명
	private String currentTime; // 채팅한 시간 (오후 5:05) 
	
	/*
	  Date 타입의 변수를 사용하기 위해서 
	  @DateTimeFormat(iso = ISO.DATE_TIME) 어노테이션을 부여해 준 것이다.
	*/
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date created; // 채팅한 년월일시분초
	
}








