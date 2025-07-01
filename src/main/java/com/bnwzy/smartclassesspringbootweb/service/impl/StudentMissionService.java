package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.ClassMissionNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.StudentMissionNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.StudentNotFoundException;
import com.bnwzy.smartclassesspringbootweb.pojo.ClassMission;
import com.bnwzy.smartclassesspringbootweb.pojo.ResponseMessage;
import com.bnwzy.smartclassesspringbootweb.pojo.Student;
import com.bnwzy.smartclassesspringbootweb.pojo.StudentMission;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentMissionCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentMissionUpdateDTO;
import com.bnwzy.smartclassesspringbootweb.repository.ClassMissionRepository;
import com.bnwzy.smartclassesspringbootweb.repository.StudentMissionRepository;
import com.bnwzy.smartclassesspringbootweb.repository.StudentRepository;
import com.bnwzy.smartclassesspringbootweb.service.IStudentMissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class StudentMissionService implements IStudentMissionService {
    @Autowired
    private StudentMissionRepository studentMissionRepository;

    @Autowired
    private ClassMissionRepository classMissionRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public StudentMission createStudentMission(StudentMissionCreateDTO studentMissionCreateDTO) {
        StudentMission studentMission = new StudentMission();
        if (studentRepository.findById(studentMissionCreateDTO.getStudentId()).isEmpty()) {
            throw new StudentNotFoundException("<Student not found>");
        } else {
            studentMission.setStudent(studentRepository.findById(studentMissionCreateDTO.getStudentId()).get());
        }
        if (classMissionRepository.findById(studentMissionCreateDTO.getClassMissionId()).isEmpty()) {
            throw new ClassMissionNotFoundException("<Class mission not found>");
        } else {
            studentMission.setClassMission(classMissionRepository.findById(studentMissionCreateDTO.getClassMissionId()).get());
        }
        studentMission.setScore(0);
        studentMission.setDone(false);
        studentMission.setActive(true);
        return studentMissionRepository.save(studentMission);
    }

    @Override
    public Boolean deleteStudentMission(Long id) {
        if (studentMissionRepository.findById(id).isPresent()) {
            studentMissionRepository.deleteById(id);
            return true;
        }  else {
            throw new StudentMissionNotFoundException("<Student mission not found>");
        }
    }

    @Override
    public StudentMission updateStudentMission(StudentMissionUpdateDTO studentMissionUpdateDTO) {
        if (studentMissionRepository.findById(studentMissionUpdateDTO.getId()).isEmpty()) {
            throw new StudentMissionNotFoundException("<Student mission not found>");
        } else {
            StudentMission studentMission = studentMissionRepository.findById(studentMissionUpdateDTO.getId()).get();
            if (studentRepository.findById(studentMissionUpdateDTO.getStudentId()).isEmpty()) {
                throw new StudentNotFoundException("<Student not found>");
            } else {
                Student student = studentRepository.findById(studentMissionUpdateDTO.getStudentId()).get();
                if (classMissionRepository.findById(studentMissionUpdateDTO.getClassMissionId()).isEmpty()) {
                    throw new ClassMissionNotFoundException("<Class mission not found>");
                } else {
                    ClassMission classmission = classMissionRepository.findById(studentMissionUpdateDTO.getClassMissionId()).get();
                    studentMission.setStudent(student);
                    studentMission.setClassMission(classmission);
                    studentMission.setScore(studentMissionUpdateDTO.getScore());
                    studentMission.setDone(studentMissionUpdateDTO.getDone());
                    studentMission.setActive(studentMissionUpdateDTO.getActive());
                    return studentMissionRepository.save(studentMission);
                }
            }
        }
    }

    @Override
    public StudentMission getStudentMissionById(Long id) {
        if (studentMissionRepository.findById(id).isPresent()) {
            return studentMissionRepository.findById(id).get();
        } else {
            throw new StudentMissionNotFoundException("<Student mission not found>");
        }
    }
}
