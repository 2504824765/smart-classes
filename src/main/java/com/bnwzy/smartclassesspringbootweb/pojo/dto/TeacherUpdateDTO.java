package com.bnwzy.smartclassesspringbootweb.pojo.dto;

import lombok.Data;

@Data
public class TeacherUpdateDTO {
    private Long id;

    private String name;

    private String gender;

    private Long departmentId;
}
