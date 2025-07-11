package com.bnwzy.smartclassesspringbootweb.pojo.dto;

import lombok.Data;

@Data
public class StudentMissionUpdateDTO {
    private Long id;
    private Long studentId;
    private Long classMissionId;
    private Integer score;
    private Boolean isDone;
    private Boolean isActive;
    private String reportUrl;
    private String aiCommentUrl;
}
