package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.*;
import com.bnwzy.smartclassesspringbootweb.pojo.Classes;
import com.bnwzy.smartclassesspringbootweb.pojo.Teacher;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ClassesCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ClassesUpdateDTO;
import com.bnwzy.smartclassesspringbootweb.repository.ClassesRepository;
import com.bnwzy.smartclassesspringbootweb.repository.TeacherRepository;
import com.bnwzy.smartclassesspringbootweb.service.IClassesService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Setter
@Service
public class ClassesService implements IClassesService {
    @Autowired
    private ClassesRepository classesRepository;
    @Autowired
    private TeacherRepository teacherRepository;

    @Override
    public Classes addClass(ClassesCreateDTO classesCreateDTO) {
        Classes classes = new Classes();
        classes.setName(classesCreateDTO.getName());
        if (teacherRepository.findById(classesCreateDTO.getTeacherId()).isEmpty()) {
            throw new TeacherNotFoundException("Teacher not found");
        } else {
            Teacher teacher = teacherRepository.findById(classesCreateDTO.getTeacherId()).get();
            classes.setTeacher(teacher);
        }
        classes.setCredit(classesCreateDTO.getCredit());
        classes.setClassHours(classesCreateDTO.getClassHours());
        classes.setGraph(classesCreateDTO.getGraph());
        classes.setDescription(classesCreateDTO.getDescription());
        classes.setActive(classesCreateDTO.getActive());
        classes.setImageUrl(classesCreateDTO.getImageUrl());
        return  classesRepository.save(classes);
    }

    @Override
    public boolean deleteClass(Long id) {
        if (classesRepository.findById(id).isEmpty()) {
            throw new ClassesNotFoundException("Class not found");
        } else {
            classesRepository.deleteById(id);
            return true;
        }

    }

    @Override
    public Classes updateClass(ClassesUpdateDTO classesUpdateDTO) {
        if (classesRepository.findById(classesUpdateDTO.getId()).isEmpty()) {
            throw new ClassesNotFoundException("<Class Not Found>");
        } else {
            Classes classes = classesRepository.findById(classesUpdateDTO.getId()).get();
            if (classesUpdateDTO.getTeacherId() != null) {
                if (teacherRepository.findById(classesUpdateDTO.getTeacherId()).isEmpty()) {
                    throw new TeacherNotFoundException("Teacher not found");
                } else {
                    Teacher teacher = teacherRepository.findById(classesUpdateDTO.getTeacherId()).get();
                    classes.setTeacher(teacher);
                }
            }
            if (classesUpdateDTO.getName() != null) {
                classes.setName(classesUpdateDTO.getName());
            }
            if (classesUpdateDTO.getCredit() != null) {
                classes.setCredit(classesUpdateDTO.getCredit());
            }
            if (classesUpdateDTO.getClassHours() != null) {
                classes.setClassHours(classesUpdateDTO.getClassHours());
            }
            if (classesUpdateDTO.getGraph() != null) {
                classes.setGraph(classesUpdateDTO.getGraph());
            }
            if (classesUpdateDTO.getDescription() != null) {
                classes.setDescription(classesUpdateDTO.getDescription());
            }
            if (classesUpdateDTO.getActive() != null) {
                classes.setActive(classesUpdateDTO.getActive());
            }
            if (classesUpdateDTO.getImageUrl() != null) {
                classes.setImageUrl(classesUpdateDTO.getImageUrl());
            }
            return classesRepository.save(classes);
        }
    }

    @Override
    public List<Classes> getAllClasses() {
        return new ArrayList<>(classesRepository.findAll());
    }

    @Override
    public Classes getClassById(Long id) {
        if (classesRepository.findById(id).isEmpty()) {
            throw new ClassesNotFoundException("Class not found");
        } else {
            return classesRepository.findById(id).get();
        }
    }

    @Override
    public Classes getClassByName(String name) {
        if (classesRepository.findByName(name).isEmpty()) {
            throw new ClassesNotFoundException("Class not found");
        } else {
            return classesRepository.findByName(name).get();
        }
    }

    @Override
    public List<Classes> getClassByTeacherId(Long teacherId) {
        if (teacherRepository.findById(teacherId).isEmpty()) {
            throw new TeacherNotFoundException("Teacher not found");
        } else {
            Teacher teacher = teacherRepository.findById(teacherId).get();
            return classesRepository.findByTeacher(teacher);
        }
    }

    @Override
    public Long getClassCount() {
        return classesRepository.count();
    }
}
