package com.bnwzy.smartclassesspringbootweb.repository;

import com.bnwzy.smartclassesspringbootweb.pojo.ClassMission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassMissionRepository extends JpaRepository<ClassMission, Long> {
    List<ClassMission> findByClasses_Id(Long classesId);
}
