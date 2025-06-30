package com.bnwzy.smartclassesspringbootweb.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import javax.naming.Name;

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
    private Double credit;
    @Column(name = "classHours")
    private Double classHours;
    @Column(name = "graph")
    private String graph;
    @Column(name = "is_active")
    private Boolean isActive;
    @Column(name = "description")
    private String description;

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

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    public Double getClassHours() {
        return classHours;
    }

    public void setClassHours(Double classHours) {
        this.classHours = classHours;
    }

    public String getGraph() {
        return graph;
    }

    public void setGraph(String graph) {
        this.graph = graph;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
