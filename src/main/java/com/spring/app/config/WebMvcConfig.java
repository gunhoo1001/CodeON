package com.spring.app.config;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebMvcConfig implements WebMvcConfigurer {
		
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 기존 정적 리소스 (CSS, JS 등)
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/resources/");

        // 업로드 파일 매핑
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:///C:/Users/user/git/CodeON/CodeON/src/main/webapp/upload/");
    }
}
