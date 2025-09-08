package com.spring.app.model;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.app.entity.Grade;

public interface GradeRepository extends JpaRepository<Grade, Integer>  {

	
	
}
