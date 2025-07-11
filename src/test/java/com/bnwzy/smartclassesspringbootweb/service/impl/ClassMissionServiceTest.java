package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.ClassMissionNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.ClassesNotFoundException;
import com.bnwzy.smartclassesspringbootweb.pojo.ClassMission;
import com.bnwzy.smartclassesspringbootweb.pojo.Classes;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ClassMissionCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ClassMissionUpdateDTO;
import com.bnwzy.smartclassesspringbootweb.repository.ClassMissionRepository;
import com.bnwzy.smartclassesspringbootweb.repository.ClassesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ClassMissionServiceTest {
    private ClassMissionService service;
    private ClassMissionRepository missionRepository;
    private ClassesRepository classesRepository;

    @BeforeEach
    void setUp() {
        missionRepository = Mockito.mock(ClassMissionRepository.class);
        classesRepository = Mockito.mock(ClassesRepository.class);
        service = new ClassMissionService();
        service.setClassMissionRepository(missionRepository);
        service.setClassesRepository(classesRepository);
    }

    @Test
    void testCreateClassMissionSuccess() {
        ClassMissionCreateDTO dto = new ClassMissionCreateDTO();
        dto.setCid(1L);
        Classes classes = new Classes();
        Mockito.when(classesRepository.findById(1L)).thenReturn(Optional.of(classes));
        ClassMission mission = new ClassMission();
        Mockito.when(missionRepository.save(Mockito.any())).thenReturn(mission);
        ClassMission result = service.createClassMission(dto);
        assertNotNull(result);
    }

    @Test
    void testCreateClassMissionClassNotFound() {
        ClassMissionCreateDTO dto = new ClassMissionCreateDTO();
        dto.setCid(2L);
        Mockito.when(classesRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ClassesNotFoundException.class, () -> service.createClassMission(dto));
    }

    @Test
    void testDeleteClassMissionSuccess() {
        ClassMission mission = new ClassMission();
        Mockito.when(missionRepository.findById(1L)).thenReturn(Optional.of(mission));
        assertTrue(service.deleteClassMission(1L));
        Mockito.verify(missionRepository).deleteById(1L);
    }

    @Test
    void testDeleteClassMissionNotFound() {
        Mockito.when(missionRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ClassMissionNotFoundException.class, () -> service.deleteClassMission(2L));
    }

    @Test
    void testUpdateClassMissionSuccess() {
        ClassMission mission = new ClassMission();
        mission.setId(1);
        ClassMissionUpdateDTO dto = new ClassMissionUpdateDTO();
        dto.setId(1L);
        dto.setCid(2L);
        dto.setType("type");
        dto.setDescription("desc");
        dto.setDeadline("2024-01-01");
        dto.setSubmitMethod("online");
        dto.setScore(100.0);
        Classes classes = new Classes();
        Mockito.when(missionRepository.findById(1L)).thenReturn(Optional.of(mission));
        Mockito.when(classesRepository.findById(2L)).thenReturn(Optional.of(classes));
        Mockito.when(missionRepository.save(mission)).thenReturn(mission);
        ClassMission result = service.updateClassMission(dto);
        assertEquals(mission, result);
    }

    @Test
    void testUpdateClassMissionNotFound() {
        ClassMissionUpdateDTO dto = new ClassMissionUpdateDTO();
        dto.setId(2L);
        Mockito.when(missionRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ClassMissionNotFoundException.class, () -> service.updateClassMission(dto));
    }

    @Test
    void testUpdateClassMissionClassNotFound() {
        ClassMission mission = new ClassMission();
        mission.setId(1);
        ClassMissionUpdateDTO dto = new ClassMissionUpdateDTO();
        dto.setId(1L);
        dto.setCid(3L);
        Mockito.when(missionRepository.findById(1L)).thenReturn(Optional.of(mission));
        Mockito.when(classesRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(ClassesNotFoundException.class, () -> service.updateClassMission(dto));
    }

    @Test
    void testGetAllClassMission() {
        List<ClassMission> list = new ArrayList<>();
        Mockito.when(missionRepository.findAll()).thenReturn(list);
        List<ClassMission> result = service.getAllClassMission();
        assertEquals(list, result);
    }

    @Test
    void testGetCLassMissionByCid() {
        List<ClassMission> list = new ArrayList<>();
        Mockito.when(missionRepository.findByClasses_Id(1L)).thenReturn(list);
        List<ClassMission> result = service.getCLassMissionByCid(1L);
        assertEquals(list, result);
    }

    @Test
    void testGetClassMissionByIdSuccess() {
        ClassMission mission = new ClassMission();
        Mockito.when(missionRepository.findById(1L)).thenReturn(Optional.of(mission));
        assertEquals(mission, service.getClassMissionById(1L));
    }

    @Test
    void testGetClassMissionByIdNotFound() {
        Mockito.when(missionRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ClassMissionNotFoundException.class, () -> service.getClassMissionById(2L));
    }
} 