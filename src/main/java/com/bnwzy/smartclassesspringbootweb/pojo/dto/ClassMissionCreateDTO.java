package com.bnwzy.smartclassesspringbootweb.pojo.dto;

import lombok.Data;

@Data
public class ClassMissionCreateDTO {
    private Long cid;
    private String type;
    private String description;
    private String deadline;
    private String submitMethod;
    private Double score;
}
