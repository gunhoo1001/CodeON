package com.spring.app.attendance.service;

import com.spring.app.attendance.domain.AttendanceRecord;
import com.spring.app.attendance.domain.WorkSummary;
import com.spring.app.attendance.model.AttendanceDAO;
import com.spring.app.domain.AnnualLeaveDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AttendanceService_imple implements AttendanceService {

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");
    private static final int STANDARD_MIN = 480; // 8시간 기준

    private final AttendanceDAO attendanceDAO;

    @Transactional
    @Override
    public void startWork(int memberSeq) {
        LocalDate today = LocalDate.now(ZONE);	// 한국 기준으로 오늘 날짜(LocalDate)를 가져옴. 시간은 제와하고 년-월-일만.
        // 오늘 날짜와 해당 직원 번호로 근태 테이블 조회.
        // 반환값이 없으면 -> 오늘 첫 기록이 없음
        // 반환값이 있으면 -> 이미 출근 또는 퇴근 기록이 있을 수 있음
        var rec = attendanceDAO.selectByMemberAndDate(memberSeq, today);
        if (rec == null) {
        	// 오늘 처음 출근 기록
            attendanceDAO.insertStart(memberSeq, today);
        } else if (rec.getStartTime() == null) {
        	// 오늘 기록은 있으나 출근 시간이 아직 없음 -> 업데이트
            attendanceDAO.updateStartIfNull(memberSeq, today);
        }
    }

    @Transactional
    @Override
    public void endWork(int memberSeq) {
        LocalDate today = LocalDate.now(ZONE);

        var rec = attendanceDAO.selectByMemberAndDate(memberSeq, today);
        if (rec == null || rec.getStartTime() == null) {
            throw new IllegalStateException("출근 기록이 없습니다.");
        }
        if (rec.getEndTime() != null) {
            // 이미 퇴근 처리되어 있으면 재계산/저장 불필요
            return;
        }

        LocalDateTime now = LocalDateTime.now(ZONE);

        // 원시 총 근무분
        long total = Duration.between(rec.getStartTime(), now).toMinutes();
        if (total < 0) total = 0;

        // 4시간 초과 시 60분 차감
        long adjusted = (total > 240 ? total - 60 : total);
        if (adjusted < 0) adjusted = 0;

        // 연장근무 = (조정근무분 - 480) 이상만
        int overtime = (int) Math.max(0, adjusted - 480);

        // 저장: Mapper에서 END_TIME이 NULL인 경우에만 세팅하도록 방지 조건도 함께 추가
        attendanceDAO.updateEnd(memberSeq, today, now, overtime);
    }


    @Override
    public List<AttendanceRecord> getMonthly(int memberSeq, YearMonth ym) {
        List<AttendanceRecord> list = attendanceDAO.selectMonthly(memberSeq, ym);
        for (AttendanceRecord att : list) {
            if (att.getStartTime() != null && att.getEndTime() != null) {	// 출퇴근 시간이 모두 있는 경우에만
            	// 총 근무시간 계산하여 4h 초과시 60분 차감 (점심시간 공제)
                long minutes = Duration.between(att.getStartTime(), att.getEndTime()).toMinutes();
                if (minutes < 0) minutes = 0;
                long adjusted = (minutes > 240 ? minutes - 60 : minutes); 
                att.setWorkedMinutes((int) adjusted);

                long h = adjusted / 60;
                long m = adjusted % 60;
                att.setWorkedTimeStr(String.format("%02d:%02d", h, m));
            } else {	// 출퇴근 시간이 없다면 null 처리
                att.setWorkedMinutes(null);
                att.setWorkedTimeStr(null);
            }
        }
        return list;
    }
    
    private static String toHHmm(int minutes) {
        int h = minutes / 60, m = minutes % 60;
        return String.format("%02d:%02d", h, m);
    }

    public WorkSummary getMonthlySummary(int memberSeq, YearMonth ym) {
        Map<String, Object> m = attendanceDAO.selectMonthlySummary(memberSeq, ym);

        // ✅ 결과 행 자체가 없을 때 방어
        if (m == null) {
            WorkSummary s = new WorkSummary();
            s.setTotalMinutes(0);
            s.setTotalMinutesStr("00:00");
            s.setWorkDays(0);
            s.setTotalOvertime(0);
            return s;
        }

        // ✅ 각 컬럼이 NULL일 수도 있으니 안전 변환
        int totalMinutes  = toInt(m.get("TOTAL_MINUTES"));
        int workDays      = toInt(m.get("WORK_DAYS"));
        int totalOvertime = toInt(m.get("TOTAL_OVERTIME"));

        WorkSummary s = new WorkSummary();
        s.setTotalMinutes(totalMinutes);
        s.setTotalMinutesStr(toHHmm(totalMinutes));
        s.setWorkDays(workDays);
        s.setTotalOvertime(totalOvertime);
        return s;
    }

    private static int toInt(Object v) {
        if (v == null) return 0;
        if (v instanceof Number) return ((Number) v).intValue();
        try { return Integer.parseInt(String.valueOf(v)); } catch (Exception e) { return 0; }
    }

    @Override
    public AnnualLeaveDTO getAnnualLeave(int memberSeq) {
        AnnualLeaveDTO dto = attendanceDAO.selectAnnualLeaveByMemberSeq(memberSeq);
        if (dto == null) {	// 새입사자나 기록이 없는 사람도 기본값(0,0,0)으로 새 DTO를 만들어 반환해야 오류없이 페이지를 보여줌
            dto = AnnualLeaveDTO.builder()
                    .memberSeq(memberSeq)
                    .totalLeave(0).usedLeave(0).remainingLeave(0)
                    .build();
        }
        return dto;
    }


}
