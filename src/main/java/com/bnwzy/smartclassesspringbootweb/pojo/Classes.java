package com.bnwzy.smartclassesspringbootweb.pojo;

import jakarta.persistence.*;
import lombok.Data;

import javax.naming.Name;

@Data
@Entity
@Table(name = "tb_class")
public class Classes {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "class_seq")
    @SequenceGenerator(name = "class_seq", sequenceName = "tb_class_id_seq", allocationSize = 1)
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
    @Column(name = "imageUrl")
    private String imageUrl;
}
