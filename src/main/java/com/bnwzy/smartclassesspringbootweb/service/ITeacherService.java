package com.bnwzy.smartclassesspringbootweb.service;

import com.bnwzy.smartclassesspringbootweb.pojo.Teacher;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.TeacherCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.TeacherUpdateDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ITeacherService {
    Teacher addTeacher(TeacherCreateDTO teacherCreateDTO);

    Teacher updateTeacher(TeacherUpdateDTO teacherUpdateDTO);

    boolean deleteById(Long id);

    Teacher getTeacherById(Long id);

    Teacher getTeacherByUsername(String username);

    List<Teacher> getAllTeacher();
}
