package com.bnwzy.smartclassesspringbootweb.pojo;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_resource")
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "resource_seq")
    @SequenceGenerator(name = "resource_seq", sequenceName = "tb_resource_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "path")
    private String path;
    @Column(name = "type")
    private String type;
    @ManyToOne
    @JoinColumn(name = "classes_id") // 多个课程资源对应一个课程
    private Classes classes;
}
