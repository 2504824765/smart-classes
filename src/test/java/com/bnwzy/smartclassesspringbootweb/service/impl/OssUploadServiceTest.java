package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.config.OssProperties;
import com.bnwzy.smartclassesspringbootweb.exception.*;
import com.bnwzy.smartclassesspringbootweb.pojo.Classes;
import com.bnwzy.smartclassesspringbootweb.repository.ClassesRepository;
import com.bnwzy.smartclassesspringbootweb.repository.ResourceRepository;
import com.bnwzy.smartclassesspringbootweb.repository.UserRepository;
import com.bnwzy.smartclassesspringbootweb.utils.ALiOssUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class OssUploadServiceTest {
    private OssUploadService service;
    private ClassesRepository classesRepository;
    private ALiOssUtil aliOssUtil;

    @BeforeEach
    void setUp() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ResourceRepository resourceRepository = Mockito.mock(ResourceRepository.class);
        classesRepository = Mockito.mock(ClassesRepository.class);
        aliOssUtil = Mockito.mock(ALiOssUtil.class);
        OssProperties ossProperties = Mockito.mock(OssProperties.class);
        service = new OssUploadService();
        service.setUserRepository(userRepository);
        service.setResourceRepository(resourceRepository);
        service.setClassesRepository(classesRepository);
        service.setAliOssUtil(aliOssUtil);
        service.setOssProperties(ossProperties);
    }

    // uploadImage
    @Test
    void testUploadImageSuccess() throws IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.isEmpty()).thenReturn(false);
        Mockito.when(file.getOriginalFilename()).thenReturn("test.jpg");
        Mockito.when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{}));
        Mockito.when(aliOssUtil.uploadFile(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn("url");
        String url = service.uploadImage(file);
        assertEquals("url", url);
    }

    @Test
    void testUploadImageFileIsNull() {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.isEmpty()).thenReturn(true);
        assertThrows(FileIsNullException.class, () -> service.uploadImage(file));
    }

    @Test
    void testUploadImageInvalidFilename() {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.isEmpty()).thenReturn(false);
        Mockito.when(file.getOriginalFilename()).thenReturn(null);
        assertThrows(ImageUploadException.class, () -> service.uploadImage(file));
        Mockito.when(file.getOriginalFilename()).thenReturn("noext");
        assertThrows(ImageUploadException.class, () -> service.uploadImage(file));
    }

    @Test
    void testUploadImageNotAllowedExtension() {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.isEmpty()).thenReturn(false);
        Mockito.when(file.getOriginalFilename()).thenReturn("test.exe");
        assertThrows(ImageUploadException.class, () -> service.uploadImage(file));
    }

    @Test
    void testUploadImageIOException() throws IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.isEmpty()).thenReturn(false);
        Mockito.when(file.getOriginalFilename()).thenReturn("test.jpg");
        Mockito.when(file.getInputStream()).thenThrow(new IOException());
        assertThrows(ImageUploadException.class, () -> service.uploadImage(file));
    }

    // uploadGraph
    @Test
    void testUploadGraphSuccess() throws IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.isEmpty()).thenReturn(false);
        Mockito.when(file.getOriginalFilename()).thenReturn("graph.json");
        Mockito.when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{}));
        Mockito.when(aliOssUtil.uploadFile(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn("url");
        String url = service.uploadGraph(file);
        assertEquals("url", url);
    }

    @Test
    void testUploadGraphFileIsNull() {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.isEmpty()).thenReturn(true);
        assertThrows(FileIsNullException.class, () -> service.uploadGraph(file));
    }

    @Test
    void testUploadGraphInvalidFilename() {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.isEmpty()).thenReturn(false);
        Mockito.when(file.getOriginalFilename()).thenReturn(null);
        assertThrows(ImageUploadException.class, () -> service.uploadGraph(file));
        Mockito.when(file.getOriginalFilename()).thenReturn("noext");
        assertThrows(ImageUploadException.class, () -> service.uploadGraph(file));
    }

    @Test
    void testUploadGraphNotAllowedExtension() {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.isEmpty()).thenReturn(false);
        Mockito.when(file.getOriginalFilename()).thenReturn("test.exe");
        assertThrows(ImageUploadException.class, () -> service.uploadGraph(file));
    }

    @Test
    void testUploadGraphIOException() throws IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.isEmpty()).thenReturn(false);
        Mockito.when(file.getOriginalFilename()).thenReturn("graph.json");
        Mockito.when(file.getInputStream()).thenThrow(new IOException());
        assertThrows(ImageUploadException.class, () -> service.uploadGraph(file));
    }

    // uploadResource
    @Test
    void testUploadResourceSuccess() throws IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.isEmpty()).thenReturn(false);
        Mockito.when(file.getOriginalFilename()).thenReturn("test.pdf");
        Mockito.when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{}));
        Mockito.when(aliOssUtil.generateUniqueFilename(ArgumentMatchers.anyString())).thenReturn("unique.pdf");
        Mockito.when(aliOssUtil.uploadFile(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn("url");
        String url = service.uploadResource(file, "msg");
        assertEquals("url", url);
    }

    @Test
    void testUploadResourceFileIsNull() {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.isEmpty()).thenReturn(true);
        assertThrows(FileIsNullException.class, () -> service.uploadResource(file, "msg"));
    }

    @Test
    void testUploadResourceInvalidFilename() {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.isEmpty()).thenReturn(false);
        Mockito.when(file.getOriginalFilename()).thenReturn(null);
        assertThrows(ImageUploadException.class, () -> service.uploadResource(file, "msg"));
        Mockito.when(file.getOriginalFilename()).thenReturn("noext");
        assertThrows(ImageUploadException.class, () -> service.uploadResource(file, "msg"));
    }

    @Test
    void testUploadResourceNotAllowedExtension() {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.isEmpty()).thenReturn(false);
        Mockito.when(file.getOriginalFilename()).thenReturn("test.exe");
        assertThrows(ImageUploadException.class, () -> service.uploadResource(file, "msg"));
    }

    @Test
    void testUploadResourceEmptyMessage() throws IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.isEmpty()).thenReturn(false);
        Mockito.when(file.getOriginalFilename()).thenReturn("test.pdf");
        Mockito.when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{}));
        Mockito.when(aliOssUtil.generateUniqueFilename(ArgumentMatchers.anyString())).thenReturn("unique.pdf");
        Mockito.when(aliOssUtil.uploadFile(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn("url");
        String url = service.uploadResource(file, "");
        assertEquals("url", url);
    }

    @Test
    void testUploadResourceIOException() throws IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.isEmpty()).thenReturn(false);
        Mockito.when(file.getOriginalFilename()).thenReturn("test.pdf");
        Mockito.when(file.getInputStream()).thenThrow(new IOException());
        Mockito.when(aliOssUtil.generateUniqueFilename(ArgumentMatchers.anyString())).thenReturn("unique.pdf");
        assertThrows(ImageUploadException.class, () -> service.uploadResource(file, "msg"));
    }

    // getAllByClassId
    @Test
    void testGetAllByClassIdSuccess() {
        Classes classes = new Classes();
        classes.setId(1L);
        classes.setName("class1");
        Mockito.when(classesRepository.findById(1L)).thenReturn(Optional.of(classes));
        List<String> files = new ArrayList<>();
        files.add("file1.pdf");
        files.add("https://smart-class-northeast.oss-cn-beijing.aliyuncs.com/file2.pdf");
        Mockito.when(aliOssUtil.listFilesInFolder("class/class1/resource/")).thenReturn(files);
        List<String> result = service.getAllByClassId(1L);
        assertEquals(2, result.size());
        assertTrue(result.get(0).startsWith("https://smart-class-northeast.oss-cn-beijing.aliyuncs.com/"));
    }

    @Test
    void testGetAllByClassIdNotFound() {
        Mockito.when(classesRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ClassesNotFoundException.class, () -> service.getAllByClassId(2L));
    }
} 