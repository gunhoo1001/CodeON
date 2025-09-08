package com.spring.app.domain;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.spring.app.entity.Department;

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
public class MemberDTO {
	
    private int memberSeq;
    private int fkGradeSeq;
    private int fkDepartmentSeq;
    private String memberName;
    private String memberUserid;
    private String memberPwd;
    private String memberEmail;
    private long memberSalary;
    
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate memberHiredate;
    private String memberMobile;
    private String memberBirthday;
    private int memberGender;
    private String stampImage;
    
    private Department department;
    
    
    private String gradeName;       // "사원", "대리" 같은 이름
    private int gradeSeq;           // 직급 순서
    private String departmentName;  // 부서명
}