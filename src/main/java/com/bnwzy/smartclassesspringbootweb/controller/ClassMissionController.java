package com.bnwzy.smartclassesspringbootweb.controller;

import com.bnwzy.smartclassesspringbootweb.pojo.ClassMission;
import com.bnwzy.smartclassesspringbootweb.pojo.ResponseMessage;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ClassMissionCreateDTO;
import com.bnwzy.smartclassesspringbootweb.service.IClassMissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/classMission")
public class ClassMissionController {
    @Autowired
    private IClassMissionService classMissionService;

    @PostMapping("/add")
    public ResponseMessage addClassMission(@RequestBody ClassMissionCreateDTO classMissionCreateDTO) {
        return ResponseMessage.success("<Create class mission>", classMissionService.createClassMission(classMissionCreateDTO));
    }
}
