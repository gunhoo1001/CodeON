package com.spring.app.model;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.spring.app.domain.AddressDTO;
import com.spring.app.domain.MemberProfileDTO;
import com.spring.app.entity.DraftLine;
import com.spring.app.entity.Member;

import jakarta.transaction.Transactional;

public interface MemberRepository extends JpaRepository<Member, Integer> {
	
	@Query("SELECT m FROM Member m JOIN FETCH m.department WHERE m.memberUserid = :memberUserid")
	Optional<Member> findByMemberUserid(@Param("memberUserid") String memberUserid);

	@Modifying
	@Transactional
	@Query("UPDATE Member m SET m.stampImage = :newFilename WHERE m.memberUserid = :memberUserid")
	void stampImageSave(@Param("memberUserid") String memberUserid, @Param("newFilename") String newFilename);

	@Query("select m.stampImage from Member m where m.memberUserid = :userid")
	String findStampImageByUserid(@Param("userid") String userid);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("update Member m set m.stampImage = null where m.memberUserid = :userid")
	int clearStampImageByUserid(@Param("userid") String userid);

	@Query("select m from Member m left join fetch m.department")
	List<Member> findAllWithDept();
	
	// 부서별 직원
	List<Member> findAllByOrderByFkDepartmentSeqAsc();

	// 결재라인에 추가할 수 있는 직원(사원 제외 전부)
	@Query("select m from Member m left join fetch m.department d where m.fkGradeSeq <> 1 order by m.fkDepartmentSeq asc, m.memberName asc")
	List<Member> getSignlineMember();
	
	// 부서정보, 사원정보, 직급 조회
	@Query("""
			  select new com.spring.app.domain.AddressDTO(
			    d.departmentSeq, d.departmentName,	
			    m.memberSeq, m.memberName, m.memberEmail, m.memberMobile, m.memberUserid,	
			    g.gradeName
			  )
			  from Member m
			  join m.department d
			  left join m.grade g
			  where (:dept is null or d.departmentSeq = :dept)
			    and (
			      :kw is null or :kw = '' or
			      lower(m.memberName)   like lower(concat('%', :kw, '%')) or
			      lower(m.memberEmail)  like lower(concat('%', :kw, '%')) or
			      lower(m.memberMobile) like lower(concat('%', :kw, '%')) or
			      lower(m.memberUserid) like lower(concat('%', :kw, '%'))
			    )
			  order by d.departmentSeq asc, m.memberName asc
			""")
			Page<AddressDTO> searchAddress(@Param("dept") Long dept,
			                               @Param("kw")   String kw,
			                               Pageable pageable);
	
	@Query("""
		      select new com.spring.app.domain.MemberProfileDTO(
		        m.memberSeq, m.memberName, m.memberEmail, m.memberMobile, m.memberUserid,
		        cast(m.fkDepartmentSeq as long), d.departmentName,
		        cast(m.fkGradeSeq as long), g.gradeName,
		        to_char(m.memberHiredate, 'YYYY-MM-DD')
		      )
		      from Member m
		      left join m.department d
		      left join m.grade g
		      where m.memberSeq = :memberSeq
		    """)
		    Optional<MemberProfileDTO> findProfileDtoByMemberSeq(@Param("memberSeq") Integer memberSeq);

			// 이메일 중복 검사(본인 제외): 파생 쿼리 메서드 → select count(*) > 0 를 Boolean으로 매핑
			// 해당 이메일을 쓰는 다른 회원이 존재하는지 검사	
		    boolean existsByMemberEmailAndMemberSeqNot(String email, Integer memberSeq);

		    @Query("select d.departmentName from Department d where d.departmentSeq = :seq")
		    String findDeptName(@Param("seq") int seq);



}
