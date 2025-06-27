package com.bnwzy.smartclassesspringbootweb.service;

import com.bnwzy.smartclassesspringbootweb.pojo.Department;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.DeptCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.DeptUpdateDTO;
import org.springframework.stereotype.Service;

@Service
public interface IDepartmentService {
    Department createDept(DeptCreateDTO deptCreateDTO);

    Boolean deleteDept(Long id);

    Department updateDept(DeptUpdateDTO deptUpdateDTO);
}
