package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.DepartmentAlreadyExistException;
import com.bnwzy.smartclassesspringbootweb.exception.DepartmentHasChildrenException;
import com.bnwzy.smartclassesspringbootweb.exception.DepartmentNotFoundException;
import com.bnwzy.smartclassesspringbootweb.pojo.Department;
import com.bnwzy.smartclassesspringbootweb.pojo.Student;
import com.bnwzy.smartclassesspringbootweb.pojo.Teacher;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.DeptCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.DeptUpdateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.DeptDetailDTO;
import com.bnwzy.smartclassesspringbootweb.repository.DepartmentRepository;
import com.bnwzy.smartclassesspringbootweb.repository.StudentRepository;
import com.bnwzy.smartclassesspringbootweb.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DepartmentServiceTest {
    private DepartmentService departmentService;
    private DepartmentRepository departmentRepository;
    private TeacherRepository teacherRepository;
    private StudentRepository studentRepository;

    @BeforeEach
    void setUp() {
        departmentRepository = Mockito.mock(DepartmentRepository.class);
        teacherRepository = Mockito.mock(TeacherRepository.class);
        studentRepository = Mockito.mock(StudentRepository.class);
        departmentService = new DepartmentService();
        departmentService.setDepartmentRepository(departmentRepository);
        departmentService.setTeacherRepository(teacherRepository);
        departmentService.setStudentRepository(studentRepository);
    }

    @Test
    void testCreateDeptSuccessNoParent() {
        DeptCreateDTO dto = new DeptCreateDTO();
        dto.setName("dept");
        dto.setParentId(0L);
        Mockito.when(departmentRepository.findByName("dept")).thenReturn(Optional.empty());
        Department dept = new Department();
        Mockito.when(departmentRepository.save(ArgumentMatchers.any(Department.class))).thenReturn(dept);
        Department result = departmentService.createDept(dto);
        assertNotNull(result);
    }

    @Test
    void testCreateDeptSuccessWithParent() {
        DeptCreateDTO dto = new DeptCreateDTO();
        dto.setName("dept");
        dto.setParentId(1L);
        Department parent = new Department(); parent.setId(1L); parent.setDepartmentLevel(2);
        Mockito.when(departmentRepository.findByName("dept")).thenReturn(Optional.empty());
        Mockito.when(departmentRepository.findById(1L)).thenReturn(Optional.of(parent));
        Department dept = new Department();
        Mockito.when(departmentRepository.save(ArgumentMatchers.any(Department.class))).thenReturn(dept);
        Department result = departmentService.createDept(dto);
        assertNotNull(result);
    }

    @Test
    void testCreateDeptAlreadyExist() {
        DeptCreateDTO dto = new DeptCreateDTO();
        dto.setName("exist");
        Mockito.when(departmentRepository.findByName("exist")).thenReturn(Optional.of(new Department()));
        assertThrows(DepartmentAlreadyExistException.class, () -> departmentService.createDept(dto));
    }

    @Test
    void testCreateDeptParentNotFound() {
        DeptCreateDTO dto = new DeptCreateDTO();
        dto.setName("dept");
        dto.setParentId(2L);
        Mockito.when(departmentRepository.findByName("dept")).thenReturn(Optional.empty());
        Mockito.when(departmentRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(DepartmentNotFoundException.class, () -> departmentService.createDept(dto));
    }

    @Test
    void testDeleteDeptSuccessNoChildren() {
        Department dept = new Department(); dept.setId(1L);
        Mockito.when(departmentRepository.findById(1L)).thenReturn(Optional.of(dept));
        Mockito.when(departmentRepository.findByParentId(1L)).thenReturn(Collections.emptyList());
        Mockito.when(teacherRepository.findByDepartment(dept)).thenReturn(Collections.emptyList());
        Mockito.when(studentRepository.findByDepartment(dept)).thenReturn(Collections.emptyList());
        assertTrue(departmentService.deleteDept(1L));
        Mockito.verify(departmentRepository).delete(dept);
    }

    @Test
    void testDeleteDeptNotFound() {
        Mockito.when(departmentRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(DepartmentNotFoundException.class, () -> departmentService.deleteDept(2L));
    }

    @Test
    void testDeleteDeptHasChildren() {
        Department dept = new Department(); dept.setId(1L);
        Department child = new Department(); child.setId(2L);
        List<Department> children = new ArrayList<>(); children.add(child);
        Mockito.when(departmentRepository.findById(1L)).thenReturn(Optional.of(dept));
        Mockito.when(departmentRepository.findByParentId(1L)).thenReturn(children);
        assertThrows(DepartmentHasChildrenException.class, () -> departmentService.deleteDept(1L));
    }

    @Test
    void testDeleteDeptWithTeachersAndStudents() {
        Department dept = new Department(); dept.setId(1L);
        Mockito.when(departmentRepository.findById(1L)).thenReturn(Optional.of(dept));
        Mockito.when(departmentRepository.findByParentId(1L)).thenReturn(Collections.emptyList());
        Teacher t = new Teacher(); t.setId(1L); t.setDepartment(dept);
        Student s = new Student(); s.setId(1L); s.setDepartment(dept);
        List<Teacher> teachers = new ArrayList<>(); teachers.add(t);
        List<Student> students = new ArrayList<>(); students.add(s);
        Mockito.when(teacherRepository.findByDepartment(dept)).thenReturn(teachers);
        Mockito.when(studentRepository.findByDepartment(dept)).thenReturn(students);
        assertTrue(departmentService.deleteDept(1L));
        assertNull(t.getDepartment());
        assertNull(s.getDepartment());
        Mockito.verify(departmentRepository).delete(dept);
    }

    @Test
    void testUpdateDeptSuccessNoParent() {
        Department oldDept = new Department(); oldDept.setId(1L);
        DeptUpdateDTO dto = new DeptUpdateDTO();
        dto.setId(1L);
        dto.setName("newName");
        dto.setParentId(0L);
        Mockito.when(departmentRepository.findById(1L)).thenReturn(Optional.of(oldDept));
        Mockito.when(departmentRepository.save(oldDept)).thenReturn(oldDept);
        Department result = departmentService.updateDept(dto);
        assertEquals("newName", result.getName());
    }

    @Test
    void testUpdateDeptSuccessWithParent() {
        Department oldDept = new Department(); oldDept.setId(1L);
        Department parent = new Department(); parent.setId(2L); parent.setDepartmentLevel(3);
        DeptUpdateDTO dto = new DeptUpdateDTO();
        dto.setId(1L);
        dto.setName("newName");
        dto.setParentId(2L);
        Mockito.when(departmentRepository.findById(1L)).thenReturn(Optional.of(oldDept));
        Mockito.when(departmentRepository.findById(2L)).thenReturn(Optional.of(parent));
        Mockito.when(departmentRepository.save(oldDept)).thenReturn(oldDept);
        Department result = departmentService.updateDept(dto);
        assertEquals("newName", result.getName());
    }

    @Test
    void testUpdateDeptNotFound() {
        DeptUpdateDTO dto = new DeptUpdateDTO();
        dto.setId(2L);
        Mockito.when(departmentRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(DepartmentNotFoundException.class, () -> departmentService.updateDept(dto));
    }

    @Test
    void testUpdateDeptParentNotFound() {
        Department oldDept = new Department(); oldDept.setId(1L);
        DeptUpdateDTO dto = new DeptUpdateDTO();
        dto.setId(1L);
        dto.setParentId(2L);
        Mockito.when(departmentRepository.findById(1L)).thenReturn(Optional.of(oldDept));
        Mockito.when(departmentRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(DepartmentNotFoundException.class, () -> departmentService.updateDept(dto));
    }

    @Test
    void testGetAllDept() {
        List<Department> depts = new ArrayList<>();
        Department d1 = new Department(); d1.setId(1L);
        Department d2 = new Department(); d2.setId(2L);
        depts.add(d1); depts.add(d2);
        Mockito.when(departmentRepository.findAll()).thenReturn(depts);
        List<Department> result = departmentService.getAllDept();
        assertEquals(2, result.size());
    }

    @Test
    void testGetDeptByIdSuccess() {
        Department dept = new Department(); dept.setId(1L);
        Mockito.when(departmentRepository.findById(1L)).thenReturn(Optional.of(dept));
        assertEquals(dept, departmentService.getDeptById(1L));
    }

    @Test
    void testGetDeptByIdNotFound() {
        Mockito.when(departmentRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(DepartmentNotFoundException.class, () -> departmentService.getDeptById(2L));
    }

    @Test
    void testGetDeptByNameSuccess() {
        Department dept = new Department(); dept.setId(1L);
        Mockito.when(departmentRepository.findByName("d1")).thenReturn(Optional.of(dept));
        assertEquals(dept, departmentService.getDeptByName("d1"));
    }

    @Test
    void testGetDeptByNameNotFound() {
        Mockito.when(departmentRepository.findByName("notfound")).thenReturn(Optional.empty());
        assertThrows(DepartmentNotFoundException.class, () -> departmentService.getDeptByName("notfound"));
    }

    @Test
    void testGetDeptDetailByIdSuccess() {
        Department dept = new Department(); dept.setId(1L);
        List<Teacher> teachers = new ArrayList<>();
        List<Student> students = new ArrayList<>();
        Mockito.when(departmentRepository.findById(1L)).thenReturn(Optional.of(dept));
        Mockito.when(teacherRepository.findByDepartment(dept)).thenReturn(teachers);
        Mockito.when(studentRepository.findByDepartment(dept)).thenReturn(students);
        DeptDetailDTO dto = departmentService.getDeptDetailById(1L);
        assertEquals(dept, dto.getDepartment());
        assertEquals(teachers, dto.getTeachers());
        assertEquals(students, dto.getStudents());
    }

    @Test
    void testGetDeptDetailByIdNotFound() {
        Mockito.when(departmentRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(DepartmentNotFoundException.class, () -> departmentService.getDeptDetailById(2L));
    }

    // 边界条件测试
    @Test
    void testCreateDeptWithEmptyName() {
        DeptCreateDTO dto = new DeptCreateDTO();
        dto.setName("");
        Mockito.when(departmentRepository.findByName("")).thenReturn(Optional.empty());
        Department dept = new Department();
        Mockito.when(departmentRepository.save(ArgumentMatchers.any(Department.class))).thenReturn(dept);
        Department result = departmentService.createDept(dto);
        assertNotNull(result);
    }
} 