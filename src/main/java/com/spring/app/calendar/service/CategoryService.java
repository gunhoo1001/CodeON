package com.spring.app.calendar.service;

import java.util.List;

import com.spring.app.calendar.domain.BigCategoryDTO;
import com.spring.app.calendar.domain.SmallCategoryDTO;

public interface CategoryService {

	// 대분류, 소분류 리스트 가져오기
	List<BigCategoryDTO> getAllBigCategories();
	List<SmallCategoryDTO> getAllSmallCategories();

}
