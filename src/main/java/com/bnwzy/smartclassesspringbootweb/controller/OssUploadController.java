package com.bnwzy.smartclassesspringbootweb.controller;

import com.bnwzy.smartclassesspringbootweb.pojo.ResponseMessage;
import com.bnwzy.smartclassesspringbootweb.service.IOssUploadService;
import com.bnwzy.smartclassesspringbootweb.service.impl.OssUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/oss")
public class OssUploadController {
    @Autowired
    IOssUploadService ossUploadService;

    @PostMapping("/uploadImage")
    public ResponseMessage uploadResource(MultipartFile file,Long id) {
        return ResponseMessage.success("Resource upload successful",ossUploadService.uploadImage(file,id));
    }
    @PostMapping("/uploadGraph")
    public ResponseMessage uploadGraph(MultipartFile file,Long id) {
        return ResponseMessage.success("Resource upload successful",ossUploadService.uploadGraph(file,id));
    }

    @PostMapping("/uploadResource")
    public ResponseMessage uploadResource(MultipartFile file,Long id,String message) {
        return ResponseMessage.success("Resource upload successful",ossUploadService.uploadResource(file,id,message));
    }

}
