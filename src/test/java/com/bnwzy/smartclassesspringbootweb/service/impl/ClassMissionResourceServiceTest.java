package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.ClassMissionNotFoundException;
import com.bnwzy.smartclassesspringbootweb.pojo.ClassMission;
import com.bnwzy.smartclassesspringbootweb.pojo.ClassMissionResource;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ClassMissionResourceCreateDTO;
import com.bnwzy.smartclassesspringbootweb.repository.ClassMissionRepository;
import com.bnwzy.smartclassesspringbootweb.repository.ClassMissionResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ClassMissionResourceServiceTest {
    private ClassMissionResourceService service;
    private ClassMissionResourceRepository resourceRepository;
    private ClassMissionRepository missionRepository;

    @BeforeEach
    void setUp() {
        resourceRepository = Mockito.mock(ClassMissionResourceRepository.class);
        missionRepository = Mockito.mock(ClassMissionRepository.class);
        service = new ClassMissionResourceService();
        service.setClassMissionResourceRepository(resourceRepository);
        service.setClassMissionRepository(missionRepository);
    }

    @Test
    void testCreateClassMissionResourceSuccess() {
        ClassMissionResourceCreateDTO dto = new ClassMissionResourceCreateDTO();
        dto.setClassMissionId(1L);
        dto.setPath("/path");
        ClassMission mission = new ClassMission();
        Mockito.when(missionRepository.findById(1L)).thenReturn(Optional.of(mission));
        ClassMissionResource resource = new ClassMissionResource();
        Mockito.when(resourceRepository.save(Mockito.any())).thenReturn(resource);
        ClassMissionResource result = service.createClassMissionResource(dto);
        assertNotNull(result);
    }

    @Test
    void testCreateClassMissionResourceNotFound() {
        ClassMissionResourceCreateDTO dto = new ClassMissionResourceCreateDTO();
        dto.setClassMissionId(2L);
        Mockito.when(missionRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ClassMissionNotFoundException.class, () -> service.createClassMissionResource(dto));
    }

    @Test
    void testGetAllClassMissionResourcesByClassMissionIdSuccess() {
        ClassMission mission = new ClassMission();
        List<ClassMissionResource> list = new ArrayList<>();
        Mockito.when(missionRepository.findById(1L)).thenReturn(Optional.of(mission));
        Mockito.when(resourceRepository.findByClassMission(mission)).thenReturn(list);
        List<ClassMissionResource> result = service.getAllClassMissionResourcesByClassMissionId(1L);
        assertEquals(list, result);
    }

    @Test
    void testGetAllClassMissionResourcesByClassMissionIdNotFound() {
        Mockito.when(missionRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ClassMissionNotFoundException.class, () -> service.getAllClassMissionResourcesByClassMissionId(2L));
    }
} 