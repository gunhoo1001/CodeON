package com.spring.app.service;

import java.util.List;

import com.spring.app.domain.MemberProfileDTO;
import com.spring.app.entity.Department;

public interface MyPageService {

    MemberProfileDTO getProfile(Integer memberSeq);

    List<Department> getDepartments();

    void updateProfile(Integer loginMemberSeq, MemberProfileDTO form);
    
    void changePassword(Integer memberSeq, String currentPwd, String newPwd);
}
