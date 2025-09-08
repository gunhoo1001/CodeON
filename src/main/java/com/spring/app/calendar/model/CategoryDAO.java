package com.spring.app.calendar.model;

import java.util.List;

import com.spring.app.calendar.domain.BigCategoryDTO;
import com.spring.app.calendar.domain.SmallCategoryDTO;

public interface CategoryDAO {

	// 대분류 
	List<BigCategoryDTO> selectAllBigCategories();
	// 소분류 
	List<SmallCategoryDTO> selectAllSmallCategories();

}
