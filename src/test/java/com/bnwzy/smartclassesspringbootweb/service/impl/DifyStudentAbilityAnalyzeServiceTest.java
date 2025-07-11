package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.StudentNotFoundException;
import com.bnwzy.smartclassesspringbootweb.pojo.Student;
import com.bnwzy.smartclassesspringbootweb.pojo.StudentData;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.DifyStudentAbilityAnalyzeDTO;
import com.bnwzy.smartclassesspringbootweb.repository.StudentDataRepository;
import com.bnwzy.smartclassesspringbootweb.repository.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DifyStudentAbilityAnalyzeServiceTest {
    private DifyStudentAbilityAnalyzeService service;
    private OssUploadService ossUploadService;

    @BeforeEach
    void setUp() {
        StudentRepository studentRepository = Mockito.mock(StudentRepository.class);
        StudentDataRepository studentDataRepository = Mockito.mock(StudentDataRepository.class);
        ossUploadService = Mockito.mock(OssUploadService.class);
        service = new DifyStudentAbilityAnalyzeService();
        service.setStudentRepository(studentRepository);
        service.setStudentDataRepository(studentDataRepository);
        service.setOssUploadService(ossUploadService);
    }

    @Test
    void testStudentAbilityAnalyzeNullStudentId() {
        DifyStudentAbilityAnalyzeDTO dto = new DifyStudentAbilityAnalyzeDTO();
        dto.setStudentId(null);
        assertThrows(IllegalArgumentException.class, () -> service.studentAbilityAnalyze(dto));
    }

    @Test
    void testValidateRequestInputsNull() throws Exception {
        DifyStudentAbilityAnalyzeDTO.Inputs inputs = null;
        DifyStudentAbilityAnalyzeDTO.Upload upload = new DifyStudentAbilityAnalyzeDTO.Upload(inputs, null, null);
        var method = service.getClass().getDeclaredMethod("validateRequest", DifyStudentAbilityAnalyzeDTO.Upload.class);
        method.setAccessible(true);
        Exception ex = assertThrows(Exception.class, () -> method.invoke(service, upload));
        assertTrue(ex.getCause().getMessage().contains("inputs 不能为空"));
    }

    @Test
    void testValidateRequestStudentReportNull() throws Exception {
        DifyStudentAbilityAnalyzeDTO.Inputs inputs = new DifyStudentAbilityAnalyzeDTO.Inputs();
        DifyStudentAbilityAnalyzeDTO.Upload upload = new DifyStudentAbilityAnalyzeDTO.Upload(inputs, null, null);
        var method = service.getClass().getDeclaredMethod("validateRequest", DifyStudentAbilityAnalyzeDTO.Upload.class);
        method.setAccessible(true);
        Exception ex = assertThrows(Exception.class, () -> method.invoke(service, upload));
        assertTrue(ex.getCause().getMessage().contains("studentReportFile不能为空"));
    }

    @Test
    void testValidateRequestStudentReportTypeNull() throws Exception {
        DifyStudentAbilityAnalyzeDTO.Inputs inputs = new DifyStudentAbilityAnalyzeDTO.Inputs();
        DifyStudentAbilityAnalyzeDTO.FileInput report = new DifyStudentAbilityAnalyzeDTO.FileInput();
        inputs.setStudentReport(report);
        DifyStudentAbilityAnalyzeDTO.Upload upload = new DifyStudentAbilityAnalyzeDTO.Upload(inputs, null, null);
        var method = service.getClass().getDeclaredMethod("validateRequest", DifyStudentAbilityAnalyzeDTO.Upload.class);
        method.setAccessible(true);
        Exception ex = assertThrows(Exception.class, () -> method.invoke(service, upload));
        assertTrue(ex.getCause().getMessage().contains("type不能为空"));
    }

    @Test
    void testGenerateAndUploadTxtSuccess() throws IOException {
        when(ossUploadService.uploadGraph(any(MultipartFile.class))).thenReturn("url");
        Mono<String> result = service.generateAndUploadTxt("content", "base");
        assertEquals("url", result.block());
    }

    @Test
    void testGenerateAndUploadTxtIOException() {
        DifyStudentAbilityAnalyzeService spy = Mockito.spy(service);
        try {
            doThrow(new IOException("fail")).when(spy).createTextFile(anyString(), anyString());
        } catch (IOException e) { }
        Mono<String> result = spy.generateAndUploadTxt("content", "base");
        Throwable thrown = assertThrows(RuntimeException.class, result::block);
        assertTrue(thrown.getCause() instanceof IOException);
    }

    @Test
    void testCreateTextFileAndDelete() throws Exception {
        var method = service.getClass().getDeclaredMethod("createTextFile", String.class, String.class);
        method.setAccessible(true);
        File file = (File) method.invoke(service, "abc", "testfile");
        assertTrue(file.exists());
        assertTrue(file.delete());
    }

    @Test
    void testUploadFileSuccess() throws Exception {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        when(ossUploadService.uploadGraph(file)).thenReturn("url");
        var method = service.getClass().getDeclaredMethod("uploadFile", MultipartFile.class);
        method.setAccessible(true);
        Mono<String> result = (Mono<String>) method.invoke(service, file);
        assertEquals("url", result.block());
    }

    @Test
    void testUploadFileFail() throws Exception {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        when(ossUploadService.uploadGraph(file)).thenThrow(new RuntimeException("fail"));
        var method = service.getClass().getDeclaredMethod("uploadFile", MultipartFile.class);
        method.setAccessible(true);
        Mono<String> result = (Mono<String>) method.invoke(service, file);
        assertThrows(RuntimeException.class, result::block);
    }
} 