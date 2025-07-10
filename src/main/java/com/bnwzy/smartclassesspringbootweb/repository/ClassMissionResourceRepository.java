package com.bnwzy.smartclassesspringbootweb.repository;

import com.bnwzy.smartclassesspringbootweb.pojo.ClassMission;
import com.bnwzy.smartclassesspringbootweb.pojo.ClassMissionResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassMissionResourceRepository extends JpaRepository<ClassMissionResource, Long> {
    List<ClassMissionResource> findByClassMission(ClassMission classMission);
}
