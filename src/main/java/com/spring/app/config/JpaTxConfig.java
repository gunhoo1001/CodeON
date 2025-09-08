package com.spring.app.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class JpaTxConfig {

    @Bean(name = "transactionManager")   // ★ Spring Data JPA가 기본으로 찾는 표준 이름
    @Primary                             // ★ 기본 Tx 매니저로 우선권 부여
    public PlatformTransactionManager jpaTransactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
