package com.bnwzy.smartclassesspringbootweb.pojo;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "tb_class_mission_resource")
public class ClassMissionResource {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "class_mission_resource_seq")
    @SequenceGenerator(name = "class_mission_resource_seq", sequenceName = "tb_class_mission_resource_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "class_mission_id")
    private ClassMission classMission;
    @Column(name = "path")
    private String path;
}
