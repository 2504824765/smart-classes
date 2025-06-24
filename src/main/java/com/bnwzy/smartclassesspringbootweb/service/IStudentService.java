package com.bnwzy.smartclassesspringbootweb.service;

import com.bnwzy.smartclassesspringbootweb.pojo.Student;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentLoginDTO;
import org.springframework.stereotype.Service;

@Service
public interface IStudentService {
    Boolean registStudent(StudentCreateDTO studentCreateDTO);

    Boolean loginStudent(StudentLoginDTO studentLoginDTO);

    Student getStudentById(Long id);
}
