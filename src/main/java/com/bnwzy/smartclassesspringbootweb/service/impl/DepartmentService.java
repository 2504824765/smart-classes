package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.DepartmentAlreadyExistException;
import com.bnwzy.smartclassesspringbootweb.exception.DepartmentNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.IlligalParentDeptException;
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
            // 如果是顶级组织，则父级组织id为0
            if (deptCreateDTO.getDepartmentLevel() == 0) {
                department.setParentId((long) 0);
            }
            // 如果不是顶级组织，寻找父级组织存不存在
            else if (departmentRepository.findById(deptCreateDTO.getParentId()).isPresent()) {
                Department parentDepartment = departmentRepository.findById(deptCreateDTO.getParentId()).get();
                // 如果选择的父组织不是上一级组织
                if (parentDepartment.getDepartmentLevel() != deptCreateDTO.getDepartmentLevel() - 1) {
                    throw new IlligalParentDeptException("<Target department is not parentDepartment>");
                } else {
                    department.setParentId(deptCreateDTO.getParentId());
                }
            } else {
                throw new DepartmentNotFoundException("<Parent department not found>");
            }
            department.setDepartmentLevel(deptCreateDTO.getDepartmentLevel());
            return departmentRepository.save(department);
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
    public Department updateDept(DeptUpdateDTO newDeptDTO) {
        if (!departmentRepository.findById(newDeptDTO.getId()).isPresent()) {
            throw new DepartmentNotFoundException("<Department not found>");
        } else {
            Department oldDepartment = departmentRepository.findById(newDeptDTO.getId()).get();
            // 判断是否是顶级组织
            if (newDeptDTO.getDepartmentLevel() == 0) {
                oldDepartment.setParentId((long) 0);
            }
            // 判断目标父组织存不存在
            else if (!departmentRepository.findById(newDeptDTO.getParentId()).isPresent()) {
                throw new DepartmentNotFoundException("<Parent oldDepartment not found>");
            } else {
                // 如果存在，判断是否为上一级
                Department parentDepartment = departmentRepository.findById(newDeptDTO.getParentId()).get();
                if (!newDeptDTO.getDepartmentLevel().equals(parentDepartment.getDepartmentLevel() + 1)) {
                    throw new IlligalParentDeptException("<Target oldDepartment is not parentDepartment>");
                } else {
                    oldDepartment.setParentId(newDeptDTO.getParentId());
                }
            }
            oldDepartment.setName(newDeptDTO.getName());
            oldDepartment.setDepartmentLevel(newDeptDTO.getDepartmentLevel());
            return departmentRepository.save(oldDepartment);
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

    @Override
    public Department getDeptByName(String name) {
        if (!departmentRepository.findByName(name).isPresent()) {
            throw new DepartmentNotFoundException("<Department not found>");
        } else {
            return departmentRepository.findByName(name).get();
        }
    }
}
