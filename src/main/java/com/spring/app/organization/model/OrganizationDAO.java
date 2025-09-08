package com.spring.app.organization.model;

import java.util.List;

import com.spring.app.domain.MemberDTO;

public interface OrganizationDAO {

	List<MemberDTO> selectAllMembersWithDeptAndGrade();

}
