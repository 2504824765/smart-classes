package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.ClassesNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.StudentClassesNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.StudentNotFoundException;
import com.bnwzy.smartclassesspringbootweb.pojo.Classes;
import com.bnwzy.smartclassesspringbootweb.pojo.Student;
import com.bnwzy.smartclassesspringbootweb.pojo.StudentClasses;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentClassesCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentClassesUpdateDTO;
import com.bnwzy.smartclassesspringbootweb.repository.ClassesRepository;
import com.bnwzy.smartclassesspringbootweb.repository.StudentClassesRepository;
import com.bnwzy.smartclassesspringbootweb.repository.StudentRepository;
import com.bnwzy.smartclassesspringbootweb.service.IStudentClassesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentClassesService implements IStudentClassesService {
    @Autowired
    StudentClassesRepository studentClassesRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    ClassesRepository classesRepository;

    @Override
    public StudentClasses addClassRecord(StudentClassesCreateDTO studentClassesCreateDTO) {
        if(studentRepository.existsById(studentClassesCreateDTO.getSid())&&classesRepository.existsById(studentClassesCreateDTO.getCid())){
            StudentClasses studentClasses = new StudentClasses();
            studentClasses.setClasses(classesRepository.getById(studentClassesCreateDTO.getCid()));
            studentClasses.setStudent(studentRepository.getById(studentClassesCreateDTO.getSid()));
            studentClassesRepository.save(studentClasses);
            return studentClasses;
        }else{
            if (!classesRepository.existsById(studentClassesCreateDTO.getCid())) {
                throw new ClassesNotFoundException("Classes not found");
            } else {
                throw new StudentNotFoundException("Student not found");
            }
        }
    }

    @Override
    public boolean deleteClassRecordById(Long id) {
        if(studentRepository.existsById(id)){
            studentClassesRepository.deleteById(id);
            return true;
        }else{
            throw new StudentClassesNotFoundException("Student classes not found");
        }
    }

    @Override
    public StudentClasses updateClassRecord(StudentClassesUpdateDTO studentClassesUpdateDTO) {
        if(studentClassesRepository.existsById(studentClassesUpdateDTO.getSid())&&classesRepository.existsById(studentClassesUpdateDTO.getCid())){
            List<StudentClasses> studentClasses1 = new ArrayList<>();
            List<StudentClasses> studentClasses2 = new ArrayList<>();
            for(StudentClasses studentClasses : studentClassesRepository.findAll()){
                if(studentClasses.getStudent().getId().equals(studentClassesUpdateDTO.getSid())){
                    studentClasses1.add(studentClasses);
                }
            }
            for(StudentClasses studentClasses : studentClassesRepository.findAll()){
                if(studentClasses.getClasses().getId().equals(studentClassesUpdateDTO.getCid())){
                    studentClasses2.add(studentClasses);
                }
            }
            for(StudentClasses sc1 : studentClasses1){
                for(StudentClasses sc2 : studentClasses2){
                    if(sc1.getId().equals(sc2.getId())){
                        sc2.setGrade(studentClassesUpdateDTO.getGrade());
                        studentClassesRepository.save(sc2);
                        return sc2;
                    }
                }
            }
            throw new StudentClassesNotFoundException("Student classes not found");
        }else{
            if (!classesRepository.existsById(studentClassesUpdateDTO.getCid())) {
                throw new ClassesNotFoundException("Classes not found");
            } else {
                throw new StudentNotFoundException("Student not found");
            }
        }
    }

    @Override
    public StudentClasses getStudentClassesById(Long id) {
        if(studentClassesRepository.existsById(id)){
            return studentClassesRepository.getById(id);
        }else{
            throw new StudentClassesNotFoundException("Student classes not found");
        }
    }

    @Override
    public List<StudentClasses> getAllStudentClassesByStudentId(Long sid) {
        List<StudentClasses> sc=new ArrayList<>();
        for (StudentClasses studentClasses : studentClassesRepository.findAll()) {
            if (studentClasses.getStudent().getId().equals(sid)) {
                sc.add(studentClasses);
            }
        }
        if(sc.size()>0){
            return sc;
        }
        else{
            throw new StudentClassesNotFoundException("Student classes not found");
        }
    }

    @Override
    public List<StudentClasses> getAllStudentClassesByClassesId(Long cid) {
        List<StudentClasses> sc=new ArrayList<>();
        for (StudentClasses studentClasses : studentClassesRepository.findAll()) {
            if (studentClasses.getClasses().getId().equals(cid)) {
                sc.add(studentClasses);
            }
        }
        if(sc.size()>0){
            return sc;
        }
        else{
            throw new StudentClassesNotFoundException("Student classes not found");
        }
    }
}
