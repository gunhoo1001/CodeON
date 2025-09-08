package com.spring.app.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.spring.app.entity.Signline;

public interface SignlineRepository extends JpaRepository<Signline, Long> {
	
	// 로그인 사용자의 결재라인 목록 반환
	List<Signline> findByFkMemberSeqOrderBySignlineSeqDesc(Long fkMemberSeq);

	
	@Query("""
		    select distinct sl
		    from Signline sl
		    left join fetch sl.members sm
		    left join fetch sm.member m
		    left join fetch m.department d
		    left join fetch m.grade g
		    where sl.fkMemberSeq = :fkMemberSeq
		    order by sl.signlineSeq desc
		  """)
	List<Signline> findAllWithMembersByFkMemberSeq(@Param("fkMemberSeq") Long fkMemberSeq);

}
