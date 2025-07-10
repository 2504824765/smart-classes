package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.ClassMissionNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.ClassesNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.StudentMissionNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.StudentNotFoundException;
import com.bnwzy.smartclassesspringbootweb.pojo.*;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.GetStudentMissionByStudentIdAndClassMissionIdDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentMissionCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentMissionUpdateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentsAllClassMissionGetDTO;
import com.bnwzy.smartclassesspringbootweb.repository.*;
import com.bnwzy.smartclassesspringbootweb.service.IStudentMissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentMissionService implements IStudentMissionService {
    @Autowired
    private StudentMissionRepository studentMissionRepository;

    @Autowired
    private ClassMissionRepository classMissionRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClassesRepository classesRepository;

    @Autowired
    private StudentClassesRepository studentClassesRepository;

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
            if (studentMissionUpdateDTO.getStudentId() != null) {
                if (studentRepository.findById(studentMissionUpdateDTO.getStudentId()).isEmpty()) {
                    throw new StudentNotFoundException("<Student not found>");
                } else {
                    Student student = studentRepository.findById(studentMissionUpdateDTO.getStudentId()).get();
                    studentMission.setStudent(student);
                }
            }
            if (studentMissionUpdateDTO.getClassMissionId() != null) {
                if (classMissionRepository.findById(studentMissionUpdateDTO.getClassMissionId()).isEmpty()) {
                    throw new ClassMissionNotFoundException("<Class mission not found>");
                } else {
                    ClassMission classmission = classMissionRepository.findById(studentMissionUpdateDTO.getClassMissionId()).get();
                    studentMission.setClassMission(classmission);
                }
            }
            if (studentMissionUpdateDTO.getScore() != null) {
                studentMission.setScore(studentMissionUpdateDTO.getScore());
            }
            if (studentMissionUpdateDTO.getDone() != null) {
                studentMission.setDone(studentMissionUpdateDTO.getDone());
            }
            if (studentMissionUpdateDTO.getActive() != null) {
                studentMission.setActive(studentMissionUpdateDTO.getActive());
            }
            if (studentMissionUpdateDTO.getReportUrl() != null) {
                studentMission.setReportUrl(studentMissionUpdateDTO.getReportUrl());
            }
            return studentMissionRepository.save(studentMission);
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

    @Override
    public List<StudentMission> getStudentsAllClassMission(StudentsAllClassMissionGetDTO studentsAllClassMissionGetDTO) {
        if (classesRepository.findById(studentsAllClassMissionGetDTO.getClassId()).isEmpty()) {
            throw new ClassesNotFoundException("<Class not found>");
        } else {
            Classes classes =  classesRepository.findById(studentsAllClassMissionGetDTO.getClassId()).get();
            List<ClassMission> classMissionList = classMissionRepository.findByClasses_Id(classes.getId());
            if (studentRepository.findById(studentsAllClassMissionGetDTO.getStudentId()).isEmpty()) {
                throw new StudentNotFoundException("<Student not found>");
            } else {
                Student student = studentRepository.findById(studentsAllClassMissionGetDTO.getStudentId()).get();
                return studentMissionRepository.findByClassMissionInAndStudent(classMissionList, student);
            }
        }
    }

    @Override
    public List<StudentMission> getStudentMissionsByStudentId(Long id) {
        if (studentRepository.findById(id).isEmpty()) {
            throw new StudentNotFoundException("<Student not found>");
        } else {
            Student student = studentRepository.findById(id).get();
            return studentMissionRepository.findByStudent(student);
        }
    }

    @Override
    public List<Student> getAllStudentsOfClassMission(Long id) {
        ClassMission classMission = classMissionRepository.findById(id)
                .orElseThrow(() -> new ClassMissionNotFoundException("<Class mission not found>"));

        // 只返回领取了该任务的学生
        List<StudentMission> studentMissions = studentMissionRepository.findByClassMission(classMission);
        return studentMissions.stream()
                .map(StudentMission::getStudent)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public StudentMission getStudentMissionByStudentIdAndClassMissionId(GetStudentMissionByStudentIdAndClassMissionIdDTO getStudentMissionByStudentIdAndClassMissionIdDTO) {
        if (classMissionRepository.findById(getStudentMissionByStudentIdAndClassMissionIdDTO.getClassMissionId()).isEmpty()) {
            throw new ClassMissionNotFoundException("<Class mission not found>");
        } else {
            ClassMission classMission = classMissionRepository.findById(getStudentMissionByStudentIdAndClassMissionIdDTO.getClassMissionId()).get();
            if (studentRepository.findById(getStudentMissionByStudentIdAndClassMissionIdDTO.getStudentId()).isEmpty()) {
                throw new StudentNotFoundException("<Student not found>");
            } else {
                Student student = studentRepository.findById(getStudentMissionByStudentIdAndClassMissionIdDTO.getStudentId()).get();
                return  studentMissionRepository.findByClassMissionAndStudent(classMission, student);
            }
        }
    }
}
