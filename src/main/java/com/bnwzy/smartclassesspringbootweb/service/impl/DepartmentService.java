package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.DepartmentAlreadyExistException;
import com.bnwzy.smartclassesspringbootweb.exception.DepartmentNotFoundException;
import com.bnwzy.smartclassesspringbootweb.pojo.Department;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.DeptCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.DeptUpdateDTO;
import com.bnwzy.smartclassesspringbootweb.repository.DepartmentRepository;
import com.bnwzy.smartclassesspringbootweb.service.IDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public Boolean deleteDept(Long id) {
        if (departmentRepository.findById(id).isPresent()) {
            departmentRepository.deleteById(id);
            return true;
        } else {
            throw new DepartmentNotFoundException("<Department not found>");
        }
    }

    @Override
    public Department updateDept(DeptUpdateDTO deptUpdateDTO) {
        if (!departmentRepository.findById(deptUpdateDTO.getParentId()).isPresent()) {
            throw new DepartmentNotFoundException("<Department not found>");
        } else {
            Department department = departmentRepository.findById(deptUpdateDTO.getParentId()).get();
            if (!departmentRepository.findById(deptUpdateDTO.getParentId()).isPresent()) {
                throw new DepartmentNotFoundException("<Parent department not found>");
            } else {
                department.setParentId(deptUpdateDTO.getParentId());
            }
            department.setName(deptUpdateDTO.getName());
            department.setDepartmentLevel(deptUpdateDTO.getDepartmentLevel());
            departmentRepository.save(department);
            return department;
        }
    }

    @Override
    public List<Department> getAllDept() {
        List<Department> departmentList = departmentRepository.findAll();
        return departmentList;
    }

    @Override
    public Department getDeptById(Long id) {
        if (!departmentRepository.findById(id).isPresent()) {
            throw new DepartmentNotFoundException("<Department not found>");
        } else {
            return departmentRepository.findById(id).get();
        }
    }
}
