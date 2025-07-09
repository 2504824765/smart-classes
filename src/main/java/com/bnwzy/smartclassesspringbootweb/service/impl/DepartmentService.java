package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.DepartmentAlreadyExistException;
import com.bnwzy.smartclassesspringbootweb.exception.DepartmentHasChildrenException;
import com.bnwzy.smartclassesspringbootweb.exception.DepartmentNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.IllegalParentDeptException;
import com.bnwzy.smartclassesspringbootweb.pojo.Department;
import com.bnwzy.smartclassesspringbootweb.pojo.Student;
import com.bnwzy.smartclassesspringbootweb.pojo.Teacher;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.DeptCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.DeptUpdateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.DeptDetailDTO;
import com.bnwzy.smartclassesspringbootweb.repository.DepartmentRepository;
import com.bnwzy.smartclassesspringbootweb.repository.StudentRepository;
import com.bnwzy.smartclassesspringbootweb.repository.TeacherRepository;
import com.bnwzy.smartclassesspringbootweb.service.IDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService implements IDepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private StudentRepository studentRepository;

    @Override
    public Department createDept(DeptCreateDTO deptCreateDTO) {
        if (departmentRepository.findByName(deptCreateDTO.getName()).isPresent()) {
            throw new DepartmentAlreadyExistException("<Department already exists>");
        } else {
            Department department = new Department();
            department.setName(deptCreateDTO.getName());
            if (deptCreateDTO.getParentId() == null || deptCreateDTO.getParentId() == 0) {
                department.setParentId(0L);
                department.setDepartmentLevel(0);
            } else if (departmentRepository.findById(deptCreateDTO.getParentId()).isPresent()) {
                department.setParentId(deptCreateDTO.getParentId());
                Department parent = departmentRepository.findById(deptCreateDTO.getParentId()).get();
                department.setDepartmentLevel(parent.getDepartmentLevel() + 1);
            } else {
                throw new DepartmentNotFoundException("<Parent department not found>");
            }
            return departmentRepository.save(department);
        }
    }

    @Override
    public Boolean deleteDept(Long id) {
        if (departmentRepository.findById(id).isPresent()) {
            Department department = departmentRepository.findById(id).get();
            List<Department> children = departmentRepository.findByParentId(id);
            if (children != null && !children.isEmpty()) {
                throw new DepartmentHasChildrenException("<该部门有子部门，不能直接删除！>");
            }
            List<Teacher> teacherList = teacherRepository.findByDepartment(department);
            if (!teacherList.isEmpty()) {
                for (Teacher teacher : teacherList) {
                    teacher.setDepartment(null);
                }
            }
            List<Student> studentList = studentRepository.findByDepartment(department);
            if (!studentList.isEmpty()) {
                for (Student student : studentList) {
                    student.setDepartment(null);
                }
            }
            departmentRepository.delete(department);
            return true;
        } else {
            throw new DepartmentNotFoundException("<Department not found>");
        }
    }

    @Override
    public Department updateDept(DeptUpdateDTO newDeptDTO) {
        if (departmentRepository.findById(newDeptDTO.getId()).isEmpty()) {
            throw new DepartmentNotFoundException("<Department not found>");
        } else {
            Department oldDepartment = departmentRepository.findById(newDeptDTO.getId()).get();
            if (newDeptDTO.getParentId() == null || newDeptDTO.getParentId() == 0) {
                oldDepartment.setParentId(0L);
                oldDepartment.setDepartmentLevel(0);
            } else if (departmentRepository.findById(newDeptDTO.getParentId()).isPresent()) {
                oldDepartment.setParentId(newDeptDTO.getParentId());
                Department parent = departmentRepository.findById(newDeptDTO.getParentId()).get();
                oldDepartment.setDepartmentLevel(parent.getDepartmentLevel() + 1);
            } else {
                throw new DepartmentNotFoundException("<Parent department not found>");
            }
            oldDepartment.setName(newDeptDTO.getName());
            return departmentRepository.save(oldDepartment);
        }
    }

    @Override
    public List<Department> getAllDept() {
        return departmentRepository.findAll();
    }

    @Override
    public Department getDeptById(Long id) {
        if (departmentRepository.findById(id).isEmpty()) {
            throw new DepartmentNotFoundException("<Department not found>");
        } else {
            return departmentRepository.findById(id).get();
        }
    }

    @Override
    public Department getDeptByName(String name) {
        if (departmentRepository.findByName(name).isEmpty()) {
            throw new DepartmentNotFoundException("<Department not found>");
        } else {
            return departmentRepository.findByName(name).get();
        }
    }

    @Override
    public DeptDetailDTO getDeptDetailById(Long id) {
        Department dept = departmentRepository.findById(id)
            .orElseThrow(() -> new DepartmentNotFoundException("<Department not found>"));
        List<Teacher> teachers = teacherRepository.findByDepartment(dept);
        List<Student> students = studentRepository.findByDepartment(dept);
        DeptDetailDTO dto = new DeptDetailDTO();
        dto.setDepartment(dept);
        dto.setTeachers(teachers);
        dto.setStudents(students);
        return dto;
    }
}
