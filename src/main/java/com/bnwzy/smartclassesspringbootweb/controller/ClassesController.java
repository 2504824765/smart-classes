package com.bnwzy.smartclassesspringbootweb.controller;

import com.bnwzy.smartclassesspringbootweb.pojo.ResponseMessage;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ClassesCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ClassesUpdateDTO;
import com.bnwzy.smartclassesspringbootweb.service.IClassesService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/class")
public class ClassesController {
    @Autowired
    private IClassesService classesService;

    @PostMapping("/add")
    public ResponseMessage addClass(@RequestBody ClassesCreateDTO classesCreateDTO) {
        return ResponseMessage.success("<Add Class>", classesService.addClass(classesCreateDTO));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseMessage deleteClass(@PathVariable("id") Long id) {
        return ResponseMessage.success("<Delete Class>", classesService.deleteClass(id));
    }

    @PostMapping("/update")
    public ResponseMessage updateClass(@Validated @RequestBody ClassesUpdateDTO classesUpdateDTO) {
        return ResponseMessage.success("<Update Class>", classesService.updateClass(classesUpdateDTO));
    }

    @GetMapping("/all")
    public ResponseMessage getAllClasses() {
        return ResponseMessage.success("<Get all Classes>", classesService.getAllClasses());
    }

}
