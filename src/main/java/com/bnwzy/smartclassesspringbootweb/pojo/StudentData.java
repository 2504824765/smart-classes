package com.bnwzy.smartclassesspringbootweb.pojo;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "tb_student_data")
public class StudentData {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_data_seq")
    @SequenceGenerator(name = "student_data_seq", sequenceName = "tb_student_data_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;
    @Column(name = "condept_understanding")
    private String conceptUnderstanding;
    @Column(name = "logical_reasoning")
    private String logicalReasoning;
    @Column(name = "problem_solving")
    private String problemSolving;
    @Column(name = "expression norms")
    private String expressionNorms;
    @Column(name = "innovative_thinking")
    private String innovativeThinking;
}
