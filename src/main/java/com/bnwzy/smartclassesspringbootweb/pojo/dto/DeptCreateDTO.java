package com.bnwzy.smartclassesspringbootweb.pojo.dto;

import lombok.Data;

@Data
public class DeptCreateDTO {
    private String name;
    private Long parentId;
    private Integer departmentLevel;
}
