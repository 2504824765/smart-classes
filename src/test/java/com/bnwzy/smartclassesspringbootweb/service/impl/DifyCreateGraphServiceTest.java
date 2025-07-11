package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.pojo.dto.DifyCreateGraphDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DifyCreateGraphServiceTest {
    private DifyCreateGraphService service;
    private OssUploadService ossUploadService;

    @BeforeEach
    void setUp() {
        ossUploadService = Mockito.mock(OssUploadService.class);
        service = new DifyCreateGraphService();
        service.setOssUploadService(ossUploadService);
    }

    @Test
    void testValidateRequestInputsNull() throws Exception {
        DifyCreateGraphDTO dto = new DifyCreateGraphDTO();
        dto.setInputs(null);
        var method = service.getClass().getDeclaredMethod("validateRequest", DifyCreateGraphDTO.class);
        method.setAccessible(true);
        Exception ex = assertThrows(Exception.class, () -> method.invoke(service, dto));
        assertTrue(ex.getCause().getMessage().contains("inputs不能为空"));
    }

    @Test
    void testValidateRequestInputsEmpty() throws Exception {
        DifyCreateGraphDTO dto = new DifyCreateGraphDTO();
        dto.setInputs(new HashMap<>());
        var method = service.getClass().getDeclaredMethod("validateRequest", DifyCreateGraphDTO.class);
        method.setAccessible(true);
        Exception ex = assertThrows(Exception.class, () -> method.invoke(service, dto));
        assertTrue(ex.getCause().getMessage().contains("inputs不能为空"));
    }

    @Test
    void testValidateRequestFileListEmpty() throws Exception {
        DifyCreateGraphDTO dto = new DifyCreateGraphDTO();
        Map<String, List<DifyCreateGraphDTO.FileInput>> inputs = new HashMap<>();
        inputs.put("files", new ArrayList<>());
        dto.setInputs(inputs);
        var method = service.getClass().getDeclaredMethod("validateRequest", DifyCreateGraphDTO.class);
        method.setAccessible(true);
        Exception ex = assertThrows(Exception.class, () -> method.invoke(service, dto));
        assertTrue(ex.getCause().getMessage().contains("文件列表不能为空"));
    }

    @Test
    void testValidateRequestFileTypeNull() throws Exception {
        DifyCreateGraphDTO dto = new DifyCreateGraphDTO();
        Map<String, List<DifyCreateGraphDTO.FileInput>> inputs = new HashMap<>();
        List<DifyCreateGraphDTO.FileInput> files = new ArrayList<>();
        DifyCreateGraphDTO.FileInput file = new DifyCreateGraphDTO.FileInput();
        file.setType(null);
        files.add(file);
        inputs.put("files", files);
        dto.setInputs(inputs);
        var method = service.getClass().getDeclaredMethod("validateRequest", DifyCreateGraphDTO.class);
        method.setAccessible(true);
        Exception ex = assertThrows(Exception.class, () -> method.invoke(service, dto));
        assertTrue(ex.getCause().getMessage().contains("文件类型(type)不能为空"));
    }

    @Test
    void testValidateRequestTransferMethodNull() throws Exception {
        DifyCreateGraphDTO dto = new DifyCreateGraphDTO();
        Map<String, List<DifyCreateGraphDTO.FileInput>> inputs = new HashMap<>();
        List<DifyCreateGraphDTO.FileInput> files = new ArrayList<>();
        DifyCreateGraphDTO.FileInput file = new DifyCreateGraphDTO.FileInput();
        file.setType("pdf");
        file.setTransfer_method(null);
        files.add(file);
        inputs.put("files", files);
        dto.setInputs(inputs);
        var method = service.getClass().getDeclaredMethod("validateRequest", DifyCreateGraphDTO.class);
        method.setAccessible(true);
        Exception ex = assertThrows(Exception.class, () -> method.invoke(service, dto));
        assertTrue(ex.getCause().getMessage().contains("传输方式(transfer_method)不能为空"));
    }

    @Test
    void testValidateRequestRemoteUrlWithoutUrl() throws Exception {
        DifyCreateGraphDTO dto = new DifyCreateGraphDTO();
        Map<String, List<DifyCreateGraphDTO.FileInput>> inputs = new HashMap<>();
        List<DifyCreateGraphDTO.FileInput> files = new ArrayList<>();
        DifyCreateGraphDTO.FileInput file = new DifyCreateGraphDTO.FileInput();
        file.setType("pdf");
        file.setTransfer_method("remote_url");
        file.setUrl(null);
        files.add(file);
        inputs.put("files", files);
        dto.setInputs(inputs);
        var method = service.getClass().getDeclaredMethod("validateRequest", DifyCreateGraphDTO.class);
        method.setAccessible(true);
        Exception ex = assertThrows(Exception.class, () -> method.invoke(service, dto));
        assertTrue(ex.getCause().getMessage().contains("remote_url方式必须提供url"));
    }

    @Test
    void testValidateRequestLocalFileWithoutUploadFileId() throws Exception {
        DifyCreateGraphDTO dto = new DifyCreateGraphDTO();
        Map<String, List<DifyCreateGraphDTO.FileInput>> inputs = new HashMap<>();
        List<DifyCreateGraphDTO.FileInput> files = new ArrayList<>();
        DifyCreateGraphDTO.FileInput file = new DifyCreateGraphDTO.FileInput();
        file.setType("pdf");
        file.setTransfer_method("local_file");
        file.setUpload_file_id(null);
        files.add(file);
        inputs.put("files", files);
        dto.setInputs(inputs);
        var method = service.getClass().getDeclaredMethod("validateRequest", DifyCreateGraphDTO.class);
        method.setAccessible(true);
        Exception ex = assertThrows(Exception.class, () -> method.invoke(service, dto));
        assertTrue(ex.getCause().getMessage().contains("local_file方式必须提供upload_file_id"));
    }

    @Test
    void testGenerateAndUploadTxtSuccess() throws IOException {
        Mockito.when(ossUploadService.uploadGraph(Mockito.any(MultipartFile.class))).thenReturn("url");
        Mono<String> result = service.generateAndUploadTxt("content", "base");
        assertEquals("url", result.block());
    }

    @Test
    void testGenerateAndUploadTxtIOException() {
        DifyCreateGraphService spy = Mockito.spy(service);
        try {
            Mockito.doThrow(new IOException("fail")).when(spy).createTextFile(Mockito.anyString(), Mockito.anyString());
        } catch (IOException e) { }
        Mono<String> result = spy.generateAndUploadTxt("content", "base");
        RuntimeException thrown = assertThrows(RuntimeException.class, result::block);
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
        Mockito.when(file.getOriginalFilename()).thenReturn("test.json");
        Mockito.when(file.getSize()).thenReturn(100L);
        Mockito.when(ossUploadService.uploadGraph(file)).thenReturn("url");
        var method = service.getClass().getDeclaredMethod("uploadFile", MultipartFile.class);
        method.setAccessible(true);
        Mono<String> result = (Mono<String>) method.invoke(service, file);
        assertEquals("url", result.block());
    }

    @Test
    void testUploadFileNull() throws Exception {
        var method = service.getClass().getDeclaredMethod("uploadFile", MultipartFile.class);
        method.setAccessible(true);
        Mono<String> result = (Mono<String>) method.invoke(service, (MultipartFile) null);
        assertThrows(IllegalArgumentException.class, result::block);
    }

    @Test
    void testUploadFileEmpty() throws Exception {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.isEmpty()).thenReturn(true);
        var method = service.getClass().getDeclaredMethod("uploadFile", MultipartFile.class);
        method.setAccessible(true);
        Mono<String> result = (Mono<String>) method.invoke(service, file);
        assertThrows(IllegalArgumentException.class, result::block);
    }

    @Test
    void testUploadFileFail() throws Exception {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getOriginalFilename()).thenReturn("test.json");
        Mockito.when(file.getSize()).thenReturn(100L);
        Mockito.when(ossUploadService.uploadGraph(file)).thenThrow(new RuntimeException("fail"));
        var method = service.getClass().getDeclaredMethod("uploadFile", MultipartFile.class);
        method.setAccessible(true);
        Mono<String> result = (Mono<String>) method.invoke(service, file);
        assertThrows(RuntimeException.class, result::block);
    }
} 