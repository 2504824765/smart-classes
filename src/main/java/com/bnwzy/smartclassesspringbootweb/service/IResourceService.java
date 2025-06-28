package com.bnwzy.smartclassesspringbootweb.service;

import com.bnwzy.smartclassesspringbootweb.pojo.Resource;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ResourceCreateDTO;
import org.springframework.stereotype.Service;

@Service
public interface IResourceService {
    Resource createResource(ResourceCreateDTO resourceCreateDTO);
}
