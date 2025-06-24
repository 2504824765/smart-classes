package com.bnwzy.smartclassesspringbootweb.controller;

import com.bnwzy.smartclassesspringbootweb.pojo.ResponseMessage;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentLoginDTO;
import com.bnwzy.smartclassesspringbootweb.service.impl.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/student")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @PostMapping("/register")
    public ResponseMessage register(@Validated @RequestBody StudentCreateDTO studentCreateDTO) {
        return ResponseMessage.success("<Login Success>", studentService.registStudent(studentCreateDTO));
    }

    @PostMapping("/login")
    public ResponseMessage login(@Validated @RequestBody StudentLoginDTO studentLoginDTO) {
        return ResponseMessage.success("<Register successfully>", studentService.loginStudent(studentLoginDTO));
    }

    @GetMapping("/{id}")
    public ResponseMessage getStudentById(@PathVariable Long id) {
        return ResponseMessage.success("<Get student by id successfully", studentService.getStudentById(id));
    }
}
