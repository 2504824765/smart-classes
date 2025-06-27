package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.ClassesNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.DepartmentNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.TeacherNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.UserNotFoundException;
import com.bnwzy.smartclassesspringbootweb.pojo.Classes;
import com.bnwzy.smartclassesspringbootweb.pojo.Department;
import com.bnwzy.smartclassesspringbootweb.pojo.Student;
import com.bnwzy.smartclassesspringbootweb.pojo.Teacher;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ClassesCreateDTO;
import com.bnwzy.smartclassesspringbootweb.repository.ClassesRepository;
import com.bnwzy.smartclassesspringbootweb.repository.TeacherRepository;
import com.bnwzy.smartclassesspringbootweb.service.IClassesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
