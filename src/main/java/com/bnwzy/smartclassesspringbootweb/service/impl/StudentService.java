package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.StudentNotFoundException;
import com.bnwzy.smartclassesspringbootweb.pojo.Student;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentLoginDTO;
import com.bnwzy.smartclassesspringbootweb.repository.StudentRepository;
import com.bnwzy.smartclassesspringbootweb.service.IStudentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentService implements IStudentService {
    @Autowired
    StudentRepository studentRepository;

    @Override
    public Boolean registStudent(StudentCreateDTO studentCreateDTO) {
        Student student = new Student();
        BeanUtils.copyProperties(studentCreateDTO, student);
        studentRepository.save(student);
        return true;
    }

    @Override
    public Boolean loginStudent(StudentLoginDTO studentLoginDTO) {
        if (!studentRepository.findByUsername(studentLoginDTO.getUsername()).isPresent()) {
            throw new StudentNotFoundException("<Student Not Found>");
        } else {
            Student student = studentRepository.findByUsername(studentLoginDTO.getUsername()).get();
            return student.getPassword().equals(studentLoginDTO.getPassword());
        }
    }

    @Override
    public Student getStudentById(Long id) {
        if (!studentRepository.findById(id).isPresent()) {
            throw new StudentNotFoundException("<Student Not Found>");
        } else {
            return studentRepository.findById(id).get();
        }
    }
}
