package com.spring.app.domain;

import java.time.LocalDate;

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
public class AddressDTO {

	private Long    deptSeq;     // 부서번호
    private String  deptName;    // 부서명
    private Integer memberSeq;   // 사번
    private String  name;        // 이름  (MEMBER_NAME)
    private String  email;       // 이메일(MEMBER_EMAIL)
    private String  mobile;      // 휴대폰(MEMBER_MOBILE)
    private String  userId;      // 아이디(MEMBER_USERID)
    private String  gradeName;   // 직급명 
    
}
