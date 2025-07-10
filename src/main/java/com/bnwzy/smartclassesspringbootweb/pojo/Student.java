package com.bnwzy.smartclassesspringbootweb.pojo;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_student")
public class Student {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY) // 默认自增生成方式
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_seq")
    @SequenceGenerator(name = "student_seq", sequenceName = "tb_student_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "username")
    private String username;
    @Column(name = "gender")
    private String gender;
    @ManyToOne
    @JoinColumn(name = "dept_id")
    private Department department;
    @Column(name = "gpa")
    private Double gpa;
    @OneToOne
    @JoinColumn(name = "student_data_id")
    private StudentData studentData;
}
