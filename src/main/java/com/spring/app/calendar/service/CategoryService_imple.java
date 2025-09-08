package com.spring.app.calendar.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spring.app.calendar.domain.BigCategoryDTO;
import com.spring.app.calendar.domain.SmallCategoryDTO;
import com.spring.app.calendar.model.CategoryDAO;


@Service
public class CategoryService_imple implements CategoryService {

	 private final CategoryDAO dao;

	 
	 
	 // 생성자 주입
    	public CategoryService_imple(CategoryDAO dao) {
    		this.dao = dao;
    	}

    	@Override
    	public List<BigCategoryDTO> getAllBigCategories() {
    		return dao.selectAllBigCategories(); // dao를 통해 조회
    	}

	    @Override
	    public List<SmallCategoryDTO> getAllSmallCategories() {
	        return dao.selectAllSmallCategories(); // dao를 통해 조회
	    }
}
