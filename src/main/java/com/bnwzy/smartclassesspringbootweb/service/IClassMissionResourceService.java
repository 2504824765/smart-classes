package com.bnwzy.smartclassesspringbootweb.service;

import com.bnwzy.smartclassesspringbootweb.pojo.ClassMissionResource;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ClassMissionResourceCreateDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IClassMissionResourceService {
    ClassMissionResource createClassMissionResource(ClassMissionResourceCreateDTO classMissionResourceCreateDTO);

    List<ClassMissionResource> getAllClassMissionResourcesByClassMissionId(Long id);

    Boolean deleteClassMissionResourceById(Long id);
}
