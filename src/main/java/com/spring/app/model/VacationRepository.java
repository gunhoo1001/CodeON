package com.spring.app.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.app.entity.Vacation;

public interface VacationRepository extends JpaRepository<Vacation, Long> {
	
	Optional<Vacation> findByDraftSeq(Long draftSeq);
	
}
