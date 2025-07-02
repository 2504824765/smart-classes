package com.bnwzy.smartclassesspringbootweb.pojo.dto;

public class StudentMissionCreateDTO {
    private Long studentId;
    private Long classMissionId;

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
}
