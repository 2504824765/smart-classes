package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.ClassMissionNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.ClassesNotFoundException;
import com.bnwzy.smartclassesspringbootweb.pojo.ClassMission;
import com.bnwzy.smartclassesspringbootweb.pojo.Classes;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ClassMissionCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ClassMissionUpdateDTO;
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

    @Override
    public boolean deleteClassMission(Long id) {
        if (classMissionRepository.findById(id).isPresent()) {
            classMissionRepository.deleteById(id);
            return true;
        } else {
            throw new ClassMissionNotFoundException("<Class mission not found>");
        }
    }

    @Override
    public ClassMission updateClassMission(ClassMissionUpdateDTO classMissionUpdateDTO) {
        if (!classMissionRepository.findById(classMissionUpdateDTO.getId()).isPresent()) {
            throw new ClassMissionNotFoundException("<Class mission not found>");
        }
        ClassMission classMission = classMissionRepository.findById(classMissionUpdateDTO.getId()).get();
        if (!classesRepository.findById(classMissionUpdateDTO.getCid()).isPresent()) {
            throw new ClassesNotFoundException("<Class not found>");
        } else {
            classMission.setClasses(classesRepository.findById(classMissionUpdateDTO.getCid()).get());
        }
        classMission.setType(classMissionUpdateDTO.getType());
        classMission.setDescription(classMissionUpdateDTO.getDescription());
        classMission.setDeadline(classMissionUpdateDTO.getDeadline());
        classMission.setSubmit_method(classMissionUpdateDTO.getSubmitMethod());
        classMission.setScore(classMissionUpdateDTO.getScore());
        return classMissionRepository.save(classMission);
    }
}
