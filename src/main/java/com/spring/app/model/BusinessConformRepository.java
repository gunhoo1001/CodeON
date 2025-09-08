package com.spring.app.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.app.entity.BusinessConform;

public interface BusinessConformRepository extends JpaRepository<BusinessConform, Long> {
	
	Optional<BusinessConform> findByDraftSeq(Long draftSeq);

}
