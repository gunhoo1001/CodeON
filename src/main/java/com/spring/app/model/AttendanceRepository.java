package com.spring.app.model;

import com.spring.app.entity.Attendance;
import com.spring.app.domain.AttendanceRowView;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    /** 페이징 조회: 월 범위 + 부서/직급 필터 */
    @Query(
        value = """
            select new com.spring.app.domain.AttendanceRowView(
                a.workDate,
                m.memberName,
                m.memberSeq,
                a.startTime,
                a.endTime,
                a.overtime,
                al.usedLeave,
                al.remainingLeave
            )
            from Attendance a
              join Member m on m.memberSeq = a.memberSeq
              left join m.department d
              left join m.grade g
              left join AnnualLeave al on al.memberSeq = m.memberSeq
            where a.workDate between :from and :to
              and (:deptSeq  is null or d.departmentSeq = :deptSeq)
              and (:gradeSeq is null or g.gradeSeq      = :gradeSeq)
            order by a.workDate asc, m.memberName asc
        """,
        countQuery = """
            select count(a)
            from Attendance a
              join Member m on m.memberSeq = a.memberSeq
              left join m.department d
              left join m.grade g
            where a.workDate between :from and :to
              and (:deptSeq  is null or d.departmentSeq = :deptSeq)
              and (:gradeSeq is null or g.gradeSeq      = :gradeSeq)
        """
    )
    Page<AttendanceRowView> findPageForMonth(
        @Param("from") LocalDate from,
        @Param("to")   LocalDate to,
        @Param("deptSeq")  Long deptSeq,       // Department.departmentSeq = Long
        @Param("gradeSeq") Long gradeSeq,   // Grade.gradeSeq = int → Integer
        Pageable pageable
    );

    /** 엑셀용 전체 조회(비페이징): 월 범위 + 부서/직급 필터, 정렬은 Sort로 전달 */
    @Query("""
    	    select new com.spring.app.domain.AttendanceRowView(
    	        a.workDate,
    	        m.memberName,
    	        m.memberSeq,
    	        a.startTime,
    	        a.endTime,
    	        a.overtime,
    	        al.usedLeave,
    	        al.remainingLeave
    	    )
    	    from Attendance a
    	      join Member m on m.memberSeq = a.memberSeq
    	      left join m.department d
    	      left join m.grade g
    	      left join AnnualLeave al on al.memberSeq = m.memberSeq
    	    where a.workDate between :from and :to
    	      and (:deptSeq  is null or d.departmentSeq = :deptSeq)
    	      and (:gradeSeq is null or g.gradeSeq      = :gradeSeq)
    	    order by a.workDate asc, m.memberName asc
    	""")
    	List<AttendanceRowView> findAllForMonth(
    	    @Param("from") LocalDate from,
    	    @Param("to")   LocalDate to,
    	    @Param("deptSeq")  Long deptSeq,
    	    @Param("gradeSeq") Integer gradeSeq
    	);
}
