package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.DepartmentNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.StudentNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.UserNotFoundException;
import com.bnwzy.smartclassesspringbootweb.pojo.Department;
import com.bnwzy.smartclassesspringbootweb.pojo.Student;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentUpdateDTO;
import com.bnwzy.smartclassesspringbootweb.repository.DepartmentRepository;
import com.bnwzy.smartclassesspringbootweb.repository.StudentRepository;
import com.bnwzy.smartclassesspringbootweb.repository.UserRepository;
import com.bnwzy.smartclassesspringbootweb.service.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public Student getStudentById(Long id) {
        if (!studentRepository.findById(id).isPresent()) {
            throw new StudentNotFoundException("<Student Not Found>");
        } else {
            return studentRepository.findById(id).get();
        }
    }

    @Override
    public Student updateStudent(StudentUpdateDTO studentUpdateDTO) {
        if (!studentRepository.findById(studentUpdateDTO.getId()).isPresent()) {
            throw new StudentNotFoundException("<Student Not Found>");
        } else {
            Student student = studentRepository.findById(studentUpdateDTO.getId()).get();
            student.setName(studentUpdateDTO.getName());
            student.setGender(studentUpdateDTO.getGender());
            if (!departmentRepository.findById(studentUpdateDTO.getDeptId()).isPresent()) {
                throw new DepartmentNotFoundException("<Department Not Found>");
            } else {
                Department department = departmentRepository.findById(studentUpdateDTO.getDeptId()).get();
                student.setDepartment(department);
            }
            student.setGpa(studentUpdateDTO.getGpa());
            return studentRepository.save(student);
        }
    }

    @Override
    public Student getStudentByUsername(String username) {
        if (!studentRepository.findByUsername(username).isPresent()) {
            throw new StudentNotFoundException("<Student Not Found>");
        } else {
            return studentRepository.findByUsername(username).get();
        }
    }

    @Override
    public List<Student> getAllStudent() {
        List<Student> studentList = new ArrayList<>();
        studentRepository.findAll().forEach(studentList::add);
        return studentList;
    }

    @Override
    public Student createStudent(StudentCreateDTO studentCreateDTO) {
        if (!userRepository.findByUsername(studentCreateDTO.getUsername()).isPresent()) {
            throw new UserNotFoundException("<User Not Found>");
        } else {
            Student student = new Student();
            if (!departmentRepository.findById(studentCreateDTO.getDeptId()).isPresent()) {
                throw new DepartmentNotFoundException("<Department Not Found>");
            } else {
                Department department = departmentRepository.findById(studentCreateDTO.getDeptId()).get();
                student.setDepartment(department);
            }
            student.setName(studentCreateDTO.getName());
            student.setGender(studentCreateDTO.getGender());
            student.setGpa(studentCreateDTO.getGpa());
            student.setUsername(studentCreateDTO.getUsername());
            studentRepository.save(student);
            return student;
        }
    }

    @Override
    public boolean deleteStudent(Long id) {
        if (!studentRepository.findById(id).isPresent()) {
            throw new StudentNotFoundException("<Student Not Found>");
        } else {
            studentRepository.deleteById(id); // 部门不会被删除
            // 在删除部门时，应先解除关系再删除部门
//            public void deleteDepartment(Long departmentId) {
//                // 查找关联的学生
//                Optional<Student> studentOpt = studentRepository.findByDepartmentId(departmentId);
//
//                if(studentOpt.isPresent()) {
//                    Student student = studentOpt.get();
//                    student.setDepartment(null); // 解除关系
//                    studentRepository.save(student);
//                }
//
//                departmentRepository.deleteById(departmentId);
//            }
            return true;
        }
    }
}
