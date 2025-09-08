package com.spring.app.chatting.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.spring.app.chatting.domain.Mongo_messageDTO;

import lombok.RequiredArgsConstructor;

// === (#웹채팅관련11) ===
@Service
@RequiredArgsConstructor
public class ChattingMongoOperations {

	// === com.spring.app.config.MongoDB_Config 클래스에서 생성된 MongoTemplate mongoTemplate 빈을 주입시킨다. === 
 
 //	private final MongoTemplate mongoTemplate;
 // 또는
	private final MongoOperations mongoTemplate;

	public void insertMessage(Mongo_messageDTO document) throws Exception {
		
		try {
		  // mongoTemplate.save(document, "chatting"); // 없으면 추가, 있으면 수정. 이것을 실행되면 mongodb 의 데이터베이스 mydb 에 chatting 라는 컬렉션이 없다라도 자동적으로 먼저 chatting 컬렉션을 생성해준 다음에 도큐먼트를 추가시켜준다.
		  // 또는
			 mongoTemplate.save(document); // 없으면 추가, 있으면 수정. 이것을 실행되면 mongodb 의 데이터베이스 mydb 에 chatting 라는 컬렉션이 없다라도 자동적으로 먼저 chatting 컬렉션을 생성해준 다음에 도큐먼트를 추가시켜준다.  
                                           // 없으면 추가, 있으면 수정.  com.spring.app.chatting.websockethandler.Mongo_messageDTO 클래스 생성시 @Document(collection = "chatting") 어노테이션을 설정했으므로 두번째 파라미터로 "chatting" 은 생략가능하다. 
		} catch(Exception e) {
			e.printStackTrace();
			
			throw e;
		}
		
	}

	
	public List<Mongo_messageDTO> listChatting() {
	
		List<Mongo_messageDTO> list = null;
		
		try {
			// >>> 정렬결과 조회 <<< //
			Query query = new Query();
			query.with(Sort.by(Sort.Direction.ASC, "_id"));
			list = mongoTemplate.find(query, Mongo_messageDTO.class);
			
			// >>> [참고] 전체조회 <<< 
			// list = mongoTemplate.findAll(Mongo_messageDTO.class);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	
}





