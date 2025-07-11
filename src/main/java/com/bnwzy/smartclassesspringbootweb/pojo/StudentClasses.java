package com.bnwzy.smartclassesspringbootweb.pojo;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_student_classes")
public class StudentClasses {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_classes_seq")
    @SequenceGenerator(name = "student_classes_seq", sequenceName = "tb_student_classes_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "classes_id")
    private Classes classes;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    @Column(name = "grade")
    private Double grade;
}
