package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.ClassesNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.ResourceNameAlreadyExistException;
import com.bnwzy.smartclassesspringbootweb.exception.ResourceNotFoundException;
import com.bnwzy.smartclassesspringbootweb.pojo.Classes;
import com.bnwzy.smartclassesspringbootweb.pojo.Resource;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ResourceCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ResourceUpdateDTO;
import com.bnwzy.smartclassesspringbootweb.repository.ClassesRepository;
import com.bnwzy.smartclassesspringbootweb.repository.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ResourceServiceTest {
    private ResourceService service;
    private ResourceRepository resourceRepository;
    private ClassesRepository classesRepository;

    @BeforeEach
    void setUp() {
        resourceRepository = Mockito.mock(ResourceRepository.class);
        classesRepository = Mockito.mock(ClassesRepository.class);
        service = new ResourceService();
        service.setResourceRepository(resourceRepository);
        service.setClassesRepository(classesRepository);
    }

    @Test
    void testCreateResourceSuccess() {
        ResourceCreateDTO dto = new ResourceCreateDTO();
        dto.setName("r1");
        dto.setClassId(1L);
        Classes classes = new Classes(); classes.setId(1L);
        Mockito.when(resourceRepository.findByName("r1")).thenReturn(Optional.empty());
        Mockito.when(classesRepository.findById(1L)).thenReturn(Optional.of(classes));
        Resource resource = new Resource();
        Mockito.when(resourceRepository.save(ArgumentMatchers.any(Resource.class))).thenReturn(resource);
        Resource result = service.createResource(dto);
        assertNotNull(result);
    }

    @Test
    void testCreateResourceNameAlreadyExist() {
        ResourceCreateDTO dto = new ResourceCreateDTO();
        dto.setName("exist");
        Mockito.when(resourceRepository.findByName("exist")).thenReturn(Optional.of(new Resource()));
        assertThrows(ResourceNameAlreadyExistException.class, () -> service.createResource(dto));
    }

    @Test
    void testCreateResourceClassNotFound() {
        ResourceCreateDTO dto = new ResourceCreateDTO();
        dto.setName("r1");
        dto.setClassId(2L);
        Mockito.when(resourceRepository.findByName("r1")).thenReturn(Optional.empty());
        Mockito.when(classesRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ClassesNotFoundException.class, () -> service.createResource(dto));
    }

    @Test
    void testDeleteResourceSuccess() {
        Mockito.when(resourceRepository.existsById(1L)).thenReturn(true);
        assertTrue(service.deleteResource(1L));
        Mockito.verify(resourceRepository).deleteById(1L);
    }

    @Test
    void testDeleteResourceNotFound() {
        Mockito.when(resourceRepository.existsById(2L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> service.deleteResource(2L));
    }

    @Test
    void testUpdateResourceSuccess() {
        Resource resource = new Resource(); resource.setId(1L);
        Classes classes = new Classes(); classes.setId(2L);
        ResourceUpdateDTO dto = new ResourceUpdateDTO();
        dto.setId(1L);
        dto.setClassId(2L);
        dto.setName("newName");
        dto.setPath("/new/path");
        dto.setType("pdf");
        Mockito.when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));
        Mockito.when(classesRepository.findById(2L)).thenReturn(Optional.of(classes));
        Mockito.when(resourceRepository.save(resource)).thenReturn(resource);
        Resource result = service.updateResource(dto);
        assertEquals("newName", result.getName());
        assertEquals("/new/path", result.getPath());
        assertEquals("pdf", result.getType());
        assertEquals(classes, result.getClasses());
    }

    @Test
    void testUpdateResourceNotFound() {
        ResourceUpdateDTO dto = new ResourceUpdateDTO();
        dto.setId(2L);
        Mockito.when(resourceRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.updateResource(dto));
    }

    @Test
    void testUpdateResourceClassNotFound() {
        Resource resource = new Resource(); resource.setId(1L);
        ResourceUpdateDTO dto = new ResourceUpdateDTO();
        dto.setId(1L);
        dto.setClassId(3L);
        Mockito.when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));
        Mockito.when(classesRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(ClassesNotFoundException.class, () -> service.updateResource(dto));
    }

    @Test
    void testGetAllResource() {
        List<Resource> resources = new ArrayList<>();
        Resource r1 = new Resource(); r1.setId(1L);
        Resource r2 = new Resource(); r2.setId(2L);
        resources.add(r1); resources.add(r2);
        Mockito.when(resourceRepository.findAll()).thenReturn(resources);
        List<Resource> result = service.getAllResource();
        assertEquals(2, result.size());
    }

    @Test
    void testGetResourceByIdSuccess() {
        Resource resource = new Resource(); resource.setId(1L);
        Mockito.when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));
        assertEquals(resource, service.getResourceById(1L));
    }

    @Test
    void testGetResourceByIdNotFound() {
        Mockito.when(resourceRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getResourceById(2L));
    }

    @Test
    void testGetResourcesByType() {
        Resource r1 = new Resource(); r1.setType("pdf");
        Resource r2 = new Resource(); r2.setType("doc");
        Resource r3 = new Resource(); r3.setType("pdf");
        List<Resource> all = Arrays.asList(r1, r2, r3);
        Mockito.when(resourceRepository.findAll()).thenReturn(all);
        List<Resource> result = service.getResourcesByType("pdf");
        assertEquals(2, result.size());
    }

    @Test
    void testGetResourceByClassIdSuccess() {
        Classes classes = new Classes(); classes.setId(1L);
        List<Resource> resources = new ArrayList<>();
        Mockito.when(classesRepository.findById(1L)).thenReturn(Optional.of(classes));
        Mockito.when(resourceRepository.findByClasses(classes)).thenReturn(resources);
        List<Resource> result = service.getResourceByClassId(1L);
        assertEquals(resources, result);
    }

    @Test
    void testGetResourceByClassIdClassNotFound() {
        Mockito.when(classesRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ClassesNotFoundException.class, () -> service.getResourceByClassId(2L));
    }

    // 边界条件测试
    @Test
    void testCreateResourceWithEmptyName() {
        ResourceCreateDTO dto = new ResourceCreateDTO();
        dto.setName("");
        dto.setClassId(1L);
        Classes classes = new Classes(); classes.setId(1L);
        Mockito.when(resourceRepository.findByName("")).thenReturn(Optional.empty());
        Mockito.when(classesRepository.findById(1L)).thenReturn(Optional.of(classes));
        Resource resource = new Resource();
        Mockito.when(resourceRepository.save(ArgumentMatchers.any(Resource.class))).thenReturn(resource);
        Resource result = service.createResource(dto);
        assertNotNull(result);
    }
} 