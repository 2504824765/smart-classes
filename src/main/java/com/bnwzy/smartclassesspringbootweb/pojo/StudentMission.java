package com.bnwzy.smartclassesspringbootweb.pojo;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestMapping;

@Data
@Entity
@Table(name = "tb_student_mission")
public class StudentMission {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_mission_seq")
    @SequenceGenerator(name = "student_mission_seq", sequenceName = "tb_student_mission_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    @ManyToOne
    @JoinColumn(name = "class_mission_id")
    private ClassMission classMission;
    @Column(name = "score")
    private Integer score;
    @Column(name = "is_done")
    private Boolean isDone;
    @Column(name = "is_actice")
    private Boolean isActive;
    @Column(name = "report_url")
    private String reportUrl;
    @Column(name = "ai_comment_url")
    private String aiCommentUrl;
}
