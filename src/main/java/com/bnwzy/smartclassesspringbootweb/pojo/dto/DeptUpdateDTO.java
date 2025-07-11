package com.bnwzy.smartclassesspringbootweb.pojo.dto;

import lombok.Data;

@Data
public class DeptUpdateDTO {
    private Long id;
    private String name;
    private Long parentId;
}
