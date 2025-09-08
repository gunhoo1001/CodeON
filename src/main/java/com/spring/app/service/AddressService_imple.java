package com.spring.app.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.spring.app.domain.AddressDTO;
import com.spring.app.entity.Department;
import com.spring.app.model.DepartmentRepository;
import com.spring.app.model.MemberRepository;
import org.springframework.data.domain.Pageable;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressService_imple implements AddressService {
	
	private final DepartmentRepository departmentRepository;
    private final MemberRepository memberRepository;

    @Override
    public List<Department> departments() {
    	// 부서 목록을 departmentSeq 기준 오름차순 정렬해서 조회
        return departmentRepository.findAll(Sort.by(Sort.Order.asc("departmentSeq")));
    }

    @Override
    public Page<AddressDTO> search(Integer dept, String kw, int page, int size) {
    	// PageRequest.of()는 0-based 인덱스 → page-1 처리
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);
        // dept가 null일 경우를 위해 Long 변환
        Long deptParam = (dept == null ? null : dept.longValue());
        // MemberRepository의 커스텀 검색 쿼리 실행
        return memberRepository.searchAddress(deptParam, kw, pageable);
    }

}
