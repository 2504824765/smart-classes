package com.bnwzy.smartclassesspringbootweb.controller;


import com.bnwzy.smartclassesspringbootweb.pojo.ResponseMessage;
import com.bnwzy.smartclassesspringbootweb.pojo.Teacher;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.TeacherCreateDTO;
import com.bnwzy.smartclassesspringbootweb.service.ITeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RequestMapping("/api/teacher")
@RestController
public class TeacherController {
    @Autowired
    ITeacherService teacherService;

    @PostMapping("/add")
    public ResponseMessage<Teacher> addTeacher(TeacherCreateDTO teacherCreateDTO) {
        return ResponseMessage.success("<Add teacher successfully>",teacherService.addTeacher(teacherCreateDTO));
    }

}
