package com.bnwzy.smartclassesspringbootweb.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_class_mission")
public class ClassMission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "classes_id") // 多个课程任务对应一个课程
    private Classes classes;
    @Column(name = "type")
    private String type;
    @Column(name = "description")
    private String description;
    @Column(name = "submit_method")
    private String submit_method;
    @Column(name = "deadline")
    private String deadline;
    @Column(name = "score")
    private Integer score;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubmit_method() {
        return submit_method;
    }

    public void setSubmit_method(String submit_method) {
        this.submit_method = submit_method;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Classes getClasses() {
        return classes;
    }

    public void setClasses(Classes classes) {
        this.classes = classes;
    }

}
