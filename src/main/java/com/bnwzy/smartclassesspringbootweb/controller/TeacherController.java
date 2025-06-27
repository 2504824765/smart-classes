package com.bnwzy.smartclassesspringbootweb.controller;


import com.bnwzy.smartclassesspringbootweb.pojo.ResponseMessage;
import com.bnwzy.smartclassesspringbootweb.pojo.Teacher;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.TeacherCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.TeacherUpdateDTO;
import com.bnwzy.smartclassesspringbootweb.service.ITeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RequestMapping("/api/teacher")
@RestController
public class TeacherController {
    @Autowired
    ITeacherService teacherService;

    @PostMapping("/add")
    public ResponseMessage addTeacher(@RequestBody @Validated TeacherCreateDTO teacherCreateDTO) {
        return ResponseMessage.success("<Add teacher successfully>",teacherService.addTeacher(teacherCreateDTO));
    }

    @PostMapping("/update")
    public ResponseMessage updateTeacher(@RequestBody @Validated TeacherUpdateDTO teacherUpdateDTO) {
        return ResponseMessage.success("<Update teacher successfully>",teacherService.updateTeacher(teacherUpdateDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseMessage deleteById(@PathVariable("id") Long id) {
        return ResponseMessage.success("<Delete teacher successfully>",teacherService.deleteById(id));
    }
    @GetMapping("/getTeacherById/{id}")
    public ResponseMessage getTeacherById(@PathVariable("id") Long id) {
        return ResponseMessage.success("<Get teacher by id successfully>",teacherService.getTeacherById(id));
    }
    @GetMapping("getTeacherByUsername/{username}")
    public ResponseMessage getTeacherByUsername(@PathVariable String username) {
        return ResponseMessage.success("<Get teacher by username successfully>",teacherService.getTeacherByUsername(username));
    }
    @GetMapping("/all")
    public ResponseMessage getAllTeachers() {
        return ResponseMessage.success("<Get AllTeachers successfully>",teacherService.getAllTeacher());
    }
}
