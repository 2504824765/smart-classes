package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.StudentDataNotFoundException;
import com.bnwzy.smartclassesspringbootweb.pojo.StudentData;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentDataCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentDataUpdateDTO;
import com.bnwzy.smartclassesspringbootweb.repository.StudentDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class StudentDataServiceTest {
    private StudentDataService studentDataService;
    private StudentDataRepository studentDataRepository;

    @BeforeEach
    void setUp() {
        studentDataRepository = Mockito.mock(com.bnwzy.smartclassesspringbootweb.repository.StudentDataRepository.class);
        studentDataService = new StudentDataService();
        studentDataService.setStudentDataRepository(studentDataRepository);
    }

    @Test
    void testCreateStudentDateSuccess() {
        StudentDataCreateDTO dto = new StudentDataCreateDTO();
        dto.setConceptUnderstanding("A");
        dto.setExpressionNorms("B");
        dto.setProblemSolving("C");
        dto.setLogicalReasoning("D");
        dto.setInnovativeThinking("E");
        StudentData data = new StudentData();
        Mockito.when(studentDataRepository.save(ArgumentMatchers.any(StudentData.class))).thenReturn(data);
        StudentData result = studentDataService.createStudentDate(dto);
        assertNotNull(result);
    }

    @Test
    void testDeleteStudentDataSuccess() {
        StudentData data = new StudentData(); data.setId(1L);
        Mockito.when(studentDataRepository.findById(1L)).thenReturn(Optional.of(data));
        assertTrue(studentDataService.deleteStudentData(1L));
        Mockito.verify(studentDataRepository).deleteById(1L);
    }

    @Test
    void testDeleteStudentDataNotFound() {
        Mockito.when(studentDataRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(StudentDataNotFoundException.class, () -> studentDataService.deleteStudentData(2L));
    }

    @Test
    void testUpdateStudentDataSuccess() {
        StudentData data = new StudentData(); data.setId(1L);
        StudentDataUpdateDTO dto = new StudentDataUpdateDTO();
        dto.setId(1L);
        dto.setConceptUnderstanding("A");
        dto.setExpressionNorms("B");
        dto.setProblemSolving("C");
        dto.setLogicalReasoning("D");
        dto.setInnovativeThinking("E");
        Mockito.when(studentDataRepository.findById(1L)).thenReturn(Optional.of(data));
        Mockito.when(studentDataRepository.save(data)).thenReturn(data);
        StudentData result = studentDataService.updateStudentData(dto);
        assertEquals("A", result.getConceptUnderstanding());
        assertEquals("B", result.getExpressionNorms());
        assertEquals("C", result.getProblemSolving());
        assertEquals("D", result.getLogicalReasoning());
        assertEquals("E", result.getInnovativeThinking());
    }

    @Test
    void testUpdateStudentDataNotFound() {
        StudentDataUpdateDTO dto = new StudentDataUpdateDTO();
        dto.setId(2L);
        Mockito.when(studentDataRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(StudentDataNotFoundException.class, () -> studentDataService.updateStudentData(dto));
    }

    @Test
    void testGetStudentDataByIdSuccess() {
        StudentData data = new StudentData(); data.setId(1L);
        Mockito.when(studentDataRepository.findById(1L)).thenReturn(Optional.of(data));
        assertEquals(data, studentDataService.getStudentDataById(1L));
    }

    @Test
    void testGetStudentDataByIdNotFound() {
        Mockito.when(studentDataRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(StudentDataNotFoundException.class, () -> studentDataService.getStudentDataById(2L));
    }

    // 边界条件测试
    @Test
    void testCreateStudentDateWithNullFields() {
        StudentDataCreateDTO dto = new StudentDataCreateDTO();
        StudentData data = new StudentData();
        Mockito.when(studentDataRepository.save(ArgumentMatchers.any(StudentData.class))).thenReturn(data);
        StudentData result = studentDataService.createStudentDate(dto);
        assertNotNull(result);
    }
} 