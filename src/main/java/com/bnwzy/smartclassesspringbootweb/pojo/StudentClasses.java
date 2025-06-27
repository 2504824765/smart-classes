package com.bnwzy.smartclassesspringbootweb.pojo;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_student_classes")
public class StudentClasses {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @OneToOne
    @JoinColumn(name = "classes_id")
    private Classes classes;
    @OneToOne
    @JoinColumn(name = "student_id")
    private Student student;
    @Column(name = "grade")
    private Double grade;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Classes getClasses() {
        return classes;
    }

    public void setClasses(Classes classes) {
        this.classes = classes;
    }
}
