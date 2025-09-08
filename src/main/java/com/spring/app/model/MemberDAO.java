package com.spring.app.model;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.spring.app.domain.MemberDTO;

@Mapper
public interface MemberDAO {

	List<Map<String, Object>> memberCntByDeptname();
    List<Map<String, Object>> memberCntByGender();
	
	// 입사연도별 인원/퍼센티지
	List<Map<String, Object>> memberCntByHireYear();

    // 입사연도×성별 분해
	List<Map<String, Object>> memberCntByHireYearGender();
	
	List<MemberDTO> findByDept(@Param("fkDepartmentSeq")int fkDepartmentSeq);
	

}
