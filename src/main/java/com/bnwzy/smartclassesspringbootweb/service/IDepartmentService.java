package com.bnwzy.smartclassesspringbootweb.service;

import com.bnwzy.smartclassesspringbootweb.pojo.Department;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.DeptCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.DeptUpdateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.DeptDetailDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IDepartmentService {
    Department createDept(DeptCreateDTO deptCreateDTO);

    Boolean deleteDept(Long id);

    Department updateDept(DeptUpdateDTO deptUpdateDTO);

    List<Department> getAllDept();

    Department getDeptById(Long id);

    Department getDeptByName(String name);

    DeptDetailDTO getDeptDetailById(Long id);
}
