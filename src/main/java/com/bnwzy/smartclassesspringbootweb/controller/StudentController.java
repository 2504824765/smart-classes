package com.bnwzy.smartclassesspringbootweb.controller;

import com.bnwzy.smartclassesspringbootweb.pojo.dto.ResponseMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    @PostMapping("login")
    public ResponseMessage login(String username, String password){
        return ResponseMessage.success("<UNK>");
    }
}
