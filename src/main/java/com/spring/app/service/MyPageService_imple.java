package com.spring.app.service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.app.domain.MemberProfileDTO;
import com.spring.app.entity.Department;
import com.spring.app.entity.Member;
import com.spring.app.model.DepartmentRepository;
import com.spring.app.model.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService_imple implements MyPageService {

    private final MemberRepository memberRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public MemberProfileDTO getProfile(Integer memberSeq) {
        return memberRepository.findProfileDtoByMemberSeq(memberSeq)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    @Override
    public List<Department> getDepartments() {
        return departmentRepository.findAll(Sort.by(Sort.Order.asc("departmentSeq")));
    }

    @Override
    @Transactional
    public void updateProfile(Integer loginMemberSeq, MemberProfileDTO form) {
        if (!loginMemberSeq.equals(form.getMemberSeq()))
            throw new IllegalStateException("본인 정보만 수정할 수 있습니다.");

        // 도메인 유효성 검증 (null/blank, 형식, 길이 등)
        assertNotBlank(form.getName(), "이름은 필수입니다.");
        assertRegex(form.getMobile(), "^\\d{2,3}-\\d{3,4}-\\d{4}$", "휴대폰 형식은 010-1234-5678 입니다.");

     // JPA가 DB에서 해당 회원 데이터를 꺼내와 Member 객체로 저장하고 세터로 값을 변경하면
     // JAP가 알아서 update 쿼리를 날려준다.
     // 2) 회원 로딩
        Member m = memberRepository.findById(form.getMemberSeq())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 3) 이메일 수정 불가 정책(서버 이중 방어)
        if (form.getEmail() != null && !Objects.equals(form.getEmail(), m.getMemberEmail())) {
            throw new IllegalArgumentException("이메일은 수정할 수 없습니다.");
        }
        
        m.setMemberName(form.getName());
        m.setMemberMobile(form.getMobile());
    }
    
    @Override
    @Transactional
    public void changePassword(Integer memberSeq, String currentPwd, String newPwd) {
        // 최소 유효성 검사
        assertNotBlank(currentPwd, "현재 비밀번호를 입력하세요.");
        assertNotBlank(newPwd, "새 비밀번호를 입력하세요.");
        if (newPwd.length() < 8 || newPwd.length() > 30)
            throw new IllegalArgumentException("새 비밀번호는 8~30자여야 합니다.");
        if (!newPwd.matches(".*[A-Za-z].*") || !newPwd.matches(".*\\d.*"))
            throw new IllegalArgumentException("새 비밀번호는 영문과 숫자를 조합해야 합니다.");
        
        // 바뀐 값이 있으면 → update member set member_pwd=? where member_seq=? SQL을 자동 실행
        Member m = memberRepository.findById(memberSeq)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 평문 비교(현재 구조 기준). 해싱 사용 시 BCrypt로 대체.
        if (!currentPwd.equals(m.getMemberPwd())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        if (currentPwd.equals(newPwd)) {
            throw new IllegalArgumentException("현재 비밀번호와 다른 비밀번호를 사용하세요.");
        }
        m.setMemberPwd(newPwd);
        // 트랜잭션 종료 시 flush
    }

    private static void assertNotBlank(String v, String msg){ if (v == null || v.trim().isEmpty()) throw new IllegalArgumentException(msg); }
    private static void assertRegex(String v, String regex, String msg){ if (v == null || !v.matches(regex)) throw new IllegalArgumentException(msg); }
    
    
}
