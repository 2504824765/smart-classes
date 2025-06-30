package com.bnwzy.smartclassesspringbootweb.controller;

import com.bnwzy.smartclassesspringbootweb.pojo.ResponseMessage;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ResourceCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ResourceUpdateDTO;
import com.bnwzy.smartclassesspringbootweb.service.IResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/resource")
public class ResourceController {
    @Autowired
    private IResourceService resourceService;

    @PostMapping("/add")
    public ResponseMessage createResource(@Validated @RequestBody ResourceCreateDTO resourceCreateDTO) {
        return ResponseMessage.success("<Create resource>", resourceService.createResource(resourceCreateDTO));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseMessage deleteResource(@PathVariable("id") Long id) {
        return ResponseMessage.success("<Delete resource", resourceService.deleteResource(id));
    }

    @PostMapping("/update")
    public ResponseMessage updateResource(@Validated @RequestBody ResourceUpdateDTO resourceUpdateDTO) {
        return ResponseMessage.success("<Update resource>", resourceService.updateResource(resourceUpdateDTO));
    }

    @GetMapping("/all")
    public ResponseMessage getAllResource() {
        return ResponseMessage.success("<Get all resources", resourceService.getAllResource());
    }

    @GetMapping("/getResourceById/{id}")
    public ResponseMessage getResourceById(@PathVariable("id") Long id) {
        return ResponseMessage.success("<Get resource>", resourceService.getResourceById(id));
    }

    @GetMapping("/getResourceByType/{type}")
    public ResponseMessage getResourceByType(@PathVariable("type") String type) {
        return ResponseMessage.success("<Get resources by type>", resourceService.getResourcesByType(type));
    }
}
