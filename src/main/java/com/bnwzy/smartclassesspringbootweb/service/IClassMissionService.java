package com.bnwzy.smartclassesspringbootweb.service;

import com.bnwzy.smartclassesspringbootweb.pojo.ClassMission;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ClassMissionCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ClassMissionUpdateDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IClassMissionService {
    ClassMission createClassMission(ClassMissionCreateDTO classMissionCreateDTO);

    boolean deleteClassMission(Long id);

    ClassMission updateClassMission(ClassMissionUpdateDTO classMissionUpdateDTO);

    List<ClassMission> getAllClassMission();

    List<ClassMission> getCLassMissionByCid(Long cid);
}
