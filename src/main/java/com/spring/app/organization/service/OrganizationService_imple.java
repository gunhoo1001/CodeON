package com.spring.app.organization.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.app.domain.MemberDTO;
import com.spring.app.organization.model.OrganizationDAO;
@Service
public class OrganizationService_imple implements OrganizationService {

	@Autowired
	private OrganizationDAO dao;
	
	@Override
	public List<MemberDTO> getAllMembersWithDeptAndGrade() {
		return dao.selectAllMembersWithDeptAndGrade();
	}

}
