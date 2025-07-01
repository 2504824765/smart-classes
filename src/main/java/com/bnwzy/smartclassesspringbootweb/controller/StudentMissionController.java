package com.bnwzy.smartclassesspringbootweb.controller;

import com.bnwzy.smartclassesspringbootweb.pojo.ResponseMessage;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.GetStudentMissionByStudentIdAndClassMissionIdDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentMissionCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentMissionUpdateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentsAllClassMissionGetDTO;
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

    @DeleteMapping("/delete/{id}")
    public ResponseMessage deleteStudentMissionById(@PathVariable("id") Long id) {
        return ResponseMessage.success("<Delete student mission>", studentMissionService.deleteStudentMission(id));
    }

    @PostMapping("/update")
    public ResponseMessage updateStudentMission(@Validated @RequestBody StudentMissionUpdateDTO studentMissionUpdateDTO) {
        return ResponseMessage.success("<Update student mission>", studentMissionService.updateStudentMission(studentMissionUpdateDTO));
    }

    @GetMapping("/getStudentMissionById/{id}")
    public ResponseMessage getStudentMissionById(@PathVariable("id") Long id) {
        return ResponseMessage.success("<Get student mission>", studentMissionService.getStudentMissionById(id));
    }

    @GetMapping("/getStudentsAllClassMission")
    public ResponseMessage getStudentsAllClassMission(@Validated @RequestBody StudentsAllClassMissionGetDTO studentsAllClassMissionGetDTO) {
        return ResponseMessage.success("<Get student's all class mission>", studentMissionService.getStudentsAllClassMission(studentsAllClassMissionGetDTO));
    }

    @GetMapping("/getStudentMissionsByStudentId/{id}")
    public ResponseMessage getStudentMissionsByStudentId(@PathVariable("id") Long id) {
        return ResponseMessage.success("<Get student's all class mission>", studentMissionService.getStudentMissionsByStudentId(id));
    }

    @GetMapping("/allStudentsOfClassMission/{id}")
    public ResponseMessage getAllStudentsOfClassMission(@PathVariable("id") Long id) {
        return ResponseMessage.success("<Get all students of class mission>", studentMissionService.getAllStudentsOfClassMission(id));
    }

    @PostMapping("/getStudentMissionByStudentIdAndClassMissionId")
    public ResponseMessage getStudentMissionByStudentIdAndClassMissionId(@Validated @RequestBody GetStudentMissionByStudentIdAndClassMissionIdDTO getStudentMissionByStudentIdAndClassMissionIdDTO) {
        return ResponseMessage.success("<Get student mission>", studentMissionService.getStudentMissionByStudentIdAndClassMissionId(getStudentMissionByStudentIdAndClassMissionIdDTO));
    }
}
