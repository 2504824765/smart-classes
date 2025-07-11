package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.pojo.dto.DifyGenerateQuestionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DifyGenerateQuestionServiceTest {
    private DifyGenerateQuestionService service;
    private OssUploadService ossUploadService;

    @BeforeEach
    void setUp() {
        ossUploadService = Mockito.mock(OssUploadService.class);
        service = new DifyGenerateQuestionService();
        service.setOssUploadService(ossUploadService);
    }

    @Test
    void testValidateRequestInputsNull() throws Exception {
        DifyGenerateQuestionDTO dto = new DifyGenerateQuestionDTO();
        dto.setInputs(null);
        var method = service.getClass().getDeclaredMethod("validateRequest", DifyGenerateQuestionDTO.class);
        method.setAccessible(true);
        Exception ex = assertThrows(Exception.class, () -> method.invoke(service, dto));
        assertTrue(ex.getCause().getMessage().contains("inputs 不能为空"));
    }

    @Test
    void testValidateRequestFileListNull() throws Exception {
        DifyGenerateQuestionDTO dto = new DifyGenerateQuestionDTO();
        DifyGenerateQuestionDTO.Inputs inputs = new DifyGenerateQuestionDTO.Inputs();
        inputs.setFile(null);
        dto.setInputs(inputs);
        var method = service.getClass().getDeclaredMethod("validateRequest", DifyGenerateQuestionDTO.class);
        method.setAccessible(true);
        Exception ex = assertThrows(Exception.class, () -> method.invoke(service, dto));
        assertTrue(ex.getCause().getMessage().contains("文件列表不能为空"));
    }

    @Test
    void testValidateRequestFileListEmpty() throws Exception {
        DifyGenerateQuestionDTO dto = new DifyGenerateQuestionDTO();
        DifyGenerateQuestionDTO.Inputs inputs = new DifyGenerateQuestionDTO.Inputs();
        inputs.setFile(new ArrayList<>());
        dto.setInputs(inputs);
        var method = service.getClass().getDeclaredMethod("validateRequest", DifyGenerateQuestionDTO.class);
        method.setAccessible(true);
        Exception ex = assertThrows(Exception.class, () -> method.invoke(service, dto));
        assertTrue(ex.getCause().getMessage().contains("文件列表不能为空"));
    }

    @Test
    void testValidateRequestFileTypeNull() throws Exception {
        DifyGenerateQuestionDTO dto = new DifyGenerateQuestionDTO();
        DifyGenerateQuestionDTO.Inputs inputs = new DifyGenerateQuestionDTO.Inputs();
        List<DifyGenerateQuestionDTO.FileInput> files = new ArrayList<>();
        DifyGenerateQuestionDTO.FileInput file = new DifyGenerateQuestionDTO.FileInput();
        file.setType(null);
        files.add(file);
        inputs.setFile(files);
        dto.setInputs(inputs);
        var method = service.getClass().getDeclaredMethod("validateRequest", DifyGenerateQuestionDTO.class);
        method.setAccessible(true);
        Exception ex = assertThrows(Exception.class, () -> method.invoke(service, dto));
        assertTrue(ex.getCause().getMessage().contains("type不能为空"));
    }

    @Test
    void testValidateRequestTransferMethodNull() throws Exception {
        DifyGenerateQuestionDTO dto = new DifyGenerateQuestionDTO();
        DifyGenerateQuestionDTO.Inputs inputs = new DifyGenerateQuestionDTO.Inputs();
        List<DifyGenerateQuestionDTO.FileInput> files = new ArrayList<>();
        DifyGenerateQuestionDTO.FileInput file = new DifyGenerateQuestionDTO.FileInput();
        file.setType("pdf");
        file.setTransfer_method(null);
        files.add(file);
        inputs.setFile(files);
        dto.setInputs(inputs);
        var method = service.getClass().getDeclaredMethod("validateRequest", DifyGenerateQuestionDTO.class);
        method.setAccessible(true);
        Exception ex = assertThrows(Exception.class, () -> method.invoke(service, dto));
        assertTrue(ex.getCause().getMessage().contains("transfer_method不能为空"));
    }

    @Test
    void testValidateRequestRemoteUrlWithoutUrl() throws Exception {
        DifyGenerateQuestionDTO dto = new DifyGenerateQuestionDTO();
        DifyGenerateQuestionDTO.Inputs inputs = new DifyGenerateQuestionDTO.Inputs();
        List<DifyGenerateQuestionDTO.FileInput> files = new ArrayList<>();
        DifyGenerateQuestionDTO.FileInput file = new DifyGenerateQuestionDTO.FileInput();
        file.setType("pdf");
        file.setTransfer_method("remote_url");
        file.setUrl(null);
        files.add(file);
        inputs.setFile(files);
        dto.setInputs(inputs);
        var method = service.getClass().getDeclaredMethod("validateRequest", DifyGenerateQuestionDTO.class);
        method.setAccessible(true);
        Exception ex = assertThrows(Exception.class, () -> method.invoke(service, dto));
        assertTrue(ex.getCause().getMessage().contains("remote_url方式必须提供url"));
    }

    @Test
    void testValidateRequestLocalFileWithoutUploadFileId() throws Exception {
        DifyGenerateQuestionDTO dto = new DifyGenerateQuestionDTO();
        DifyGenerateQuestionDTO.Inputs inputs = new DifyGenerateQuestionDTO.Inputs();
        List<DifyGenerateQuestionDTO.FileInput> files = new ArrayList<>();
        DifyGenerateQuestionDTO.FileInput file = new DifyGenerateQuestionDTO.FileInput();
        file.setType("pdf");
        file.setTransfer_method("local_file");
        file.setUpload_file_id(null);
        files.add(file);
        inputs.setFile(files);
        dto.setInputs(inputs);
        var method = service.getClass().getDeclaredMethod("validateRequest", DifyGenerateQuestionDTO.class);
        method.setAccessible(true);
        Exception ex = assertThrows(Exception.class, () -> method.invoke(service, dto));
        assertTrue(ex.getCause().getMessage().contains("local_file方式必须提供upload_file_id"));
    }

    @Test
    void testValidateRequestNodeNameNull() throws Exception {
        DifyGenerateQuestionDTO dto = new DifyGenerateQuestionDTO();
        DifyGenerateQuestionDTO.Inputs inputs = new DifyGenerateQuestionDTO.Inputs();
        List<DifyGenerateQuestionDTO.FileInput> files = new ArrayList<>();
        DifyGenerateQuestionDTO.FileInput file = new DifyGenerateQuestionDTO.FileInput();
        file.setType("pdf");
        file.setTransfer_method("remote_url");
        file.setUrl("http://example.com");
        files.add(file);
        inputs.setFile(files);
        inputs.setNodeName(null);
        dto.setInputs(inputs);
        var method = service.getClass().getDeclaredMethod("validateRequest", DifyGenerateQuestionDTO.class);
        method.setAccessible(true);
        Exception ex = assertThrows(Exception.class, () -> method.invoke(service, dto));
        assertTrue(ex.getCause().getMessage().contains("nodeName 不能为空"));
    }

    @Test
    void testValidateRequestNodeNameEmpty() throws Exception {
        DifyGenerateQuestionDTO dto = new DifyGenerateQuestionDTO();
        DifyGenerateQuestionDTO.Inputs inputs = new DifyGenerateQuestionDTO.Inputs();
        List<DifyGenerateQuestionDTO.FileInput> files = new ArrayList<>();
        DifyGenerateQuestionDTO.FileInput file = new DifyGenerateQuestionDTO.FileInput();
        file.setType("pdf");
        file.setTransfer_method("remote_url");
        file.setUrl("http://example.com");
        files.add(file);
        inputs.setFile(files);
        inputs.setNodeName("   ");
        dto.setInputs(inputs);
        var method = service.getClass().getDeclaredMethod("validateRequest", DifyGenerateQuestionDTO.class);
        method.setAccessible(true);
        Exception ex = assertThrows(Exception.class, () -> method.invoke(service, dto));
        assertTrue(ex.getCause().getMessage().contains("nodeName 不能为空"));
    }

    @Test
    void testValidateRequestQuantityNull() throws Exception {
        DifyGenerateQuestionDTO dto = new DifyGenerateQuestionDTO();
        DifyGenerateQuestionDTO.Inputs inputs = new DifyGenerateQuestionDTO.Inputs();
        List<DifyGenerateQuestionDTO.FileInput> files = new ArrayList<>();
        DifyGenerateQuestionDTO.FileInput file = new DifyGenerateQuestionDTO.FileInput();
        file.setType("pdf");
        file.setTransfer_method("remote_url");
        file.setUrl("http://example.com");
        files.add(file);
        inputs.setFile(files);
        inputs.setNodeName("test");
        inputs.setQuantity(null);
        dto.setInputs(inputs);
        var method = service.getClass().getDeclaredMethod("validateRequest", DifyGenerateQuestionDTO.class);
        method.setAccessible(true);
        Exception ex = assertThrows(Exception.class, () -> method.invoke(service, dto));
        assertTrue(ex.getCause().getMessage().contains("quantity 必须为正整数"));
    }

    @Test
    void testValidateRequestQuantityZero() throws Exception {
        DifyGenerateQuestionDTO dto = new DifyGenerateQuestionDTO();
        DifyGenerateQuestionDTO.Inputs inputs = new DifyGenerateQuestionDTO.Inputs();
        List<DifyGenerateQuestionDTO.FileInput> files = new ArrayList<>();
        DifyGenerateQuestionDTO.FileInput file = new DifyGenerateQuestionDTO.FileInput();
        file.setType("pdf");
        file.setTransfer_method("remote_url");
        file.setUrl("http://example.com");
        files.add(file);
        inputs.setFile(files);
        inputs.setNodeName("test");
        inputs.setQuantity(0);
        dto.setInputs(inputs);
        var method = service.getClass().getDeclaredMethod("validateRequest", DifyGenerateQuestionDTO.class);
        method.setAccessible(true);
        Exception ex = assertThrows(Exception.class, () -> method.invoke(service, dto));
        assertTrue(ex.getCause().getMessage().contains("quantity 必须为正整数"));
    }

    @Test
    void testValidateRequestQuantityNegative() throws Exception {
        DifyGenerateQuestionDTO dto = new DifyGenerateQuestionDTO();
        DifyGenerateQuestionDTO.Inputs inputs = new DifyGenerateQuestionDTO.Inputs();
        List<DifyGenerateQuestionDTO.FileInput> files = new ArrayList<>();
        DifyGenerateQuestionDTO.FileInput file = new DifyGenerateQuestionDTO.FileInput();
        file.setType("pdf");
        file.setTransfer_method("remote_url");
        file.setUrl("http://example.com");
        files.add(file);
        inputs.setFile(files);
        inputs.setNodeName("test");
        inputs.setQuantity(-1);
        dto.setInputs(inputs);
        var method = service.getClass().getDeclaredMethod("validateRequest", DifyGenerateQuestionDTO.class);
        method.setAccessible(true);
        Exception ex = assertThrows(Exception.class, () -> method.invoke(service, dto));
        assertTrue(ex.getCause().getMessage().contains("quantity 必须为正整数"));
    }

    @Test
    void testGenerateAndUploadTxtSuccess() throws IOException {
        Mockito.when(ossUploadService.uploadGraph(Mockito.any(MultipartFile.class))).thenReturn("url");
        Mono<String> result = service.generateAndUploadTxt("content", "base");
        assertEquals("url", result.block());
    }

    @Test
    void testGenerateAndUploadTxtIOException() {
        DifyGenerateQuestionService spy = Mockito.spy(service);
        try {
            Mockito.doThrow(new IOException("fail")).when(spy).createTextFile(Mockito.anyString(), Mockito.anyString());
        } catch (IOException e) { }
        Mono<String> result = spy.generateAndUploadTxt("content", "base");
        RuntimeException thrown = assertThrows(RuntimeException.class, result::block);
        assertTrue(thrown.getCause() instanceof IOException);
    }

    @Test
    void testCreateTextFileAndDelete() throws Exception {
        File file = service.createTextFile("abc", "testfile");
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