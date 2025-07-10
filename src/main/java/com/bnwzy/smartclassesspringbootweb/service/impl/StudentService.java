package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.*;
import com.bnwzy.smartclassesspringbootweb.pojo.Department;
import com.bnwzy.smartclassesspringbootweb.pojo.Student;
import com.bnwzy.smartclassesspringbootweb.pojo.StudentData;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentUpdateDTO;
import com.bnwzy.smartclassesspringbootweb.repository.*;
import com.bnwzy.smartclassesspringbootweb.service.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentService implements IStudentService {
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    StudentClassesRepository studentClassesRepository;
    @Autowired
    private StudentDataRepository studentDataRepository;

    @Override
    public Student getStudentById(Long id) {
        if (studentRepository.findById(id).isEmpty()) {
            throw new StudentNotFoundException("<Student Not Found>");
        } else {
            return studentRepository.findById(id).get();
        }
    }

    @Override
    public Student updateStudent(StudentUpdateDTO studentUpdateDTO) {
        if (studentRepository.findById(studentUpdateDTO.getId()).isEmpty()) {
            throw new StudentNotFoundException("<Student Not Found>");
        } else {
            Student student = studentRepository.findById(studentUpdateDTO.getId()).get();
            if (studentUpdateDTO.getName() != null) {
                student.setName(studentUpdateDTO.getName());
            }
            if  (studentUpdateDTO.getGender() != null) {
                student.setGender(studentUpdateDTO.getGender());
            }
            if (studentUpdateDTO.getDeptId() != null) {
                if (departmentRepository.findById(studentUpdateDTO.getDeptId()).isEmpty()) {
                    throw new DepartmentNotFoundException("<Department Not Found>");
                } else {
                    Department department = departmentRepository.findById(studentUpdateDTO.getDeptId()).get();
                    student.setDepartment(department);
                }
            }
            if (studentUpdateDTO.getGender() != null) {
                student.setGpa(studentUpdateDTO.getGpa());
            }
            if (studentUpdateDTO.getStudentDataId() != null) {
                if (studentDataRepository.findById(studentUpdateDTO.getStudentDataId()).isEmpty()) {
                    throw new StudentDataNotFoundException("<StudentData Not Found>");
                } else {
                    StudentData studentData = studentDataRepository.findById(studentUpdateDTO.getStudentDataId()).get();
                    student.setStudentData(studentData);
                }
            }
            return studentRepository.save(student);
        }
    }

    @Override
    public Student getStudentByUsername(String username) {
        if (studentRepository.findByUsername(username).isEmpty()) {
            throw new StudentNotFoundException("<Student Not Found>");
        } else {
            return studentRepository.findByUsername(username).get();
        }
    }

    @Override
    public List<Student> getAllStudent() {
        return new ArrayList<>(studentRepository.findAll());
    }

    @Override
    public Student createStudent(StudentCreateDTO studentCreateDTO) {
        if (userRepository.findByUsername(studentCreateDTO.getUsername()).isEmpty()) {
            throw new UserNotFoundException("<User Not Found>");
        } else {
            Student student = new Student();
            if (departmentRepository.findById(studentCreateDTO.getDeptId()).isEmpty()) {
                throw new DepartmentNotFoundException("<Department Not Found>");
            } else {
                Department department = departmentRepository.findById(studentCreateDTO.getDeptId()).get();
                student.setDepartment(department);
            }
            student.setName(studentCreateDTO.getName());
            student.setGender(studentCreateDTO.getGender());
            student.setGpa(studentCreateDTO.getGpa());
            student.setUsername(studentCreateDTO.getUsername());
            if (studentDataRepository.findById(studentCreateDTO.getStudentDataId()).isEmpty()) {
                throw new StudentDataNotFoundException("<StudentData Not Found>");
            } else {
                StudentData studentData = studentDataRepository.findById(studentCreateDTO.getStudentDataId()).get();
                student.setStudentData(studentData);
                return studentRepository.save(student);
            }
        }
    }

    @Override
    @Transactional
    public Boolean deleteStudent(Long id) {
        if (studentRepository.findById(id).isEmpty()) {
            throw new StudentNotFoundException("<Student Not Found>");
        } else {
            // 先删除所有与该学生相关的选课关联
            studentClassesRepository.deleteByStudent_Id(id);
            // 再删除学生本身
            studentRepository.deleteById(id);
            return true;
        }
    }

    @Override
    public Long getStudentCount() {
        return studentRepository.count();
    }

    @Override
    public List<Student> getStudentOfDept(Long deptId) {
        if (departmentRepository.findById(deptId).isEmpty()) {
            throw new DepartmentNotFoundException("<Department Not Found>");
        } else {
            return studentRepository.findByDepartment(departmentRepository.findById(deptId).get());
        }
    }
}
