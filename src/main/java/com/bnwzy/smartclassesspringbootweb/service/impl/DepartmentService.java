package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.DepartmentAlreadyExistException;
import com.bnwzy.smartclassesspringbootweb.exception.DepartmentNotFoundException;
import com.bnwzy.smartclassesspringbootweb.pojo.Department;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.DeptCreateDTO;
import com.bnwzy.smartclassesspringbootweb.repository.DepartmentRepository;
import com.bnwzy.smartclassesspringbootweb.service.IDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DepartmentService implements IDepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public Department createDept(DeptCreateDTO deptCreateDTO) {
        if (departmentRepository.findByName(deptCreateDTO.getName()).isPresent()) {
            throw new DepartmentAlreadyExistException("<Department already exists>");
        } else {
            Department department = new Department();
            department.setName(deptCreateDTO.getName());
            if (departmentRepository.findById(deptCreateDTO.getParentId()).isPresent()) {
                department.setParentId(deptCreateDTO.getParentId());
            } else {
                throw new DepartmentNotFoundException("<Department not found>");
            }
            department.setDepartmentLevel(deptCreateDTO.getDepartmentLevel());
            departmentRepository.save(department);
            return department;
        }
    }
}
