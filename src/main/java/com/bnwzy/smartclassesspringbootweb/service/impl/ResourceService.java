package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.ResourceNameAlreadyExistException;
import com.bnwzy.smartclassesspringbootweb.exception.ResourceNotFoundException;
import com.bnwzy.smartclassesspringbootweb.pojo.Resource;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ResourceCreateDTO;
import com.bnwzy.smartclassesspringbootweb.repository.ResourceRepository;
import com.bnwzy.smartclassesspringbootweb.service.IResourceService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResourceService implements IResourceService {
    @Autowired
    private ResourceRepository resourceRepository;

    @Override
    public Resource createResource(ResourceCreateDTO resourceCreateDTO) {
        if (resourceRepository.findByName(resourceCreateDTO.getName()).isPresent()) {
            throw new ResourceNameAlreadyExistException("<Resource name already exists>");
        } else {
            Resource resource = new Resource();
            BeanUtils.copyProperties(resourceCreateDTO, resource);
            return resourceRepository.save(resource);
        }
    }

    @Override
    public Boolean deleteResource(Long id) {
        if (!resourceRepository.existsById(id)) {
            throw new ResourceNotFoundException("<Resource not found>");
        } else {
            resourceRepository.deleteById(id);
            return true;
        }
    }
}
