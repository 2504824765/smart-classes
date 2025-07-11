package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.DepartmentNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.StudentDataNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.StudentNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.UserNotFoundException;
import com.bnwzy.smartclassesspringbootweb.pojo.Department;
import com.bnwzy.smartclassesspringbootweb.pojo.Student;
import com.bnwzy.smartclassesspringbootweb.pojo.StudentData;
import com.bnwzy.smartclassesspringbootweb.pojo.User;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentUpdateDTO;
import com.bnwzy.smartclassesspringbootweb.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class StudentServiceTest {
    private StudentService studentService;
    private StudentRepository studentRepository;
    private DepartmentRepository departmentRepository;
    private UserRepository userRepository;
    private StudentClassesRepository studentClassesRepository;
    private StudentDataRepository studentDataRepository;

    @BeforeEach
    void setUp() {
        studentRepository = Mockito.mock(StudentRepository.class);
        departmentRepository = Mockito.mock(DepartmentRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        studentClassesRepository = Mockito.mock(StudentClassesRepository.class);
        studentDataRepository = Mockito.mock(StudentDataRepository.class);
        studentService = new StudentService();
        studentService.studentRepository = studentRepository;
        studentService.departmentRepository = departmentRepository;
        studentService.userRepository = userRepository;
        studentService.studentClassesRepository = studentClassesRepository;
        studentService.studentDataRepository = studentDataRepository;
    }

    @Test
    void testGetStudentByIdSuccess() {
        Student student = new Student();
        student.setId(1L);
        Mockito.when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        assertEquals(student, studentService.getStudentById(1L));
    }

    @Test
    void testGetStudentByIdNotFound() {
        Mockito.when(studentRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(StudentNotFoundException.class, () -> studentService.getStudentById(2L));
    }

    @Test
    void testUpdateStudentSuccess() {
        Student student = new Student();
        student.setId(1L);
        Department dept = new Department();
        dept.setId(10L);
        StudentData data = new StudentData();
        data.setId(100L);
        Mockito.when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        Mockito.when(departmentRepository.findById(10L)).thenReturn(Optional.of(dept));
        Mockito.when(studentDataRepository.findById(100L)).thenReturn(Optional.of(data));
        StudentUpdateDTO dto = new StudentUpdateDTO();
        dto.setId(1L);
        dto.setName("newName");
        dto.setGender("女");
        dto.setDeptId(10L);
        dto.setGpa(3.5);
        dto.setStudentDataId(100L);
        Mockito.when(studentRepository.save(student)).thenReturn(student);
        Student result = studentService.updateStudent(dto);
        assertEquals("newName", result.getName());
        assertEquals("女", result.getGender());
        assertEquals(dept, result.getDepartment());
        assertEquals(3.5, result.getGpa());
        assertEquals(data, result.getStudentData());
    }

    @Test
    void testUpdateStudentNotFound() {
        StudentUpdateDTO dto = new StudentUpdateDTO();
        dto.setId(2L);
        Mockito.when(studentRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(StudentNotFoundException.class, () -> studentService.updateStudent(dto));
    }

    @Test
    void testUpdateStudentDepartmentNotFound() {
        Student student = new Student();
        student.setId(1L);
        StudentUpdateDTO dto = new StudentUpdateDTO();
        dto.setId(1L);
        dto.setDeptId(10L);
        Mockito.when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        Mockito.when(departmentRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(DepartmentNotFoundException.class, () -> studentService.updateStudent(dto));
    }

    @Test
    void testUpdateStudentStudentDataNotFound() {
        Student student = new Student();
        student.setId(1L);
        StudentUpdateDTO dto = new StudentUpdateDTO();
        dto.setId(1L);
        dto.setStudentDataId(100L);
        Mockito.when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        Mockito.when(studentDataRepository.findById(100L)).thenReturn(Optional.empty());
        assertThrows(StudentDataNotFoundException.class, () -> studentService.updateStudent(dto));
    }

    @Test
    void testGetStudentByUsernameSuccess() {
        Student student = new Student();
        student.setUsername("test");
        Mockito.when(studentRepository.findByUsername("test")).thenReturn(Optional.of(student));
        assertEquals(student, studentService.getStudentByUsername("test"));
    }

    @Test
    void testGetStudentByUsernameNotFound() {
        Mockito.when(studentRepository.findByUsername("notfound")).thenReturn(Optional.empty());
        assertThrows(StudentNotFoundException.class, () -> studentService.getStudentByUsername("notfound"));
    }

    @Test
    void testGetAllStudent() {
        List<Student> students = new ArrayList<>();
        Student s1 = new Student(); s1.setId(1L);
        Student s2 = new Student(); s2.setId(2L);
        students.add(s1); students.add(s2);
        Mockito.when(studentRepository.findAll()).thenReturn(students);
        List<Student> result = studentService.getAllStudent();
        assertEquals(2, result.size());
    }

    @Test
    void testCreateStudentSuccess() {
        StudentCreateDTO dto = new StudentCreateDTO();
        dto.setUsername("user");
        dto.setDeptId(10L);
        dto.setName("name");
        dto.setGender("男");
        dto.setGpa(4.0);
        dto.setStudentDataId(100L);

        Department dept = new Department(); dept.setId(10L);
        StudentData data = new StudentData(); data.setId(100L);

        Mockito.when(userRepository.findByUsername("user")).thenReturn(Optional.of(new User()));
        Mockito.when(departmentRepository.findById(10L)).thenReturn(Optional.of(dept));
        Mockito.when(studentDataRepository.findById(100L)).thenReturn(Optional.of(data));

        Mockito.when(studentRepository.save(ArgumentMatchers.any(Student.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Student result = studentService.createStudent(dto);

        assertEquals("name", result.getName());
        assertEquals("男", result.getGender());
        assertEquals(dept, result.getDepartment());
        assertEquals(4.0, result.getGpa());
        assertEquals(data, result.getStudentData());
    }


    @Test
    void testCreateStudentUserNotFound() {
        StudentCreateDTO dto = new StudentCreateDTO();
        dto.setUsername("nouser");
        Mockito.when(userRepository.findByUsername("nouser")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> studentService.createStudent(dto));
    }

    @Test
    void testCreateStudentDepartmentNotFound() {
        StudentCreateDTO dto = new StudentCreateDTO();
        dto.setUsername("user");
        dto.setDeptId(10L);
        Mockito.when(userRepository.findByUsername("user")).thenReturn(Optional.of(new User()));
        Mockito.when(departmentRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(DepartmentNotFoundException.class, () -> studentService.createStudent(dto));
    }

    @Test
    void testCreateStudentStudentDataNotFound() {
        StudentCreateDTO dto = new StudentCreateDTO();
        dto.setUsername("user");
        dto.setDeptId(10L);
        dto.setStudentDataId(100L);
        Department dept = new Department(); dept.setId(10L);
        Mockito.when(userRepository.findByUsername("user")).thenReturn(Optional.of(new User()));
        Mockito.when(departmentRepository.findById(10L)).thenReturn(Optional.of(dept));
        Mockito.when(studentDataRepository.findById(100L)).thenReturn(Optional.empty());
        assertThrows(StudentDataNotFoundException.class, () -> studentService.createStudent(dto));
    }

    @Test
    void testDeleteStudentSuccess() {
        Student student = new Student();
        student.setId(1L);
        Mockito.when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        assertTrue(studentService.deleteStudent(1L));
        Mockito.verify(studentClassesRepository).deleteByStudent_Id(1L);
        Mockito.verify(studentRepository).deleteById(1L);
    }

    @Test
    void testDeleteStudentNotFound() {
        Mockito.when(studentRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(StudentNotFoundException.class, () -> studentService.deleteStudent(2L));
    }

    @Test
    void testGetStudentCount() {
        Mockito.when(studentRepository.count()).thenReturn(5L);
        assertEquals(5L, studentService.getStudentCount());
    }

    @Test
    void testGetStudentOfDeptSuccess() {
        Department dept = new Department();
        dept.setId(10L);
        List<Student> students = new ArrayList<>();
        Mockito.when(departmentRepository.findById(10L)).thenReturn(Optional.of(dept));
        Mockito.when(studentRepository.findByDepartment(dept)).thenReturn(students);
        assertEquals(students, studentService.getStudentOfDept(10L));
    }

    @Test
    void testGetStudentOfDeptDepartmentNotFound() {
        Mockito.when(departmentRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(DepartmentNotFoundException.class, () -> studentService.getStudentOfDept(10L));
    }
}