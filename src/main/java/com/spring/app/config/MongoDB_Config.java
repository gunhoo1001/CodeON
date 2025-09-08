package com.spring.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

// ==== (#웹채팅관련10) ==== //
@Configuration  // Spring 컨테이너가 처리해주는 클래스로서, 클래스내에 하나 이상의 @Bean 메소드를 선언만 해주면 런타임시 해당 빈에 대해 정의되어진 대로 요청을 처리해준다.
public class MongoDB_Config {

	@Value("${spring.data.mongodb.uri}") // "${spring.data.mongodb.uri}" 은 application.yml 파일에 설정된 값이다.
    private String uri; // 변수 uri 에는 위의 @Value("${spring.data.mongodb.uri}") 이 들어온다.
	                    // 즉, uri 에는 mongodb://devuser:qwer1234$@localhost:27017/mydb 이 들어온다. 

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory() {
        return new SimpleMongoClientDatabaseFactory(uri);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoDatabaseFactory());
    }
}
