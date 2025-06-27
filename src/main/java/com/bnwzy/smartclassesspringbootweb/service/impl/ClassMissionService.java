package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.ClassesNotFoundException;
import com.bnwzy.smartclassesspringbootweb.pojo.ClassMission;
import com.bnwzy.smartclassesspringbootweb.pojo.Classes;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ClassMissionCreateDTO;
import com.bnwzy.smartclassesspringbootweb.repository.ClassMissionRepository;
import com.bnwzy.smartclassesspringbootweb.repository.ClassesRepository;
import com.bnwzy.smartclassesspringbootweb.service.IClassMissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClassMissionService implements IClassMissionService {
    @Autowired
    private ClassMissionRepository classMissionRepository;
    @Autowired
    private ClassesRepository classesRepository;

    @Override
    public ClassMission createClassMission(ClassMissionCreateDTO classMissionCreateDTO) {
        ClassMission classMission = new ClassMission();
        // 如果没有课程
        if (!classesRepository.findById(classMissionCreateDTO.getCid()).isPresent()) {
            throw new ClassesNotFoundException("<Class not found>");
        } else {
            Classes classes = classesRepository.findById(classMissionCreateDTO.getCid()).get();
            classMission.setClasses(classes);
        }
        classMission.setType(classMissionCreateDTO.getType());
        classMission.setDescription(classMissionCreateDTO.getDescription());
        classMission.setDeadline(classMissionCreateDTO.getDeadline());
        classMission.setSubmit_method(classMissionCreateDTO.getSubmitMethod());
        classMission.setScore(classMissionCreateDTO.getScore());
        return classMissionRepository.save(classMission);
    }
}
