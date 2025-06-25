package com.bnwzy.smartclassesspringbootweb.controller;

import com.bnwzy.smartclassesspringbootweb.pojo.ResponseMessage;
import com.bnwzy.smartclassesspringbootweb.pojo.Student;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentUpdateDTO;
import com.bnwzy.smartclassesspringbootweb.service.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/student")
public class StudentController {
    @Autowired
    private IStudentService studentService;

    @GetMapping("/{id}")
    public ResponseMessage getStudentById(@PathVariable("id") Long id) {
        return ResponseMessage.success("<Get student by id successfully", studentService.getStudentById(id));
    }

    @PostMapping("/update")
    public ResponseMessage updateStudent(@Validated @RequestBody StudentUpdateDTO studentUpdateDTO) {
        return ResponseMessage.success("<Update student successfully", studentService.updateStudent(studentUpdateDTO));
    }

    @GetMapping("/getStudentByUsername/{username}")
    public ResponseMessage getStudentByUsername(@PathVariable("username") String username) {
        return ResponseMessage.success("<Get student successfully>", studentService.getStudentByUsername(username));
    }

    @GetMapping("/all")
    public ResponseMessage getAllStudent() {
        return ResponseMessage.success("<Get all students successfully>", studentService.getAllStudent());
    }

    @PostMapping("/add")
    public ResponseMessage createStudent(@Validated @RequestBody StudentCreateDTO studentCreateDTO) {
        return ResponseMessage.success("<Create student>", studentService.createStudent(studentCreateDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseMessage deleteStudent(@PathVariable("id") Long id) {
        return ResponseMessage.success("<Delete Student", studentService.deleteStudent(id));
    }
}
