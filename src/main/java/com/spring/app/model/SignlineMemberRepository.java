package com.spring.app.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.app.entity.SignlineMember;

public interface SignlineMemberRepository extends JpaRepository<SignlineMember, Long> {
	List<SignlineMember> findBySignline_SignlineSeqOrderByLineOrderAsc(Long signlineSeq);
    void deleteBySignline_SignlineSeq(Long signlineId);
}
