package com.spring.app.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberProfileDTO {

    private Integer memberSeq;   // 사번 (PK) — hidden input으로 폼에서 같이 전달됨

    // ===== 수정 가능 필드 =====
    private String  name;        // 이름 (member_name)
    private String  email;       // 이메일 (member_email)
    private String  mobile;      // 휴대폰 (member_mobile)

    // ===== 조회 전용 필드 =====
    private String  userId;      // 아이디 (member_userid)
    private Long    deptSeq;     // 부서번호 (fk_department_seq)
    private String  deptName;    // 부서명
    private Long    gradeSeq;    // 직급번호 (fk_grade_seq)
    private String  gradeName;   // 직급명
    private String  hiredate;    // 입사일 (yyyy-MM-dd 포맷)
}
