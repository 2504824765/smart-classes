package com.bnwzy.smartclassesspringbootweb.controller;


import com.bnwzy.smartclassesspringbootweb.pojo.ResponseMessage;
import com.bnwzy.smartclassesspringbootweb.pojo.Teacher;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.TeacherCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.TeacherUpdateDTO;
import com.bnwzy.smartclassesspringbootweb.service.ITeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/update")
    public ResponseMessage<Teacher> updateTeacher(TeacherUpdateDTO teacherUpdateDTO) {
        return ResponseMessage.success("<Update teacher successfully>",teacherService.updateTeacher(teacherUpdateDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseMessage<Teacher> deleteById(@PathVariable("id") Long id) {
        return ResponseMessage.success("<Delete teacher successfully>",teacherService.deleteById(id));
    }
    public ResponseMessage<Teacher> getTeacherById(@PathVariable("id") Long id) {
        return ResponseMessage.success("<>");
    }
}
