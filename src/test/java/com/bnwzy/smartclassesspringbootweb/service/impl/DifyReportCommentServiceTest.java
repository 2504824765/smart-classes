package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.pojo.dto.DifyReportCommentDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class DifyReportCommentServiceTest {
    private DifyReportCommentService service;
    private OssUploadService ossUploadService;

    @BeforeEach
    void setUp() {
        ossUploadService = Mockito.mock(OssUploadService.class);
        service = new DifyReportCommentService();
        service.setOssUploadService(ossUploadService);
    }

    @Test
    void testValidateRequestInputsNull() throws Exception {
        DifyReportCommentDTO dto = new DifyReportCommentDTO();
        dto.setInputs(null);
        var method = service.getClass().getDeclaredMethod("validateRequest", DifyReportCommentDTO.class);
        method.setAccessible(true);
        Exception ex = assertThrows(Exception.class, () -> method.invoke(service, dto));
        assertTrue(ex.getCause().getMessage().contains("inputs 不能为空"));
    }

    @Test
    void testValidateRequestStudentReportNull() throws Exception {
        DifyReportCommentDTO dto = new DifyReportCommentDTO();
        DifyReportCommentDTO.Inputs inputs = new DifyReportCommentDTO.Inputs();
        dto.setInputs(inputs);
        var method = service.getClass().getDeclaredMethod("validateRequest", DifyReportCommentDTO.class);
        method.setAccessible(true);
        Exception ex = assertThrows(Exception.class, () -> method.invoke(service, dto));
        assertTrue(ex.getCause().getMessage().contains("文件不能为空"));
    }

    @Test
    void testValidateRequestStudentReportTypeNull() throws Exception {
        DifyReportCommentDTO dto = new DifyReportCommentDTO();
        DifyReportCommentDTO.Inputs inputs = new DifyReportCommentDTO.Inputs();
        DifyReportCommentDTO.FileInput report = new DifyReportCommentDTO.FileInput();
        DifyReportCommentDTO.FileInput moBan = new DifyReportCommentDTO.FileInput();
        inputs.setStudentReport(report);
        inputs.setMoBan(moBan);
        dto.setInputs(inputs);
        var method = service.getClass().getDeclaredMethod("validateRequest", DifyReportCommentDTO.class);
        method.setAccessible(true);
        Exception ex = assertThrows(Exception.class, () -> method.invoke(service, dto));
        assertTrue(ex.getCause().getMessage().contains("type不能为空"));
    }

    @Test
    void testGenerateAndUploadTxtSuccess() throws IOException {
        Mockito.when(ossUploadService.uploadGraph(Mockito.any(MultipartFile.class))).thenReturn("url");
        Mono<String> result = service.generateAndUploadTxt("content", "base");
        assertEquals("url", result.block());
    }

    @Test
    void testGenerateAndUploadTxtIOException() {
        DifyReportCommentService spy = Mockito.spy(service);
        try {
            Mockito.doThrow(new IOException("fail")).when(spy).createTextFile(Mockito.anyString(), Mockito.anyString());
        } catch (IOException e) { }
        Mono<String> result = spy.generateAndUploadTxt("content", "base");
        RuntimeException thrown = assertThrows(RuntimeException.class, result::block);
        // 判断内部是否是 IOException
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
        Mockito.when(ossUploadService.uploadGraph(file)).thenReturn("url");
        var method = service.getClass().getDeclaredMethod("uploadFile", MultipartFile.class);
        method.setAccessible(true);
        Mono<String> result = (Mono<String>) method.invoke(service, file);
        assertEquals("url", result.block());
    }

    @Test
    void testUploadFileFail() throws Exception {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(ossUploadService.uploadGraph(file)).thenThrow(new RuntimeException("fail"));
        var method = service.getClass().getDeclaredMethod("uploadFile", MultipartFile.class);
        method.setAccessible(true);
        Mono<String> result = (Mono<String>) method.invoke(service, file);
        assertThrows(RuntimeException.class, result::block);
    }
} 