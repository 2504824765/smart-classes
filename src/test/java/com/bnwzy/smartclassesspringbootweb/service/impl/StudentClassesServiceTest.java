package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.ClassesNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.StudentClassesNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.StudentNotFoundException;
import com.bnwzy.smartclassesspringbootweb.pojo.Classes;
import com.bnwzy.smartclassesspringbootweb.pojo.Student;
import com.bnwzy.smartclassesspringbootweb.pojo.StudentClasses;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentClassesCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentClassesUpdateDTO;
import com.bnwzy.smartclassesspringbootweb.repository.ClassesRepository;
import com.bnwzy.smartclassesspringbootweb.repository.StudentClassesRepository;
import com.bnwzy.smartclassesspringbootweb.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class StudentClassesServiceTest {
    private StudentClassesService service;
    private StudentClassesRepository studentClassesRepository;
    private StudentRepository studentRepository;
    private ClassesRepository classesRepository;

    @BeforeEach
    void setUp() {
        studentClassesRepository = Mockito.mock(StudentClassesRepository.class);
        studentRepository = Mockito.mock(StudentRepository.class);
        classesRepository = Mockito.mock(ClassesRepository.class);
        service = new StudentClassesService();
        service.studentClassesRepository = studentClassesRepository;
        service.studentRepository = studentRepository;
        service.classesRepository = classesRepository;
    }

    @Test
    void testAddClassRecordSuccess() {
        StudentClassesCreateDTO dto = new StudentClassesCreateDTO();
        dto.setSid(1L);
        dto.setCid(2L);
        Mockito.when(studentRepository.existsById(1L)).thenReturn(true);
        Mockito.when(classesRepository.existsById(2L)).thenReturn(true);
        Student student = new Student(); student.setId(1L);
        Classes classes = new Classes(); classes.setId(2L);
        Mockito.when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        Mockito.when(classesRepository.findById(2L)).thenReturn(Optional.of(classes));
        StudentClasses sc = new StudentClasses();
        Mockito.when(studentClassesRepository.save(ArgumentMatchers.any(StudentClasses.class))).thenReturn(sc);
        StudentClasses result = service.addClassRecord(dto);
        assertNotNull(result);
    }

    @Test
    void testAddClassRecordClassesNotFound() {
        StudentClassesCreateDTO dto = new StudentClassesCreateDTO();
        dto.setSid(1L);
        dto.setCid(2L);
        Mockito.when(studentRepository.existsById(1L)).thenReturn(true);
        Mockito.when(classesRepository.existsById(2L)).thenReturn(false);
        assertThrows(ClassesNotFoundException.class, () -> service.addClassRecord(dto));
    }

    @Test
    void testAddClassRecordStudentNotFound() {
        StudentClassesCreateDTO dto = new StudentClassesCreateDTO();
        dto.setSid(1L);
        dto.setCid(2L);
        Mockito.when(studentRepository.existsById(1L)).thenReturn(false);
        Mockito.when(classesRepository.existsById(2L)).thenReturn(true);
        assertThrows(StudentNotFoundException.class, () -> service.addClassRecord(dto));
    }

    @Test
    void testDeleteClassRecordByIdSuccess() {
        Mockito.when(studentRepository.existsById(1L)).thenReturn(true);
        assertTrue(service.deleteClassRecordById(1L));
        Mockito.verify(studentClassesRepository).deleteById(1L);
    }

    @Test
    void testDeleteClassRecordByIdNotFound() {
        Mockito.when(studentRepository.existsById(2L)).thenReturn(false);
        assertThrows(StudentClassesNotFoundException.class, () -> service.deleteClassRecordById(2L));
    }

    @Test
    void testUpdateClassRecordSuccess() {
        StudentClassesUpdateDTO dto = new StudentClassesUpdateDTO();
        dto.setSid(1L);
        dto.setCid(2L);
        dto.setGrade(95.0);
        Mockito.when(studentRepository.existsById(1L)).thenReturn(true);
        Mockito.when(classesRepository.existsById(2L)).thenReturn(true);
        Student student = new Student(); student.setId(1L);
        Classes classes = new Classes(); classes.setId(2L);
        StudentClasses sc = new StudentClasses(); sc.setId(10L); sc.setStudent(student); sc.setClasses(classes);
        List<StudentClasses> all = new ArrayList<>(); all.add(sc);
        Mockito.when(studentClassesRepository.findAll()).thenReturn(all);
        Mockito.when(studentClassesRepository.save(sc)).thenReturn(sc);
        StudentClasses result = service.updateClassRecord(dto);
        assertEquals(95.0, result.getGrade());
    }

    @Test
    void testUpdateClassRecordClassesNotFound() {
        StudentClassesUpdateDTO dto = new StudentClassesUpdateDTO();
        dto.setSid(1L);
        dto.setCid(2L);
        Mockito.when(studentRepository.existsById(1L)).thenReturn(true);
        Mockito.when(classesRepository.existsById(2L)).thenReturn(false);
        assertThrows(ClassesNotFoundException.class, () -> service.updateClassRecord(dto));
    }

    @Test
    void testUpdateClassRecordStudentNotFound() {
        StudentClassesUpdateDTO dto = new StudentClassesUpdateDTO();
        dto.setSid(1L);
        dto.setCid(2L);
        Mockito.when(studentRepository.existsById(1L)).thenReturn(false);
        Mockito.when(classesRepository.existsById(2L)).thenReturn(true);
        assertThrows(StudentNotFoundException.class, () -> service.updateClassRecord(dto));
    }

    @Test
    void testUpdateClassRecordNotFound() {
        StudentClassesUpdateDTO dto = new StudentClassesUpdateDTO();
        dto.setSid(1L);
        dto.setCid(2L);
        dto.setGrade(88.0);
        Mockito.when(studentRepository.existsById(1L)).thenReturn(true);
        Mockito.when(classesRepository.existsById(2L)).thenReturn(true);
        Mockito.when(studentClassesRepository.findAll()).thenReturn(new ArrayList<>());
        assertThrows(StudentClassesNotFoundException.class, () -> service.updateClassRecord(dto));
    }

    @Test
    void testGetStudentClassesByIdSuccess() {
        StudentClasses sc = new StudentClasses(); sc.setId(1L);
        Mockito.when(studentClassesRepository.findById(1L)).thenReturn(Optional.of(sc));
        assertEquals(sc, service.getStudentClassesById(1L));
    }

    @Test
    void testGetStudentClassesByIdNotFound() {
        Mockito.when(studentClassesRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(StudentClassesNotFoundException.class, () -> service.getStudentClassesById(2L));
    }

    @Test
    void testGetAllStudentClassesByStudentId() {
        Student student = new Student(); student.setId(1L);
        StudentClasses sc1 = new StudentClasses(); sc1.setId(10L); sc1.setStudent(student);
        StudentClasses sc2 = new StudentClasses(); sc2.setId(11L); sc2.setStudent(student);
        List<StudentClasses> all = new ArrayList<>(); all.add(sc1); all.add(sc2);
        Mockito.when(studentClassesRepository.findAll()).thenReturn(all);
        List<StudentClasses> result = service.getAllStudentClassesByStudentId(1L);
        assertEquals(2, result.size());
    }

    @Test
    void testGetAllStudentClassesByClassesIdSuccess() {
        Classes classes = new Classes(); classes.setId(2L);
        StudentClasses sc1 = new StudentClasses(); sc1.setId(10L); sc1.setClasses(classes);
        List<StudentClasses> all = new ArrayList<>(); all.add(sc1);
        Mockito.when(studentClassesRepository.findAll()).thenReturn(all);
        List<StudentClasses> result = service.getAllStudentClassesByClassesId(2L);
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllStudentClassesByClassesIdNotFound() {
        Mockito.when(studentClassesRepository.findAll()).thenReturn(new ArrayList<>());
        assertThrows(StudentClassesNotFoundException.class, () -> service.getAllStudentClassesByClassesId(2L));
    }

    // 边界条件测试
    @Test
    void testAddClassRecordWithNullFields() {
        StudentClassesCreateDTO dto = new StudentClassesCreateDTO();
        Mockito.when(studentRepository.existsById(null)).thenReturn(false);
        Mockito.when(classesRepository.existsById(null)).thenReturn(false);
        assertThrows(ClassesNotFoundException.class, () -> service.addClassRecord(dto));
    }
} 