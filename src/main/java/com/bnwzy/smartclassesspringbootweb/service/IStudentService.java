package com.bnwzy.smartclassesspringbootweb.service;

import com.bnwzy.smartclassesspringbootweb.pojo.Student;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentUpdateDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IStudentService {
    Student getStudentById(Long id);

    Student updateStudent(StudentUpdateDTO studentUpdateDTO);

    Student getStudentByUsername(String username);

    List<Student> getAllStudent();

    Student createStudent(StudentCreateDTO studentCreateDTO);

    Boolean deleteStudent(Long id);

    Long getStudentCount();

    List<Student> getStudentOfDept(Long deptId);
}
