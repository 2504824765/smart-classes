package com.bnwzy.smartclassesspringbootweb.controller;

import com.bnwzy.smartclassesspringbootweb.pojo.ResponseMessage;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ResourceCreateDTO;
import com.bnwzy.smartclassesspringbootweb.service.IResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/resource")
public class ResourceController {
    @Autowired
    private IResourceService resourceService;

    @PostMapping("/add")
    public ResponseMessage createResource(@Validated @RequestBody ResourceCreateDTO resourceCreateDTO) {
        return ResponseMessage.success("<Create resource>", resourceService.createResource(resourceCreateDTO));
    }
}
