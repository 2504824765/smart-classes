package com.bnwzy.smartclassesspringbootweb.pojo;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_student_study_data")
public class StudentStudyData {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_study_data_seq")
    @SequenceGenerator(name = "student_study_data_seq", sequenceName = "tb_student_study_data_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Integer id;
    @OneToOne
    @JoinColumn(name = "student_id")
    private Student student;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
