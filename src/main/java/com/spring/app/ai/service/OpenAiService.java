package com.spring.app.ai.service;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.spring.app.domain.AttendanceDTO;
import com.spring.app.domain.DraftDTO;
import com.spring.app.domain.MemberDTO;
import com.spring.app.domain.VacationDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final ChatClient chatClient;

    /**
     * HR 데이터 분석 요약
     */
    public String memberChat(List<MemberDTO> members) {
        StringBuilder sb = new StringBuilder();
        for (MemberDTO m : members) {
            sb.append(String.format("이름:%s, 부서:%d, 직급:%d, 입사일:%s, 성별:%d\n",
                    m.getMemberName(), m.getFkDepartmentSeq(), m.getFkGradeSeq(),
                    m.getMemberHiredate(), m.getMemberGender()));
        }

        String prompt = """
                당신은 HR 데이터 분석 도우미입니다.
                아래 사원 목록 데이터를 요약해 주세요:
                %s

                출력 형식:
                1. 전체 인원 수
                2. 부서별(10:인사팀, 20:개발팀, 30:기획팀, 40:영업팀, 50:고객지원팀) 인원 수
                   직급별(1:사원, 2:대리, 3:과장, 4:부장, 5:사장) 인원수 와 평균 근속 연수
                3. 성별(0:남자, 1:여자) 비율
                4. 관리자가 참고할 인사이트 (2~3줄)
                """.formatted(sb);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    public String analyzeHrData(
            List<MemberDTO> members,
            List<VacationDTO> vacations,
            List<DraftDTO> drafts,
            List<AttendanceDTO> attendances,
            String userQuestion // 사용자가 입력한 질문 추가
    ) {
        // 1. 사원 정보
    	// 부서 코드 → 이름 매핑
    	Map<Integer, String> departmentMap = Map.of(
    	    10, "인사팀",
    	    20, "개발팀",
    	    30, "기획팀",
    	    40, "영업팀",
    	    50, "고객지원팀"
    	);

    	// 직급 코드 → 이름 매핑
    	Map<Integer, String> gradeMap = Map.of(
    	    1, "사원",
    	    2, "대리",
    	    3, "과장",
    	    4, "부장",
    	    5, "사장"
    	);

    	StringBuilder memberInfo = new StringBuilder();
    	for (MemberDTO m : members) {
    	    String departmentName = departmentMap.getOrDefault(m.getFkDepartmentSeq(), "알수없음");
    	    String gradeName = gradeMap.getOrDefault(m.getFkGradeSeq(), "알수없음");

    	    memberInfo.append(String.format(
    	        "사원: %s, 부서: %s, 직급: %s, 입사일: %s, 성별: %s\n",
    	        m.getMemberName(),
    	        departmentName,
    	        gradeName,
    	        m.getMemberHiredate(),
    	        m.getMemberGender() == 0 ? "남자" : "여자"
    	    ));
    	}


        // 2. 연차 정보
        StringBuilder vacationInfo = new StringBuilder();
        for (VacationDTO v : vacations) {
            String memberName = v.getMember() != null ? v.getMember().getMemberName() : "미등록";
            vacationInfo.append(String.format(
                "사원:%s, 연차:%s(%s), 기간:%s~%s\n",
                memberName,
                v.getVacationTitle(),
                v.getVacationType(),
                v.getVacationStart(),
                v.getVacationEnd()
            ));
        }

        // 3. 문서 정보
        StringBuilder draftInfo = new StringBuilder();
        for (DraftDTO d : drafts) {
            draftInfo.append(String.format(
                "사원번호:%d, 문서:%s, 상태:%s, 긴급:%s, 등록일:%s\n",
                d.getFkMemberSeq(),
                d.getDraftTitle(),
                d.getDraftStatus() == 0 ? "대기" : (d.getDraftStatus() == 1 ? "진행" : "완료"),
                d.getIsEmergency() == 1 ? "긴급" : "보통",
                d.getDraftRegdate()
            ));
        }

        // 4. 출퇴근 정보
        StringBuilder attendanceInfo = new StringBuilder();
        for (AttendanceDTO a : attendances) {
            String memberName = a.getMember() != null ? a.getMember().getMemberName() : "미등록";
            String startTime = a.getStartTime() != null ? a.getStartTime().toLocalTime().toString() : "미등록";
            String endTime = a.getEndTime() != null ? a.getEndTime().toLocalTime().toString() : "미등록";
            Integer overtime = a.getOvertime() != null ? a.getOvertime() : 0;

            attendanceInfo.append(String.format(
                "사원:%s, 날짜:%s, 출근:%s, 퇴근:%s, 초과근무:%d분\n",
                memberName,
                a.getWorkDate(),
                startTime,
                endTime,
                overtime
            ));
        }

        // 사용자 질문을 포함한 프롬프트 생성
        String prompt = """
                당신은 회사 HR 종합 도우미입니다.
                
                1. 사원 정보:
                %s

                2. 연차 정보:
                %s

                3. 문서 정보:
                %s

                4. 출퇴근 정보:
                %s

                사용자 질문:
                %s

                요청:
                - 질문에 맞게 분석해서 답변
                - 필요 시 통계, 인사이트 포함
                """.formatted(memberInfo, vacationInfo, draftInfo, attendanceInfo, userQuestion);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }


}
