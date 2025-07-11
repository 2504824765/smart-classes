package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.DepartmentNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.TeacherNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.UserAlreadyExistException;
import com.bnwzy.smartclassesspringbootweb.exception.UserNotFoundException;
import com.bnwzy.smartclassesspringbootweb.pojo.Classes;
import com.bnwzy.smartclassesspringbootweb.pojo.Department;
import com.bnwzy.smartclassesspringbootweb.pojo.Teacher;
import com.bnwzy.smartclassesspringbootweb.pojo.User;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.TeacherCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.TeacherUpdateDTO;
import com.bnwzy.smartclassesspringbootweb.repository.ClassesRepository;
import com.bnwzy.smartclassesspringbootweb.repository.DepartmentRepository;
import com.bnwzy.smartclassesspringbootweb.repository.TeacherRepository;
import com.bnwzy.smartclassesspringbootweb.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TeacherServiceTest {
    private TeacherService teacherService;
    private TeacherRepository teacherRepository;
    private DepartmentRepository departmentRepository;
    private UserRepository userRepository;
    private ClassesRepository classesRepository;

    @BeforeEach
    void setUp() {
        teacherRepository = Mockito.mock(TeacherRepository.class);
        departmentRepository = Mockito.mock(DepartmentRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        classesRepository = Mockito.mock(ClassesRepository.class);
        teacherService = new TeacherService();
        teacherService.teacherRepository = teacherRepository;
        teacherService.departmentRepository = departmentRepository;
        teacherService.userRepository = userRepository;
        teacherService.classesRepository = classesRepository;
    }

    @Test
    void testAddTeacherSuccess() {
        TeacherCreateDTO dto = new TeacherCreateDTO();
        dto.setUsername("user");
        dto.setName("name");
        dto.setGender("男");
        dto.setDepartmentId(10L);

        Department dept = new Department();
        dept.setId(10L);

        Mockito.when(userRepository.findByUsername("user")).thenReturn(Optional.of(new User()));
        Mockito.when(departmentRepository.findById(10L)).thenReturn(Optional.of(dept));
        Mockito.when(teacherRepository.findByUsername("user")).thenReturn(Optional.empty());
        Mockito.when(teacherRepository.save(ArgumentMatchers.any(Teacher.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Teacher result = teacherService.addTeacher(dto);
        assertEquals("name", result.getName());
        assertEquals("男", result.getGender());
        assertEquals("user", result.getUsername());
        assertEquals(dept, result.getDepartment());
    }

    @Test
    void testAddTeacherUserNotFound() {
        TeacherCreateDTO dto = new TeacherCreateDTO();
        dto.setUsername("nouser");
        Mockito.when(userRepository.findByUsername("nouser")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> teacherService.addTeacher(dto));
    }

    @Test
    void testAddTeacherDepartmentNotFound() {
        TeacherCreateDTO dto = new TeacherCreateDTO();
        dto.setUsername("user");
        dto.setDepartmentId(10L);
        Mockito.when(userRepository.findByUsername("user")).thenReturn(Optional.of(new com.bnwzy.smartclassesspringbootweb.pojo.User()));
        Mockito.when(departmentRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(DepartmentNotFoundException.class, () -> teacherService.addTeacher(dto));
    }

    @Test
    void testAddTeacherAlreadyExist() {
        TeacherCreateDTO dto = new TeacherCreateDTO();
        dto.setUsername("user");
        dto.setDepartmentId(10L);
        Mockito.when(userRepository.findByUsername("user")).thenReturn(Optional.of(new com.bnwzy.smartclassesspringbootweb.pojo.User()));
        Mockito.when(departmentRepository.findById(10L)).thenReturn(Optional.of(new Department()));
        Mockito.when(teacherRepository.findByUsername("user")).thenReturn(Optional.of(new Teacher()));
        assertThrows(UserAlreadyExistException.class, () -> teacherService.addTeacher(dto));
    }

    @Test
    void testUpdateTeacherSuccess() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        Department dept = new Department(); dept.setId(10L);
        TeacherUpdateDTO dto = new TeacherUpdateDTO();
        dto.setId(1L);
        dto.setName("newName");
        dto.setGender("女");
        dto.setDepartmentId(10L);
        Mockito.when(teacherRepository.existsById(1L)).thenReturn(true);
        Mockito.when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        Mockito.when(departmentRepository.findById(10L)).thenReturn(Optional.of(dept));
        Mockito.when(teacherRepository.save(teacher)).thenReturn(teacher);
        Teacher result = teacherService.updateTeacher(dto);
        assertEquals("newName", result.getName());
        assertEquals("女", result.getGender());
        assertEquals(dept, result.getDepartment());
    }

    @Test
    void testUpdateTeacherNotFound() {
        TeacherUpdateDTO dto = new TeacherUpdateDTO();
        dto.setId(2L);
        Mockito.when(teacherRepository.existsById(2L)).thenReturn(false);
        assertThrows(TeacherNotFoundException.class, () -> teacherService.updateTeacher(dto));
    }

    @Test
    void testUpdateTeacherDepartmentNotFound() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        TeacherUpdateDTO dto = new TeacherUpdateDTO();
        dto.setId(1L);
        dto.setDepartmentId(10L);
        Mockito.when(teacherRepository.existsById(1L)).thenReturn(true);
        Mockito.when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        Mockito.when(departmentRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(DepartmentNotFoundException.class, () -> teacherService.updateTeacher(dto));
    }

    @Test
    void testDeleteByIdSuccessWithClasses() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        Classes c1 = new Classes(); c1.setId(100L); c1.setTeacher(teacher);
        List<Classes> classes = new ArrayList<>();
        classes.add(c1);
        Mockito.when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        Mockito.when(classesRepository.findByTeacher(teacher)).thenReturn(classes);
        assertTrue(teacherService.deleteById(1L));
        assertNull(c1.getTeacher());
        Mockito.verify(teacherRepository).delete(teacher);
    }

    @Test
    void testDeleteByIdSuccessNoClasses() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        List<Classes> classes = new ArrayList<>();
        Mockito.when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        Mockito.when(classesRepository.findByTeacher(teacher)).thenReturn(classes);
        assertTrue(teacherService.deleteById(1L));
        Mockito.verify(teacherRepository).delete(teacher);
    }

    @Test
    void testDeleteByIdNotFound() {
        Mockito.when(teacherRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(TeacherNotFoundException.class, () -> teacherService.deleteById(2L));
    }

    @Test
    void testGetTeacherByIdSuccess() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        Mockito.when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        assertEquals(teacher, teacherService.getTeacherById(1L));
    }

    @Test
    void testGetTeacherByIdNotFound() {
        Mockito.when(teacherRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(TeacherNotFoundException.class, () -> teacherService.getTeacherById(2L));
    }

    @Test
    void testGetTeacherByUsernameSuccess() {
        Teacher teacher = new Teacher();
        teacher.setUsername("test");
        Mockito.when(teacherRepository.findByUsername("test")).thenReturn(Optional.of(teacher));
        assertEquals(teacher, teacherService.getTeacherByUsername("test"));
    }

    @Test
    void testGetTeacherByUsernameNotFound() {
        Mockito.when(teacherRepository.findByUsername("notfound")).thenReturn(Optional.empty());
        assertThrows(TeacherNotFoundException.class, () -> teacherService.getTeacherByUsername("notfound"));
    }

    @Test
    void testGetAllTeacher() {
        List<Teacher> teachers = new ArrayList<>();
        Teacher t1 = new Teacher(); t1.setId(1L);
        Teacher t2 = new Teacher(); t2.setId(2L);
        teachers.add(t1); teachers.add(t2);
        Mockito.when(teacherRepository.findAll()).thenReturn(teachers);
        List<Teacher> result = teacherService.getAllTeacher();
        assertEquals(2, result.size());
    }

    @Test
    void testGetTeacherCount() {
        Mockito.when(teacherRepository.count()).thenReturn(5L);
        assertEquals(5L, teacherService.getTeacherCount());
    }

    @Test
    void testGetTeachersOfDeptSuccess() {
        Department dept = new Department();
        dept.setId(10L);
        List<Teacher> teachers = new ArrayList<>();
        Mockito.when(departmentRepository.findById(10L)).thenReturn(Optional.of(dept));
        Mockito.when(teacherRepository.findByDepartment(dept)).thenReturn(teachers);
        assertEquals(teachers, teacherService.getTeachersOfDept(10L));
    }

    @Test
    void testGetTeachersOfDeptDepartmentNotFound() {
        Mockito.when(departmentRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(DepartmentNotFoundException.class, () -> teacherService.getTeachersOfDept(10L));
    }

    // 边界条件测试
    @Test
    void testAddTeacherWithEmptyUsername() {
        TeacherCreateDTO dto = new TeacherCreateDTO();
        dto.setUsername("");
        dto.setDepartmentId(10L);
        Mockito.when(userRepository.findByUsername("")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> teacherService.addTeacher(dto));
    }

    @Test
    void testUpdateTeacherWithNullFields() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        TeacherUpdateDTO dto = new TeacherUpdateDTO();
        dto.setId(1L);
        Mockito.when(teacherRepository.existsById(1L)).thenReturn(true);
        Mockito.when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        Mockito.when(teacherRepository.save(teacher)).thenReturn(teacher);
        Teacher result = teacherService.updateTeacher(dto);
        assertNotNull(result);
    }
} 