package com.spring.app.organization.model;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.spring.app.domain.MemberDTO;


@Repository
public class OrganizationDAO_imple implements OrganizationDAO {

	 @Autowired
	    private SqlSessionTemplate sqlSession;

	    @Override
	    public List<MemberDTO> selectAllMembersWithDeptAndGrade() {
	        return sqlSession.selectList("organization.selectAllMembersWithDeptAndGrade");
	    }

}
