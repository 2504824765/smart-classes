package com.bnwzy.smartclassesspringbootweb.pojo.dto;

public class ClassesCreateDTO {
    private String name;
    private Long teacherId;
    private double credit;
    private double classHours;
    private String graph;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public double getClassHours() {
        return classHours;
    }

    public void setClassHours(double classHours) {
        this.classHours = classHours;
    }

    public String getGraph() {
        return graph;
    }

    public void setGraph(String graph) {
        this.graph = graph;
    }
}
