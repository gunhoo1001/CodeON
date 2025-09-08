package com.spring.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.spring.app.interceptor.EmployeeDenySignPagesInterceptor;
import com.spring.app.interceptor.HrOnlyInterceptor;
import com.spring.app.interceptor.LoginCheckInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class Interceptor_Configuration implements WebMvcConfigurer {

    private final LoginCheckInterceptor loginCheckInterceptor;
    private final EmployeeDenySignPagesInterceptor employeeDenySignPagesInterceptor;
    private final HrOnlyInterceptor hrOnlyInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 1) 로그인 여부 체크: 로그인 관련/정적 리소스만 예외, 나머지 전부 인터셉트
        registry.addInterceptor(loginCheckInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        // 로그인 화면/처리/로그아웃은 열어두기
                        "/login/**",

                        // 랜딩/에러/파비콘
                        "/", "/index",
                        "/error", "/error/**", "/favicon.ico",

                        // 정적 리소스 (프로젝트에서 쓰는 경로 전부)
                        "/image/**", "/images/**",
                        "/css/**", "/js/**",
                        "/bootstrap-4.6.2-dist/**",
                        "/jquery-ui-1.13.1.custom/**",
                        "/fullcalendar_5.10.1/**",
                        "/Highcharts-10.3.1/**",
                        "/smarteditor/**",
                        "/resources/**"
                );
        // 사원 차단: 대상 경로만
        registry.addInterceptor(employeeDenySignPagesInterceptor)
        .addPathPatterns(
            "/sign/inbox",
            "/sign/history"
        );
        
        // 인사팀 전용
        registry.addInterceptor(hrOnlyInterceptor)
        .addPathPatterns("/member/register"); 
    }
}
