package com.spring.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import com.spring.app.excel.ExcelDownloadView;


@Configuration  // Spring 컨테이너가 처리해주는 클래스로서, 클래스내에 하나 이상의 @Bean 메소드를 선언만 해주면 런타임시 해당 빈에 대해 정의되어진 대로 요청을 처리해준다.   
public class ViewConfiguration {
    	
 	
	@Bean(name="excelDownloadView") // Spring 컨테이너에서 관리할 빈을 메소드로 생성할 때 사용한다.
	ExcelDownloadView excelDownloadView() {
		ExcelDownloadView viewResolver = new ExcelDownloadView();
		return viewResolver;
	}
 	
	
	@Bean // Spring 컨테이너에서 관리할 빈을 메소드로 생성할 때 사용한다.
	@Order(0)
	BeanNameViewResolver excelViewResolver() {
		BeanNameViewResolver viewResolver = new BeanNameViewResolver();
		viewResolver.setOrder(0);
		return viewResolver;
	}
	
	
	@Bean
 	@Order(1)
	ViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setOrder(1);
		viewResolver.setViewClass(JstlView.class);
		viewResolver.setPrefix("/WEB-INF/views/");
		viewResolver.setSuffix(".jsp");

		return viewResolver;
	}

}


