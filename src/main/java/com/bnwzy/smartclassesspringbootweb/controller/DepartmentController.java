package com.bnwzy.smartclassesspringbootweb.controller;

import com.bnwzy.smartclassesspringbootweb.pojo.ResponseMessage;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.DeptCreateDTO;
import com.bnwzy.smartclassesspringbootweb.service.IDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/dept")
public class DepartmentController {
    @Autowired
    private IDepartmentService departmentService;

    @PostMapping("/add")
    public ResponseMessage addDepartment(@RequestBody DeptCreateDTO deptCreateDTO) {
        return ResponseMessage.success("<Create Department>", departmentService.createDept(deptCreateDTO));
    }
}
