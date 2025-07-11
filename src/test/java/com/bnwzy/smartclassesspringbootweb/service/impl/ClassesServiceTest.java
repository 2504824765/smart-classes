package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.ClassesNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.TeacherNotFoundException;
import com.bnwzy.smartclassesspringbootweb.pojo.Classes;
import com.bnwzy.smartclassesspringbootweb.pojo.Teacher;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ClassesCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ClassesUpdateDTO;
import com.bnwzy.smartclassesspringbootweb.repository.ClassesRepository;
import com.bnwzy.smartclassesspringbootweb.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ClassesServiceTest {
    private ClassesService classesService;
    private ClassesRepository classesRepository;
    private TeacherRepository teacherRepository;

    @BeforeEach
    void setUp() {
        classesRepository = Mockito.mock(ClassesRepository.class);
        teacherRepository = Mockito.mock(TeacherRepository.class);
        classesService = new ClassesService();
        classesService.setClassesRepository(classesRepository);
        classesService.setTeacherRepository(teacherRepository);
    }

    @Test
    void testAddClassSuccess() {
        ClassesCreateDTO dto = new ClassesCreateDTO();
        dto.setName("class1");
        dto.setTeacherId(1L);
        dto.setCredit(3.0);
        dto.setClassHours(40.0);
        dto.setGraph("g");
        dto.setDescription("desc");
        dto.setActive(true);
        dto.setImageUrl("img");
        Teacher teacher = new Teacher(); teacher.setId(1L);
        Mockito.when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        Classes classes = new Classes();
        Mockito.when(classesRepository.save(ArgumentMatchers.any(Classes.class))).thenReturn(classes);
        Classes result = classesService.addClass(dto);
        assertNotNull(result);
    }

    @Test
    void testAddClassTeacherNotFound() {
        ClassesCreateDTO dto = new ClassesCreateDTO();
        dto.setTeacherId(2L);
        Mockito.when(teacherRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(TeacherNotFoundException.class, () -> classesService.addClass(dto));
    }

    @Test
    void testDeleteClassSuccess() {
        Classes classes = new Classes(); classes.setId(1L);
        Mockito.when(classesRepository.findById(1L)).thenReturn(Optional.of(classes));
        assertTrue(classesService.deleteClass(1L));
        Mockito.verify(classesRepository).deleteById(1L);
    }

    @Test
    void testDeleteClassNotFound() {
        Mockito.when(classesRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ClassesNotFoundException.class, () -> classesService.deleteClass(2L));
    }

    @Test
    void testUpdateClassSuccess() {
        Classes classes = new Classes(); classes.setId(1L);
        Teacher teacher = new Teacher(); teacher.setId(2L);
        ClassesUpdateDTO dto = new ClassesUpdateDTO();
        dto.setId(1L);
        dto.setTeacherId(2L);
        dto.setName("newName");
        dto.setCredit(4.0);
        dto.setClassHours(50.0);
        dto.setGraph("newG");
        dto.setDescription("newDesc");
        dto.setActive(false);
        dto.setImageUrl("newImg");
        Mockito.when(classesRepository.findById(1L)).thenReturn(Optional.of(classes));
        Mockito.when(teacherRepository.findById(2L)).thenReturn(Optional.of(teacher));
        Mockito.when(classesRepository.save(classes)).thenReturn(classes);
        Classes result = classesService.updateClass(dto);
        assertEquals("newName", result.getName());
        assertEquals(4.0, result.getCredit());
        assertEquals(50.0, result.getClassHours());
        assertEquals("newG", result.getGraph());
        assertEquals("newDesc", result.getDescription());
        assertEquals(false, result.getActive());
        assertEquals("newImg", result.getImageUrl());
        assertEquals(teacher, result.getTeacher());
    }

    @Test
    void testUpdateClassNotFound() {
        ClassesUpdateDTO dto = new ClassesUpdateDTO();
        dto.setId(2L);
        Mockito.when(classesRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ClassesNotFoundException.class, () -> classesService.updateClass(dto));
    }

    @Test
    void testUpdateClassTeacherNotFound() {
        Classes classes = new Classes(); classes.setId(1L);
        ClassesUpdateDTO dto = new ClassesUpdateDTO();
        dto.setId(1L);
        dto.setTeacherId(3L);
        Mockito.when(classesRepository.findById(1L)).thenReturn(Optional.of(classes));
        Mockito.when(teacherRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(TeacherNotFoundException.class, () -> classesService.updateClass(dto));
    }

    @Test
    void testGetAllClasses() {
        List<Classes> classesList = new ArrayList<>();
        Classes c1 = new Classes(); c1.setId(1L);
        Classes c2 = new Classes(); c2.setId(2L);
        classesList.add(c1); classesList.add(c2);
        Mockito.when(classesRepository.findAll()).thenReturn(classesList);
        List<Classes> result = classesService.getAllClasses();
        assertEquals(2, result.size());
    }

    @Test
    void testGetClassByIdSuccess() {
        Classes classes = new Classes(); classes.setId(1L);
        Mockito.when(classesRepository.findById(1L)).thenReturn(Optional.of(classes));
        assertEquals(classes, classesService.getClassById(1L));
    }

    @Test
    void testGetClassByIdNotFound() {
        Mockito.when(classesRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ClassesNotFoundException.class, () -> classesService.getClassById(2L));
    }

    @Test
    void testGetClassByNameSuccess() {
        Classes classes = new Classes(); classes.setId(1L);
        Mockito.when(classesRepository.findByName("c1")).thenReturn(Optional.of(classes));
        assertEquals(classes, classesService.getClassByName("c1"));
    }

    @Test
    void testGetClassByNameNotFound() {
        Mockito.when(classesRepository.findByName("notfound")).thenReturn(Optional.empty());
        assertThrows(ClassesNotFoundException.class, () -> classesService.getClassByName("notfound"));
    }

    @Test
    void testGetClassByTeacherIdSuccess() {
        Teacher teacher = new Teacher(); teacher.setId(1L);
        List<Classes> classesList = new ArrayList<>();
        Mockito.when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        Mockito.when(classesRepository.findByTeacher(teacher)).thenReturn(classesList);
        assertEquals(classesList, classesService.getClassByTeacherId(1L));
    }

    @Test
    void testGetClassByTeacherIdTeacherNotFound() {
        Mockito.when(teacherRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(TeacherNotFoundException.class, () -> classesService.getClassByTeacherId(2L));
    }

    @Test
    void testGetClassCount() {
        Mockito.when(classesRepository.count()).thenReturn(7L);
        assertEquals(7L, classesService.getClassCount());
    }

    // 边界条件测试
    @Test
    void testAddClassWithEmptyName() {
        ClassesCreateDTO dto = new ClassesCreateDTO();
        dto.setName("");
        dto.setTeacherId(1L);
        Teacher teacher = new Teacher(); teacher.setId(1L);
        Mockito.when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        Classes classes = new Classes();
        Mockito.when(classesRepository.save(ArgumentMatchers.any(Classes.class))).thenReturn(classes);
        Classes result = classesService.addClass(dto);
        assertNotNull(result);
    }
} 