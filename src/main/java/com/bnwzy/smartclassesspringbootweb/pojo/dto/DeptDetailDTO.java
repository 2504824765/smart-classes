package com.bnwzy.smartclassesspringbootweb.pojo.dto;

import com.bnwzy.smartclassesspringbootweb.pojo.Department;
import com.bnwzy.smartclassesspringbootweb.pojo.Teacher;
import com.bnwzy.smartclassesspringbootweb.pojo.Student;
import lombok.Data;
import java.util.List;

@Data
public class DeptDetailDTO {
    private Department department;
    private List<Teacher> teachers;
    private List<Student> students;
} 