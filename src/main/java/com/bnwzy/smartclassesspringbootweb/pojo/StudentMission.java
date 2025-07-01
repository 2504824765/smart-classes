package com.bnwzy.smartclassesspringbootweb.pojo;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_student_mission")
public class StudentMission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    @ManyToOne
    @JoinColumn(name = "class_mission-id")
    private ClassMission classMission;
    @Column(name = "score")
    private Integer score;
    @Column(name = "is_done")
    private Boolean isDone;
    @Column(name = "is_actice")
    private Boolean isActive;

    public ClassMission getClassMission() {
        return classMission;
    }

    public void setClassMission(ClassMission classMission) {
        this.classMission = classMission;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Boolean getDone() {
        return isDone;
    }

    public void setDone(Boolean done) {
        isDone = done;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
