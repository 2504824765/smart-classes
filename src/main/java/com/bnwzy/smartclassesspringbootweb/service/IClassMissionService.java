package com.bnwzy.smartclassesspringbootweb.service;

import com.bnwzy.smartclassesspringbootweb.pojo.ClassMission;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ClassMissionCreateDTO;
import org.springframework.stereotype.Service;

@Service
public interface IClassMissionService {
    ClassMission createClassMission(ClassMissionCreateDTO classMissionCreateDTO);

    boolean deleteClassMission(Long id);
}
