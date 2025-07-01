package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.ClassesNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.ResourceNameAlreadyExistException;
import com.bnwzy.smartclassesspringbootweb.exception.ResourceNotFoundException;
import com.bnwzy.smartclassesspringbootweb.pojo.Resource;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ResourceCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ResourceUpdateDTO;
import com.bnwzy.smartclassesspringbootweb.repository.ClassesRepository;
import com.bnwzy.smartclassesspringbootweb.repository.ResourceRepository;
import com.bnwzy.smartclassesspringbootweb.service.IResourceService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResourceService implements IResourceService {
    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ClassesRepository classesRepository;


    @Override
    public Resource createResource(ResourceCreateDTO resourceCreateDTO) {
        if (resourceRepository.findByName(resourceCreateDTO.getName()).isPresent()) {
            throw new ResourceNameAlreadyExistException("<Resource name already exists>");
        } else {
            Resource resource = new Resource();
            BeanUtils.copyProperties(resourceCreateDTO, resource);
            if (!classesRepository.findById(resourceCreateDTO.getClassId()).isPresent()) {
                throw new ClassesNotFoundException("<Resource class not found>");
            } else {
                resource.setClasses(classesRepository.findById(resourceCreateDTO.getClassId()).get());
            }

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

    @Override
    public Resource updateResource(ResourceUpdateDTO resourceUpdateDTO) {
        if (!resourceRepository.findById(resourceUpdateDTO.getId()).isPresent()) {
            throw new ResourceNotFoundException("<Resource not found>");
        } else {
            Resource resource = resourceRepository.findById(resourceUpdateDTO.getId()).get();
            if (!classesRepository.findById(resourceUpdateDTO.getClassId()).isPresent()) {
                throw new ClassesNotFoundException("<Resouce class not found>");
            } else {
                resource.setClasses(classesRepository.findById(resourceUpdateDTO.getClassId()).get());
            }
            resource.setName(resourceUpdateDTO.getName());
            resource.setPath(resourceUpdateDTO.getPath());
            resource.setType(resourceUpdateDTO.getType());
            return resourceRepository.save(resource);
        }
    }

    @Override
    public List<Resource> getAllResource() {
        List<Resource> resourceList = resourceRepository.findAll();
        return resourceList;
    }

    @Override
    public Resource getResourceById(Long id) {
        if (!resourceRepository.findById(id).isPresent()) {
            throw new ResourceNotFoundException("<Resource not found>");
        } else {
            return resourceRepository.findById(id).get();
        }
    }

    @Override
    public List<Resource> getResourcesByType(String type) {
        List<Resource> resourceList = new ArrayList<>();
        resourceRepository.findAll().forEach(resource -> {
            if (resource.getType().equals(type)) {
                resourceList.add(resource);
            }
        });
        return  resourceList;
    }
}
