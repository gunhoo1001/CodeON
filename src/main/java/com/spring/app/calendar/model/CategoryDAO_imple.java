package com.spring.app.calendar.model;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.spring.app.calendar.domain.BigCategoryDTO;
import com.spring.app.calendar.domain.SmallCategoryDTO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CategoryDAO_imple implements CategoryDAO {

	@Qualifier("sqlsession")
	private final SqlSessionTemplate sqlsession;
	
	@Override
    public List<BigCategoryDTO> selectAllBigCategories() {
        // selectList 사용, 파라미터 없으므로 null
        return sqlsession.selectList("calendar.selectAllBigCategories");
    }

    @Override
    public List<SmallCategoryDTO> selectAllSmallCategories() {
        // 소분류 조회
        return sqlsession.selectList("calendar.selectAllSmallCategories");
    }

}
