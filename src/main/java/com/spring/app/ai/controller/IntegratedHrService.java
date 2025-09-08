package com.spring.app.ai.controller;

import com.spring.app.domain.AttendanceDTO;
import com.spring.app.domain.DraftDTO;
import com.spring.app.domain.MemberDTO;
import com.spring.app.domain.VacationDTO;
import com.spring.app.entity.*;
import com.spring.app.service.MemberService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IntegratedHrService {

    private final JPAQueryFactory jpaQueryFactory;
    private final MemberService memberService;

    private final QMember member = QMember.member;
    private final QVacation vacation = QVacation.vacation;
    private final QDraft draft = QDraft.draft;
    private final QAttendance attendance = QAttendance.attendance;

    // 전체 사원 조회
    public List<MemberDTO> findAllMembers() {
        List<Member> members = jpaQueryFactory.selectFrom(member).fetch();
        return members.stream().map(Member::toDTO).collect(Collectors.toList());
    }

    // 전체 연차 조회
    public List<VacationDTO> findAllVacations() {
        List<Vacation> vacations = jpaQueryFactory.selectFrom(vacation).fetch();

        // Member 매핑: memberSeq 기준
        Map<Integer, MemberDTO> memberMap = memberService.findAll()
                .stream()
                .collect(Collectors.toMap(MemberDTO::getMemberSeq, m -> m));

        return vacations.stream()
                .map(v -> VacationDTO.builder()
                        .draftSeq(v.getDraftSeq())
                        .vacationTitle(v.getVacationTitle())
                        .vacationType(v.getVacationType())
                        .vacationStart(v.getVacationStart())
                        .vacationEnd(v.getVacationEnd())
                        .vacationContent(v.getVacationContent())
                        // Member 참조 없음 → null 처리
                        .member(null)
                        .build())
                .collect(Collectors.toList());
    }

    // 전체 Draft 조회
    public List<DraftDTO> findAllDrafts() {
        List<Draft> drafts = jpaQueryFactory.selectFrom(draft).fetch();
        return drafts.stream()
                .map(d -> DraftDTO.builder()
                        .draftSeq(d.getDraftSeq().intValue())
                        .fkDraftTypeSeq((int) (long) d.getDraftType().getDraftTypeSeq())
                        .fkMemberSeq(d.getMember().getMemberSeq())
                        .draftTitle(d.getDraftTitle())
                        .draftContent(d.getDraftContent())
                        .draftStatus(d.getDraftStatus() != null ? d.getDraftStatus() : 0)
                        .isEmergency(d.getIsEmergency() != null ? d.getIsEmergency() : 0)
                        .draftRegdate(d.getDraftRegdate() != null ? d.getDraftRegdate().toString() : null)
                        .build())
                .collect(Collectors.toList());
    }

    // 전체 출퇴근 조회
    public List<AttendanceDTO> findAllAttendances() {
        List<Attendance> attendances = jpaQueryFactory.selectFrom(attendance).fetch();

        // Member 매핑: memberSeq 기준
        Map<Integer, MemberDTO> memberMap = memberService.findAll()
                .stream()
                .collect(Collectors.toMap(MemberDTO::getMemberSeq, m -> m));

        return attendances.stream()
                .map(a -> AttendanceDTO.builder()
                        .attendanceSeq(a.getAttendanceSeq())
                        .member(memberMap.getOrDefault(a.getMemberSeq(), null))
                        .workDate(a.getWorkDate())
                        .startTime(a.getStartTime())
                        .endTime(a.getEndTime())
                        .overtime(a.getOvertime())
                        .build())
                .collect(Collectors.toList());
    }
}
