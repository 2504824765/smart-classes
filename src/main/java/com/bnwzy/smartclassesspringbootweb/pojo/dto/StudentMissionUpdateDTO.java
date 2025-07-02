package com.bnwzy.smartclassesspringbootweb.pojo.dto;

public class StudentMissionUpdateDTO {
    private Long id;
    private Long studentId;
    private Long classMissionId;
    private Integer score;
    private Boolean isDone;
    private Boolean isActive;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getClassMissionId() {
        return classMissionId;
    }

    public void setClassMissionId(Long classMissionId) {
        this.classMissionId = classMissionId;
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
