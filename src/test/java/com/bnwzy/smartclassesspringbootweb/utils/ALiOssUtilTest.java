package com.bnwzy.smartclassesspringbootweb.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.PutObjectRequest;
import com.bnwzy.smartclassesspringbootweb.config.OssProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ALiOssUtilTest {
    private ALiOssUtil aliOssUtil;

    @BeforeEach
    void setUp() {
        aliOssUtil = new ALiOssUtil();
        OssProperties ossProperties = new OssProperties();
        ossProperties.setEndpoint("http://oss-endpoint");
        ossProperties.setAccessKeyId("keyId");
        ossProperties.setAccessKeySecret("keySecret");
        ossProperties.setBucketName("bucket");
        aliOssUtil.setOssProperties(ossProperties);
    }

    @Test
    void testExtractPathFromUrl() {
        String url = "https://bucket.oss-cn-beijing.aliyuncs.com/folder/file.txt";
        String path = aliOssUtil.extractPathFromUrl(url);
        assertEquals("folder/file.txt", path);
    }

    @Test
    void testExtractPathFromUrlInvalid() {
        String url = "not a url";
        String path = aliOssUtil.extractPathFromUrl(url);
        assertEquals("not a url", path);
    }

    @Test
    void testGenerateUniqueFilenameNoExist() {
        ALiOssUtil spy = Mockito.spy(aliOssUtil);
        Mockito.doReturn(false).when(spy).fileExists("file.txt");
        String name = spy.generateUniqueFilename("file.txt");
        assertEquals("file.txt", name);
    }

    @Test
    void testGenerateUniqueFilenameExist() {
        ALiOssUtil spy = Mockito.spy(aliOssUtil);
        Mockito.doReturn(true).doReturn(false).when(spy).fileExists("file.txt");
        String name = spy.generateUniqueFilename("file.txt");
        assertEquals("file(1).txt", name);
    }

    @Test
    void testGenerateUniqueFilenameMultipleExist() {
        ALiOssUtil spy = Mockito.spy(aliOssUtil);

        Mockito.doAnswer(invocation -> {
            String filename = invocation.getArgument(0, String.class);
            // 模拟 file.txt 和 file(1).txt 存在，file(2).txt不存在
            return filename.equals("file.txt") || filename.equals("file(1).txt");
        }).when(spy).fileExists(Mockito.anyString());

        String result = spy.generateUniqueFilename("file.txt");
        assertEquals("file(2).txt", result);
    }


    @Test
    void testGenerateUniqueFilenameWithExtension() {
        ALiOssUtil spy = Mockito.spy(aliOssUtil);
        Mockito.doReturn(true).doReturn(false).when(spy).fileExists("test.pdf");
        String name = spy.generateUniqueFilename("test.pdf");
        assertEquals("test(1).pdf", name);
    }

    @Test
    void testGenerateUniqueFilenameNoExtension() {
        ALiOssUtil spy = Mockito.spy(aliOssUtil);
        Mockito.doReturn(true).doReturn(false).when(spy).fileExists("testfile");
        String name = spy.generateUniqueFilename("testfile");
        assertEquals("testfile(1)", name);
    }
} 