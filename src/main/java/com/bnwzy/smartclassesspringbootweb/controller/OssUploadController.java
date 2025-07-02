package com.bnwzy.smartclassesspringbootweb.controller;

import com.bnwzy.smartclassesspringbootweb.pojo.ResponseMessage;
import com.bnwzy.smartclassesspringbootweb.service.IOssUploadService;
import com.bnwzy.smartclassesspringbootweb.service.impl.OssUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin
@RestController
@RequestMapping("/api/oss")
public class OssUploadController {
    @Autowired
    IOssUploadService ossUploadService;

    @PostMapping("/uploadImage")
    public ResponseMessage uploadResource(@RequestParam("file")MultipartFile file) {
        return ResponseMessage.success("Resource upload successful",ossUploadService.uploadImage(file));
    }
    @PostMapping("/uploadGraph")
    public ResponseMessage uploadGraph(@RequestParam("file")MultipartFile file) {
        return ResponseMessage.success("Resource upload successful",ossUploadService.uploadGraph(file));
    }

    @PostMapping("/uploadResource")
    public ResponseMessage uploadResource(@RequestParam("file")MultipartFile file,@RequestParam(value = "message", required = false)String message) {
        return ResponseMessage.success("Resource upload successful",ossUploadService.uploadResource(file,message));
    }
    @GetMapping("/getAllByClassId")
    public ResponseMessage getAllByClassId(@PathVariable("id")Long cid) {
        return ResponseMessage.success("Get all successfully",ossUploadService.getAllByClassId(cid));
    }
}
