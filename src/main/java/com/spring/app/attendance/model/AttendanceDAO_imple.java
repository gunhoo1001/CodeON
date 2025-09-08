package com.spring.app.attendance.model;

import com.spring.app.attendance.domain.AttendanceRecord;
import com.spring.app.domain.AnnualLeaveDTO;

import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class AttendanceDAO_imple implements AttendanceDAO {

    @Qualifier("sqlsession")
    private final SqlSessionTemplate sqlsession;

    @Override
    public AttendanceRecord selectByMemberAndDate(int memberSeq, LocalDate workDate) {
        Map<String,Object> p = new HashMap<>();
        p.put("memberSeq", memberSeq);
        p.put("workDate", workDate);
        return sqlsession.selectOne("attendance.selectByMemberAndDate", p);
    }

    @Override
    public int insertStart(int memberSeq, LocalDate workDate) {
        Map<String,Object> p = new HashMap<>();
        p.put("memberSeq", memberSeq);
        p.put("workDate", workDate);
        return sqlsession.insert("attendance.insertStart", p);
    }

    @Override
    public int updateStartIfNull(int memberSeq, LocalDate workDate) {
        Map<String,Object> p = new HashMap<>();
        p.put("memberSeq", memberSeq);
        p.put("workDate", workDate);
        return sqlsession.update("attendance.updateStartIfNull", p);
    }

    @Override
    public int updateEnd(int memberSeq, LocalDate workDate, LocalDateTime endTime, Integer overtime) {
        Map<String,Object> p = new HashMap<>();
        p.put("memberSeq", memberSeq);
        p.put("workDate", workDate);
        p.put("endTime", endTime);
        p.put("overtime", overtime);
        return sqlsession.update("attendance.updateEnd", p);
    }

    @Override
    public List<AttendanceRecord> selectMonthly(int memberSeq, YearMonth ym) {
        Map<String,Object> p = new HashMap<>();
        p.put("memberSeq", memberSeq);
        p.put("start", ym.atDay(1));		// 해당 연월의 1일을 반환
        p.put("end", ym.atEndOfMonth());	// 해당 연월의 마지막 날을 반환
        return sqlsession.selectList("attendance.selectMonthly", p);
    }
    
    public Map<String,Object> selectMonthlySummary(int memberSeq, YearMonth ym) {
        Map<String,Object> p = new HashMap<>();
        p.put("memberSeq", memberSeq);
        p.put("start", ym.atDay(1));
        p.put("end", ym.atEndOfMonth());
        return sqlsession.selectOne("attendance.selectMonthlySummary", p);
    }

    @Override
    public AnnualLeaveDTO selectAnnualLeaveByMemberSeq(int memberSeq) {
        return sqlsession.selectOne("attendance.selectAnnualLeaveByMemberSeq", memberSeq);
    }

}
