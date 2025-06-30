package com.bnwzy.smartclassesspringbootweb.controller;

import com.bnwzy.smartclassesspringbootweb.pojo.ResponseMessage;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.DifyCreateGraphDTO;
import com.bnwzy.smartclassesspringbootweb.service.IDifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/dify")
public class DifyController {
    @Autowired
    private IDifyService difyService;

    @PostMapping("/createGraph")
    public ResponseMessage createGraph(@Validated @RequestBody DifyCreateGraphDTO difyCreateGraphDTO) {
        return ResponseMessage.success("<Create graph>", difyService.createGraph(difyCreateGraphDTO));
    }
}
