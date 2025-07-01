package com.bnwzy.smartclassesspringbootweb.controller;

import com.bnwzy.smartclassesspringbootweb.pojo.ResponseMessage;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentMissionCreateDTO;
import com.bnwzy.smartclassesspringbootweb.service.IStudentMissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/studentMission")
public class StudentMissionController {
    @Autowired
    private IStudentMissionService studentMissionService;

    @PostMapping("/add")
    public ResponseMessage createStudentMission(@Validated @RequestBody StudentMissionCreateDTO studentMissionCreateDTO) {
        return ResponseMessage.success("<Create student mission>", studentMissionService.createStudentMission(studentMissionCreateDTO));
    }
}
