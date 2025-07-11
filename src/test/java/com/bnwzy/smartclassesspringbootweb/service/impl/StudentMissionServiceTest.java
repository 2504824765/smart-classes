package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.ClassMissionNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.ClassesNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.StudentMissionNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.StudentNotFoundException;
import com.bnwzy.smartclassesspringbootweb.pojo.*;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.GetStudentMissionByStudentIdAndClassMissionIdDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentMissionCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentMissionUpdateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentsAllClassMissionGetDTO;
import com.bnwzy.smartclassesspringbootweb.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class StudentMissionServiceTest {
    private StudentMissionService service;
    private StudentMissionRepository studentMissionRepository;
    private ClassMissionRepository classMissionRepository;
    private StudentRepository studentRepository;
    private ClassesRepository classesRepository;
    private StudentClassesRepository studentClassesRepository;

    @BeforeEach
    void setUp() {
        studentMissionRepository = Mockito.mock(com.bnwzy.smartclassesspringbootweb.repository.StudentMissionRepository.class);
        classMissionRepository = Mockito.mock(com.bnwzy.smartclassesspringbootweb.repository.ClassMissionRepository.class);
        studentRepository = Mockito.mock(com.bnwzy.smartclassesspringbootweb.repository.StudentRepository.class);
        classesRepository = Mockito.mock(com.bnwzy.smartclassesspringbootweb.repository.ClassesRepository.class);
        studentClassesRepository = Mockito.mock(com.bnwzy.smartclassesspringbootweb.repository.StudentClassesRepository.class);
        service = new StudentMissionService();
        service.setStudentMissionRepository(studentMissionRepository);
        service.setClassMissionRepository(classMissionRepository);
        service.setStudentRepository(studentRepository);
        service.setClassesRepository(classesRepository);
        service.setStudentClassesRepository(studentClassesRepository);
    }

    @Test
    void testCreateStudentMissionSuccess() {
        StudentMissionCreateDTO dto = new StudentMissionCreateDTO();
        dto.setStudentId(1L);
        dto.setClassMissionId(2L);
        Student student = new Student(); student.setId(1L);
        ClassMission classMission = new ClassMission(); classMission.setId(2);
        Mockito.when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        Mockito.when(classMissionRepository.findById(2L)).thenReturn(Optional.of(classMission));
        StudentMission mission = new StudentMission();
        Mockito.when(studentMissionRepository.save(ArgumentMatchers.any(StudentMission.class))).thenReturn(mission);
        StudentMission result = service.createStudentMission(dto);
        assertNotNull(result);
    }

    @Test
    void testCreateStudentMissionStudentNotFound() {
        StudentMissionCreateDTO dto = new StudentMissionCreateDTO();
        dto.setStudentId(1L);
        Mockito.when(studentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(StudentNotFoundException.class, () -> service.createStudentMission(dto));
    }

    @Test
    void testCreateStudentMissionClassMissionNotFound() {
        StudentMissionCreateDTO dto = new StudentMissionCreateDTO();
        dto.setStudentId(1L);
        dto.setClassMissionId(2L);
        Student student = new Student(); student.setId(1L);
        Mockito.when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        Mockito.when(classMissionRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ClassMissionNotFoundException.class, () -> service.createStudentMission(dto));
    }

    @Test
    void testDeleteStudentMissionSuccess() {
        StudentMission mission = new StudentMission(); mission.setId(1L);
        Mockito.when(studentMissionRepository.findById(1L)).thenReturn(Optional.of(mission));
        assertTrue(service.deleteStudentMission(1L));
        Mockito.verify(studentMissionRepository).deleteById(1L);
    }

    @Test
    void testDeleteStudentMissionNotFound() {
        Mockito.when(studentMissionRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(StudentMissionNotFoundException.class, () -> service.deleteStudentMission(2L));
    }

    @Test
    void testUpdateStudentMissionSuccess() {
        StudentMission mission = new StudentMission(); mission.setId(1L);
        Student student = new Student(); student.setId(2L);
        ClassMission classMission = new ClassMission(); classMission.setId(3);
        StudentMissionUpdateDTO dto = new StudentMissionUpdateDTO();
        dto.setId(1L);
        dto.setStudentId(2L);
        dto.setClassMissionId(3L);
        dto.setScore(90);
        dto.setDone(true);
        dto.setActive(false);
        dto.setReportUrl("url");
        Mockito.when(studentMissionRepository.findById(1L)).thenReturn(Optional.of(mission));
        Mockito.when(studentRepository.findById(2L)).thenReturn(Optional.of(student));
        Mockito.when(classMissionRepository.findById(3L)).thenReturn(Optional.of(classMission));
        Mockito.when(studentMissionRepository.save(mission)).thenReturn(mission);
        StudentMission result = service.updateStudentMission(dto);
        assertEquals(student, result.getStudent());
        assertEquals(classMission, result.getClassMission());
        assertEquals(90, result.getScore());
        assertTrue(result.getDone());
        assertFalse(result.getActive());
        assertEquals("url", result.getReportUrl());
    }

    @Test
    void testUpdateStudentMissionNotFound() {
        StudentMissionUpdateDTO dto = new StudentMissionUpdateDTO();
        dto.setId(2L);
        Mockito.when(studentMissionRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(StudentMissionNotFoundException.class, () -> service.updateStudentMission(dto));
    }

    @Test
    void testUpdateStudentMissionStudentNotFound() {
        StudentMission mission = new StudentMission(); mission.setId(1L);
        StudentMissionUpdateDTO dto = new StudentMissionUpdateDTO();
        dto.setId(1L);
        dto.setStudentId(2L);
        Mockito.when(studentMissionRepository.findById(1L)).thenReturn(Optional.of(mission));
        Mockito.when(studentRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(StudentNotFoundException.class, () -> service.updateStudentMission(dto));
    }

    @Test
    void testUpdateStudentMissionClassMissionNotFound() {
        StudentMission mission = new StudentMission(); mission.setId(1L);
        StudentMissionUpdateDTO dto = new StudentMissionUpdateDTO();
        dto.setId(1L);
        dto.setClassMissionId(3L);
        Mockito.when(studentMissionRepository.findById(1L)).thenReturn(Optional.of(mission));
        Mockito.when(classMissionRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(ClassMissionNotFoundException.class, () -> service.updateStudentMission(dto));
    }

    @Test
    void testGetStudentMissionByIdSuccess() {
        StudentMission mission = new StudentMission(); mission.setId(1L);
        Mockito.when(studentMissionRepository.findById(1L)).thenReturn(Optional.of(mission));
        assertEquals(mission, service.getStudentMissionById(1L));
    }

    @Test
    void testGetStudentMissionByIdNotFound() {
        Mockito.when(studentMissionRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(StudentMissionNotFoundException.class, () -> service.getStudentMissionById(2L));
    }

    @Test
    void testGetStudentsAllClassMissionSuccess() {
        StudentsAllClassMissionGetDTO dto = new StudentsAllClassMissionGetDTO();
        dto.setClassId(1L);
        dto.setStudentId(2L);
        Classes classes = new Classes(); classes.setId(1L);
        Student student = new Student(); student.setId(2L);
        List<ClassMission> classMissionList = new ArrayList<>();
        List<StudentMission> studentMissions = new ArrayList<>();
        Mockito.when(classesRepository.findById(1L)).thenReturn(Optional.of(classes));
        Mockito.when(classMissionRepository.findByClasses_Id(1L)).thenReturn(classMissionList);
        Mockito.when(studentRepository.findById(2L)).thenReturn(Optional.of(student));
        Mockito.when(studentMissionRepository.findByClassMissionInAndStudent(classMissionList, student)).thenReturn(studentMissions);
        List<StudentMission> result = service.getStudentsAllClassMission(dto);
        assertEquals(studentMissions, result);
    }

    @Test
    void testGetStudentsAllClassMissionClassNotFound() {
        StudentsAllClassMissionGetDTO dto = new StudentsAllClassMissionGetDTO();
        dto.setClassId(1L);
        Mockito.when(classesRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ClassesNotFoundException.class, () -> service.getStudentsAllClassMission(dto));
    }

    @Test
    void testGetStudentsAllClassMissionStudentNotFound() {
        StudentsAllClassMissionGetDTO dto = new StudentsAllClassMissionGetDTO();
        dto.setClassId(1L);
        dto.setStudentId(2L);
        Classes classes = new Classes(); classes.setId(1L);
        List<ClassMission> classMissionList = new ArrayList<>();
        Mockito.when(classesRepository.findById(1L)).thenReturn(Optional.of(classes));
        Mockito.when(classMissionRepository.findByClasses_Id(1L)).thenReturn(classMissionList);
        Mockito.when(studentRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(StudentNotFoundException.class, () -> service.getStudentsAllClassMission(dto));
    }

    @Test
    void testGetStudentMissionsByStudentIdSuccess() {
        Student student = new Student(); student.setId(1L);
        List<StudentMission> missions = new ArrayList<>();
        Mockito.when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        Mockito.when(studentMissionRepository.findByStudent(student)).thenReturn(missions);
        assertEquals(missions, service.getStudentMissionsByStudentId(1L));
    }

    @Test
    void testGetStudentMissionsByStudentIdNotFound() {
        Mockito.when(studentRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(StudentNotFoundException.class, () -> service.getStudentMissionsByStudentId(2L));
    }

    @Test
    void testGetAllStudentMissionsOfClassMissionSuccess() {
        ClassMission classMission = new ClassMission(); classMission.setId(1);
        List<StudentMission> missions = new ArrayList<>();
        Mockito.when(classMissionRepository.findById(1L)).thenReturn(Optional.of(classMission));
        Mockito.when(studentMissionRepository.findByClassMission(classMission)).thenReturn(missions);
        assertEquals(missions, service.getAllStudentMissionsOfClassMission(1L));
    }

    @Test
    void testGetAllStudentMissionsOfClassMissionNotFound() {
        Mockito.when(classMissionRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ClassMissionNotFoundException.class, () -> service.getAllStudentMissionsOfClassMission(2L));
    }

    @Test
    void testGetStudentMissionByStudentIdAndClassMissionIdSuccess() {
        GetStudentMissionByStudentIdAndClassMissionIdDTO dto = new GetStudentMissionByStudentIdAndClassMissionIdDTO();
        dto.setClassMissionId(1L);
        dto.setStudentId(2L);
        ClassMission classMission = new ClassMission(); classMission.setId(1);
        Student student = new Student(); student.setId(2L);
        StudentMission mission = new StudentMission();
        Mockito.when(classMissionRepository.findById(1L)).thenReturn(Optional.of(classMission));
        Mockito.when(studentRepository.findById(2L)).thenReturn(Optional.of(student));
        Mockito.when(studentMissionRepository.findByClassMissionAndStudent(classMission, student)).thenReturn(mission);
        StudentMission result = service.getStudentMissionByStudentIdAndClassMissionId(dto);
        assertEquals(mission, result);
    }

    @Test
    void testGetStudentMissionByStudentIdAndClassMissionIdClassMissionNotFound() {
        GetStudentMissionByStudentIdAndClassMissionIdDTO dto = new GetStudentMissionByStudentIdAndClassMissionIdDTO();
        dto.setClassMissionId(1L);
        Mockito.when(classMissionRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ClassMissionNotFoundException.class, () -> service.getStudentMissionByStudentIdAndClassMissionId(dto));
    }

    @Test
    void testGetStudentMissionByStudentIdAndClassMissionIdStudentNotFound() {
        GetStudentMissionByStudentIdAndClassMissionIdDTO dto = new GetStudentMissionByStudentIdAndClassMissionIdDTO();
        dto.setClassMissionId(1L);
        dto.setStudentId(2L);
        ClassMission classMission = new ClassMission(); classMission.setId(1);
        Mockito.when(classMissionRepository.findById(1L)).thenReturn(Optional.of(classMission));
        Mockito.when(studentRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(StudentNotFoundException.class, () -> service.getStudentMissionByStudentIdAndClassMissionId(dto));
    }

    // 边界条件测试
    @Test
    void testCreateStudentMissionWithNullFields() {
        StudentMissionCreateDTO dto = new StudentMissionCreateDTO();
        assertThrows(StudentNotFoundException.class, () -> service.createStudentMission(dto));
    }
} 