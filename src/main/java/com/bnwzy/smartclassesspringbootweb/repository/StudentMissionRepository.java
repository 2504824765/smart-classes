package com.bnwzy.smartclassesspringbootweb.repository;

import com.bnwzy.smartclassesspringbootweb.pojo.ClassMission;
import com.bnwzy.smartclassesspringbootweb.pojo.Student;
import com.bnwzy.smartclassesspringbootweb.pojo.StudentMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentMissionRepository extends JpaRepository<StudentMission,Long> {
    List<StudentMission> findByClassMissionInAndStudent(List<ClassMission> classMissions, Student student);
    List<StudentMission> findByStudent(Student student);
    StudentMission findByClassMissionAndStudent(ClassMission classMission, Student student);
}
