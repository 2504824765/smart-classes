package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.ClassMissionNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.StudentNotFoundException;
import com.bnwzy.smartclassesspringbootweb.pojo.StudentMission;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentMissionCreateDTO;
import com.bnwzy.smartclassesspringbootweb.repository.ClassMissionRepository;
import com.bnwzy.smartclassesspringbootweb.repository.StudentMissionRepository;
import com.bnwzy.smartclassesspringbootweb.repository.StudentRepository;
import com.bnwzy.smartclassesspringbootweb.service.IStudentMissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
