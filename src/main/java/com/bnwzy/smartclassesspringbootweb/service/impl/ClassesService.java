package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.*;
import com.bnwzy.smartclassesspringbootweb.pojo.Classes;
import com.bnwzy.smartclassesspringbootweb.pojo.Department;
import com.bnwzy.smartclassesspringbootweb.pojo.Student;
import com.bnwzy.smartclassesspringbootweb.pojo.Teacher;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ClassesCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ClassesUpdateDTO;
import com.bnwzy.smartclassesspringbootweb.repository.ClassesRepository;
import com.bnwzy.smartclassesspringbootweb.repository.TeacherRepository;
import com.bnwzy.smartclassesspringbootweb.service.IClassesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
        if (!teacherRepository.findById(classesCreateDTO.getTeacherId()).isPresent()) {
            throw new TeacherNotFoundException("Teacher not found");
        } else {
            Teacher teacher = teacherRepository.findById(classesCreateDTO.getTeacherId()).get();
            classes.setTeacher(teacher);
        }
        classes.setCredit(classesCreateDTO.getCredit());
        classes.setClassHours(classesCreateDTO.getClassHours());
        classes.setGraph(classesCreateDTO.getGraph());
        classesRepository.save(classes);
        return classes;
    }

    @Override
    public boolean deleteClass(Long id) {
        if (!classesRepository.findById(id).isPresent()) {
            throw new ClassesNotFoundException("Class not found");
        } else {
            classesRepository.deleteById(id);
            return true;
        }

    }

    @Override
    public Classes updateClass(ClassesUpdateDTO classesUpdateDTO) {
        if (!classesRepository.findById(classesUpdateDTO.getId()).isPresent()) {
            throw new ClassesNotFoundException("<Class Not Found>");
        } else {
            Classes classes = classesRepository.findById(classesUpdateDTO.getId()).get();
            if (!teacherRepository.findById(classesUpdateDTO.getTeacherId()).isPresent()) {
                throw new TeacherNotFoundException("Teacher not found");
            } else {
                Teacher teacher = teacherRepository.findById(classesUpdateDTO.getTeacherId()).get();
                classes.setTeacher(teacher);
            }
            classes.setName(classesUpdateDTO.getName());
            classes.setId(classes.getId());
            classes.setCredit(classesUpdateDTO.getCredit());
            classes.setClassHours(classesUpdateDTO.getClassHours());
            classes.setGraph(classesUpdateDTO.getGraph());
            classesRepository.save(classes);
            return classes;
        }
    }

    @Override
    public List<Classes> getAllClasses() {
        List<Classes> classesList = new ArrayList<>();
        classesRepository.findAll().forEach(classesList::add);
        return classesList;
    }

    @Override
    public Classes getClassById(Long id) {
        if (!classesRepository.findById(id).isPresent()) {
            throw new ClassesNotFoundException("Class not found");
        } else {
            return classesRepository.findById(id).get();
        }
    }
}
