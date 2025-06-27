package com.bnwzy.smartclassesspringbootweb.controller;

import com.bnwzy.smartclassesspringbootweb.pojo.ResponseMessage;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentClassesCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentClassesUpdateDTO;
import com.bnwzy.smartclassesspringbootweb.repository.StudentClassesRepository;
import com.bnwzy.smartclassesspringbootweb.service.IStudentClassesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scAssociated")
@CrossOrigin
public class StudentClassesController {

    @Autowired
    IStudentClassesService studentClassesService;
    @Autowired
    private StudentClassesRepository studentClassesRepository;

    @PostMapping("/add")
    public ResponseMessage addClassRecord(@RequestBody @Validated StudentClassesCreateDTO studentClassesCreateDTO) {
        return ResponseMessage.success("<Add class record successfully>",studentClassesService.addClassRecord(studentClassesCreateDTO));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseMessage deleteClassRecordById(@PathVariable("id") Long id){
        return ResponseMessage.success("<Delete class record by id successfully>",studentClassesService.deleteClassRecordById(id));
    }
    @PostMapping("/update")
    public ResponseMessage updateClassRecord(@RequestBody @Validated StudentClassesUpdateDTO studentClassesUpdateDTO) {
        return ResponseMessage.success("<Update class record successfully>",studentClassesService.updateClassRecord(studentClassesUpdateDTO));
    }
    @GetMapping("/getAssociatedById/{id}")
    public ResponseMessage getStudentClassesById(@PathVariable("id") Long id) {
        return ResponseMessage.success("<Get class record by id successfully>",studentClassesService.getStudentClassesById(id));
    }
    @GetMapping("/getAssociatedBySid/{sid}")
    public ResponseMessage getAllStudentClassesByStudentId(@PathVariable("sid") Long sid) {
        return ResponseMessage.success("<Get class record by sid successfully>",studentClassesService.getAllStudentClassesByStudentId(sid));
    }
    @GetMapping("/getAssociatedByCid/{cid}")
    public ResponseMessage getAllStudentClassesByClassesId(@PathVariable("cid")Long cid) {
        return ResponseMessage.success("<Get class record by cid successfully>",studentClassesService.getAllStudentClassesByClassesId(cid));
    }
}
