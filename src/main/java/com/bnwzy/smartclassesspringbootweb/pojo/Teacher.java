package com.bnwzy.smartclassesspringbootweb.pojo;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_teacher")
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "teacher_seq")
    @SequenceGenerator(name = "teacher_seq", sequenceName = "tb_teacher_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;
    @Column(name = "username")
    private String username;
    @Column(name = "name")
    private String name;
    @Column(name = "gender")
    private String gender;
    @ManyToOne(optional = false)
    @JoinColumn(name = "department_id", foreignKey = @ForeignKey(name = "FK_teacher_department"))
    private Department department;

}
