package com.bnwzy.smartclassesspringbootweb.service;

import com.bnwzy.smartclassesspringbootweb.pojo.ClassMissionResource;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ClassMissionResourceCreateDTO;
import org.springframework.stereotype.Service;

@Service
public interface IClassMissionResourceService {
    ClassMissionResource createClassMissionResource(ClassMissionResourceCreateDTO classMissionResourceCreateDTO);
}
