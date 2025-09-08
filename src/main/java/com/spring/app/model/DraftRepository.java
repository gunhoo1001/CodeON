package com.spring.app.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.spring.app.entity.Draft;

public interface DraftRepository extends JpaRepository<Draft, Long> {

	 // 문서 번호 미리보기
	@Query(value = """
	        SELECT last_number
	        FROM user_sequences
	        WHERE sequence_name = 'DRAFT_SEQ'
	        """, nativeQuery = true)
	Long peekNextDraftNo();
	
	List<Draft> findByMember_MemberSeqOrderByDraftRegdateDesc(Long memberSeq);

	List<Draft> findByMember_MemberSeqOrderByDraftSeqDesc(Long me);
	
	@Query("""
		    select d
		      from Draft d
		      left join fetch d.draftType
		      left join fetch d.member
		     where d.draftSeq = :id
		""")
	Optional<Draft> findDetail(@Param("id") Long id);

    @Query("""
        select d
          from Draft d
          join fetch d.member m
          left join fetch d.draftType dt
         where d.draftSeq = :seq
    """)
    Optional<Draft> findByIdWithMemberAndType(@Param("seq") Long seq);
    
    @Query("""
	  select d
	    from Draft d
	    left join fetch d.draftType 
	    left join fetch d.member       
	   where d.member.memberSeq = :me
	   order by d.draftSeq desc
	""")
	List<Draft> findByMemberWithType(@Param("me") Long me);

    @Query("""
    	    select d
    	      from Draft d
    	      join fetch d.member m
    	      left join fetch m.department
    	      left join fetch m.grade
    	      left join fetch d.draftType
    	     where d.draftSeq = :seq
    	""")
    	Optional<Draft> findByIdWithMemberTypeAndOrg(@Param("seq") Long seq);
    
    // 내가 올린 문서 중 "결재 진행" (승인 ≥1 && 미승인 존재 && 반려 0)
    @Query("""
    select d
    from Draft d
    where d.member.memberSeq = :me
      and (select count(dl) from DraftLine dl where dl.draft = d) > 0
      and (select sum(case when dl2.signStatus = 9 then 1 else 0 end)
           from DraftLine dl2 where dl2.draft = d) = 0
      and (select sum(case when dl3.signStatus = 1 then 1 else 0 end)
           from DraftLine dl3 where dl3.draft = d) >= 1
      and (select sum(case when dl4.signStatus = 1 then 1 else 0 end)
           from DraftLine dl4 where dl4.draft = d)
          <
          (select count(dl5) from DraftLine dl5 where dl5.draft = d)
    order by d.draftRegdate desc
    """)
    List<Draft> findMyInProgress(@Param("me") Long me);

    // 내가 올린 문서 중 "결재 완료" (반려 0 && 승인수 = 결재자수)
    @Query("""
    select d
    from Draft d
    where d.member.memberSeq = :me
      and (select count(dl) from DraftLine dl where dl.draft = d) > 0
      and (select sum(case when dl2.signStatus = 9 then 1 else 0 end)
           from DraftLine dl2 where dl2.draft = d) = 0
      and (select sum(case when dl3.signStatus = 1 then 1 else 0 end)
           from DraftLine dl3 where dl3.draft = d)
          =
          (select count(dl4) from DraftLine dl4 where dl4.draft = d)
    order by d.draftRegdate desc
    """)
    List<Draft> findMyCompleted(@Param("me") Long me);

    
}
