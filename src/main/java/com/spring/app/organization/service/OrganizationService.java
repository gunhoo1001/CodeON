package com.spring.app.organization.service;

import java.util.List;

import com.spring.app.domain.MemberDTO;

public interface OrganizationService {

	List<MemberDTO> getAllMembersWithDeptAndGrade();

}
