package com.bnwzy.smartclassesspringbootweb.pojo.dto;

import lombok.Data;

@Data
public class StudentDataUpdateDTO {
    private Long id;
    private String conceptUnderstanding;
    private String logicalReasoning;
    private String problemSolving;
    private String expressionNorms;
    private String innovativeThinking;
}
