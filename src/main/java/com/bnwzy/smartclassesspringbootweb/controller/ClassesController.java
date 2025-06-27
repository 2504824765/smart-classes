package com.bnwzy.smartclassesspringbootweb.controller;

import com.bnwzy.smartclassesspringbootweb.pojo.ResponseMessage;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ClassesCreateDTO;
import com.bnwzy.smartclassesspringbootweb.service.IClassesService;
import org.springframework.beans.factory.annotation.Autowired;
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

}
