package com.bnwzy.smartclassesspringbootweb.pojo;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tb_class_mission")
@Data
public class ClassMission {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mission_seq")
    @SequenceGenerator(name = "mission_seq", sequenceName = "tb_class_mission_id_seq", allocationSize = 1)
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
    private Double score;
}
