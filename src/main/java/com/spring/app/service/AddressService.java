// com.spring.app.service.AddressService
package com.spring.app.service;

import com.spring.app.domain.AddressDTO;
import com.spring.app.entity.Department;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AddressService {
    List<Department> departments();
    Page<AddressDTO> search(Integer dept, String kw, int page, int size);
}
