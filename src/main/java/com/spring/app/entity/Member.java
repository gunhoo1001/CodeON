package com.spring.app.entity;

import java.time.LocalDate;

import com.spring.app.domain.MemberDTO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TBL_MEMBER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Member {

	@Id
    @Column(name = "member_seq", length = 9)  // YYYY(4) + dept(2) + seq(3) 총 9자리 문자열
    private int memberSeq;

	@Column(name = "fk_grade_seq", nullable = false)
	private int fkGradeSeq;

	@Column(name = "fk_department_seq", nullable = false)
	private int fkDepartmentSeq;

    @Column(name = "member_name", nullable = false, length = 30)
    private String memberName;

    @Column(name = "member_userid", nullable = false, length = 30)
    private String memberUserid;

    @Column(name = "member_pwd", nullable = false, length = 30)
    private String memberPwd;

    @Column(name = "member_email", nullable = false, length = 50, unique = true)
    private String memberEmail;

    @Column(name = "member_salary")
    private Long memberSalary;

    @Column(name = "member_hiredate", nullable = false)
    private LocalDate memberHiredate;

    @Column(name = "member_birthday", nullable = false, length = 30)
    private String memberBirthday;

    @Column(name = "member_mobile", nullable = false, length = 30)
    private String memberMobile;
    
    @Column(name = "member_gender", nullable = false, length = 10)
    private int memberGender;

    @Column(name = "stamp_image", length = 120)
    private String stampImage;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_department_seq", referencedColumnName = "department_seq",
                insertable = false, updatable = false)   // ✅ 읽기 전용
    private Department department;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_grade_seq", referencedColumnName = "grade_seq",
                insertable = false, updatable = false)  // 읽기 전용
    private Grade grade;
    
    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private AnnualLeave annualLeave;
    
    // Entity를 DTO로 변환하기
    public MemberDTO toDTO() {
    	return MemberDTO.builder()
    			.memberSeq(this.memberSeq)
    			.fkGradeSeq(this.fkGradeSeq)
    			.fkDepartmentSeq(this.fkDepartmentSeq)
    			.memberName(this.memberName)
    			.memberUserid(this.memberUserid)
    			.memberPwd(this.memberPwd)
    			.memberEmail(this.memberEmail)
    			.memberSalary(this.memberSalary != null ? this.memberSalary : 0L)
    			.memberHiredate(this.memberHiredate)
    			.memberBirthday(this.memberBirthday)
    			.memberMobile(this.memberMobile)
    			.memberGender(this.memberGender)
    			.stampImage(this.stampImage)
    			.build();
    }
}



