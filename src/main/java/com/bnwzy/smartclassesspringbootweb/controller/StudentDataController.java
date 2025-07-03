package com.bnwzy.smartclassesspringbootweb.controller;

import com.bnwzy.smartclassesspringbootweb.pojo.ResponseMessage;
import com.bnwzy.smartclassesspringbootweb.pojo.StudentData;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentDataCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentDataUpdateDTO;
import com.bnwzy.smartclassesspringbootweb.service.IStudentDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/studentData")
public class StudentDataController {
    @Autowired
    private IStudentDataService studentDataService;

    @PostMapping("/create")
    public ResponseMessage createStudentData(@Validated @RequestBody StudentDataCreateDTO studentDataCreateDTO) {
        return ResponseMessage.success("<Create studentData>", studentDataService.createStudentDate(studentDataCreateDTO));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseMessage deleteStudentDataById(@PathVariable("id") Long id) {
        return ResponseMessage.success("<Delete studentData>", studentDataService.deleteStudentData(id));
    }

    @PostMapping("/update")
    public ResponseMessage updateStudentData(@Validated @RequestBody StudentDataUpdateDTO studentDataUpdateDTO) {
        return ResponseMessage.success("<Update studentData", studentDataService.updateStudentData(studentDataUpdateDTO));
    }

    @GetMapping("/getStudentDataById/{id}")
    public ResponseMessage getStudentDataById(@PathVariable("id") Long id) {
        return ResponseMessage.success("<Get studentData>", studentDataService.getStudentDataById(id));
    }
}
