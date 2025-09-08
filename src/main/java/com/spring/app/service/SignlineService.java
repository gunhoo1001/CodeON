package com.spring.app.service;

import java.util.List;

import com.spring.app.domain.SignlineDTO;
import com.spring.app.entity.Signline;

public interface SignlineService {
    
    // 로그인 사용자의 결재라인 목록 반환
	List<SignlineDTO> findAllByOwner(int memberSeq);
	
	// 한 결재라인에 저장된 직원 목록
	List<SignlineDTO> getLinesWithMembers(Long fkMemberSeq);

	// 한 결재라인에 저장된 하나의 직원
	Signline getLineWithMembers(Long signlineSeq);

}
