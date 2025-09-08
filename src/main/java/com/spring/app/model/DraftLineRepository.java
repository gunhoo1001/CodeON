// com.spring.app.model.DraftLineRepository.java
package com.spring.app.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.spring.app.entity.DraftLine;

public interface DraftLineRepository extends JpaRepository<DraftLine, Long> {
	
    /** 결재하기: 내가 다음 결재자이면서 대기(0) */
	@Query("""
	select distinct dl
	  from DraftLine dl
	  join fetch dl.draft d
	  join fetch d.member drafter
	  left join fetch d.draftType dt
	 where dl.approver.memberSeq = :memberSeq
	   and coalesce(dl.signStatus, 0) = 0
	   and dl.lineOrder = (
	     select max(dl2.lineOrder)  
	       from DraftLine dl2
	      where dl2.draft = d
	        and coalesce(dl2.signStatus, 0) = 0
	   )
	 order by d.draftRegdate desc
	""")
	List<DraftLine> findInbox(@Param("memberSeq") Long memberSeq);


    /** 결재함: 내가 승인/반려 했던 내역 */
    @Query("""
    select distinct dl
      from DraftLine dl
      join fetch dl.draft d
      join fetch d.member drafter
      left join fetch d.draftType dt
     where dl.approver.memberSeq = :memberSeq
       and dl.signStatus in (1,9)
     order by dl.signDate desc nulls last, d.draftRegdate desc
    """)
    List<DraftLine> findHistory(@Param("memberSeq") Long memberSeq);
    
    List<DraftLine> findByDraft_DraftSeqOrderByLineOrderAscDraftLineSeqAsc(Long draftSeq);

    @Query("""
      select dl
        from DraftLine dl
        join fetch dl.approver a
        left join fetch a.department dep
        left join fetch a.grade g
       where dl.draft.draftSeq = :draftSeq
       order by dl.lineOrder asc, dl.draftLineSeq asc
    """)
    List<DraftLine> findDetailLines(@Param("draftSeq") Long draftSeq);

    @Query("""
        select max(dl.lineOrder)
          from DraftLine dl
         where dl.draft.draftSeq = :seq
           and dl.signStatus = 0
    """)
    Integer findNextOrder(@Param("seq") Long seq);

    @Query("""
      select dl
        from DraftLine dl
       where dl.draft.draftSeq = :draftSeq
         and dl.approver.memberSeq = :memberSeq
    """)
    Optional<DraftLine> findMyLine(@Param("draftSeq") Long draftSeq,
                                   @Param("memberSeq") Long memberSeq);

    @Query("""
            select dl
              from DraftLine dl
              join fetch dl.draft d
              join fetch dl.approver a
              left join fetch a.department dep
              left join fetch a.grade g
             where d.draftSeq = :draftSeq
             order by dl.lineOrder asc, dl.draftLineSeq asc
        """)
	List<DraftLine> findLinesWithApprover(@Param("draftSeq") Long draftSeq);
	
    List<DraftLine> findByDraft_DraftSeqOrderByLineOrderAsc(Long draftSeq);

    @Query("""
	  select dl
	    from DraftLine dl
	    join fetch dl.approver a
	    left join fetch a.grade
	   where dl.draft.draftSeq = :draftSeq
	   order by dl.lineOrder asc
	""")
	List<DraftLine> findLinesWithApproverByDraft(@Param("draftSeq") Long draftSeq);
    
}
