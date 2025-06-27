package com.bnwzy.smartclassesspringbootweb.controller;

import com.bnwzy.smartclassesspringbootweb.pojo.ResponseMessage;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.DeptCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.DeptUpdateDTO;
import com.bnwzy.smartclassesspringbootweb.service.IDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/dept")
public class DepartmentController {
    @Autowired
    private IDepartmentService departmentService;

    @PostMapping("/add")
    public ResponseMessage addDepartment(@Validated @RequestBody DeptCreateDTO deptCreateDTO) {
        return ResponseMessage.success("<Create Department>", departmentService.createDept(deptCreateDTO));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseMessage deleteDepartment(@PathVariable("id") Long id) {
        return ResponseMessage.success("<Delete Dept>", departmentService.deleteDept(id));
    }

        @PostMapping("/update")
    public ResponseMessage updateDepartment(@Validated @RequestBody DeptUpdateDTO deptUpdateDTO) {
        return ResponseMessage.success("<Update Dept>", departmentService.updateDept(deptUpdateDTO));
    }

    @GetMapping("/all")
    public ResponseMessage getAllDept() {
        return ResponseMessage.success("<Get all dept>", departmentService.getAllDept());
    }

    @GetMapping("/getDeptById/{id}")
    public ResponseMessage getDeptById(@PathVariable("id") Long id) {
        return ResponseMessage.success("<Get Dept by id>", departmentService.getDeptById(id));
    }

    @GetMapping("/getDeptByName/{name}")
    public ResponseMessage getDeptByName(@PathVariable("name") String name) {
        return ResponseMessage.success("<Get Dept by name>", departmentService.getDeptByName(name));
    }
}
