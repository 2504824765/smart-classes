package com.bnwzy.smartclassesspringbootweb.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_class")
public class Classes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @ManyToOne // 多个课程对应一个老师
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
    @Column(name = "credit")
    private double credit;
    @Column(name = "classHours")
    private double classHours;
    @Column(name = "graph")
    private String graph;

    public String getGraph() {
        return graph;
    }

    public void setGraph(String graph) {
        this.graph = graph;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
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
}
