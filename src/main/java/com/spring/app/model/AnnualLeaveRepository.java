package com.spring.app.model;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.app.entity.AnnualLeave;

public interface AnnualLeaveRepository extends JpaRepository<AnnualLeave, Integer> {

}
