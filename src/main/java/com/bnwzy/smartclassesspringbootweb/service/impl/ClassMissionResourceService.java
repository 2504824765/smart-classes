package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.ClassMissionNotFoundException;
import com.bnwzy.smartclassesspringbootweb.pojo.ClassMission;
import com.bnwzy.smartclassesspringbootweb.pojo.ClassMissionResource;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ClassMissionResourceCreateDTO;
import com.bnwzy.smartclassesspringbootweb.repository.ClassMissionRepository;
import com.bnwzy.smartclassesspringbootweb.repository.ClassMissionResourceRepository;
import com.bnwzy.smartclassesspringbootweb.service.IClassMissionResourceService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Setter
@Service
public class ClassMissionResourceService implements IClassMissionResourceService {
    @Autowired
    private ClassMissionResourceRepository classMissionResourceRepository;
    @Autowired
    private ClassMissionRepository classMissionRepository;

    @Override
    public ClassMissionResource createClassMissionResource(ClassMissionResourceCreateDTO classMissionResourceCreateDTO) {
        ClassMissionResource classMissionResource = new ClassMissionResource();
        ClassMission classMission = classMissionRepository.findById(classMissionResourceCreateDTO.getClassMissionId()).orElseThrow(() -> new ClassMissionNotFoundException("<ClassMission not found>"));
        classMissionResource.setClassMission(classMission);
        classMissionResource.setPath(classMissionResourceCreateDTO.getPath());
        return classMissionResourceRepository.save(classMissionResource);
    }

    @Override
    public List<ClassMissionResource> getAllClassMissionResourcesByClassMissionId(Long id) {
        ClassMission classMission = classMissionRepository.findById(id).orElseThrow(() -> new ClassMissionNotFoundException("<ClassMission not found>"));
        return classMissionResourceRepository.findByClassMission(classMission);
    }
}
