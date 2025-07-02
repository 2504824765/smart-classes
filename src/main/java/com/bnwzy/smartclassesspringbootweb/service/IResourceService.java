package com.bnwzy.smartclassesspringbootweb.service;

import com.bnwzy.smartclassesspringbootweb.pojo.Resource;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ResourceCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ResourceUpdateDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IResourceService {
    Resource createResource(ResourceCreateDTO resourceCreateDTO);

    Boolean deleteResource(Long id);

    Resource updateResource(ResourceUpdateDTO resourceUpdateDTO);

    List<Resource> getAllResource();

    Resource getResourceById(Long id);

    List<Resource> getResourcesByType(String type);

    List<Resource> getResourceByClassId(Long classId);

}
