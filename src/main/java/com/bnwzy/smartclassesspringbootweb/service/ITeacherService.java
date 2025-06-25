package com.bnwzy.smartclassesspringbootweb.service;

import com.bnwzy.smartclassesspringbootweb.pojo.Teacher;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.TeacherCreateDTO;
import org.springframework.stereotype.Service;

@Service
public interface ITeacherService {
    Teacher addTeacher(TeacherCreateDTO teacherCreateDTO);
}
