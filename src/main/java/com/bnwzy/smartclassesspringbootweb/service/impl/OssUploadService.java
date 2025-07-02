package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.config.OssProperties;
import com.bnwzy.smartclassesspringbootweb.exception.*;
import com.bnwzy.smartclassesspringbootweb.pojo.Classes;
import com.bnwzy.smartclassesspringbootweb.pojo.Resource;
import com.bnwzy.smartclassesspringbootweb.pojo.User;
import com.bnwzy.smartclassesspringbootweb.repository.ClassesRepository;
import com.bnwzy.smartclassesspringbootweb.repository.ResourceRepository;
import com.bnwzy.smartclassesspringbootweb.repository.UserRepository;
import com.bnwzy.smartclassesspringbootweb.service.IOssUploadService;
import com.bnwzy.smartclassesspringbootweb.utils.ALiOssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class OssUploadService implements IOssUploadService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ResourceRepository resourceRepository;

    @Autowired
    ClassesRepository classesRepository;

    @Autowired
    private ALiOssUtil aliOssUtil;
    @Autowired
    private OssProperties ossProperties;

    @Override
    public String uploadImage(MultipartFile file) {
        if(file.isEmpty()){
            throw new FileIsNullException("<File is null>");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.lastIndexOf(".") == -1) {
            throw new ImageUploadException("<文件名无效>");
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        Set<String> allowedExtensions = new HashSet<>(Arrays.asList(
                "png", "jpg", "jpeg", "gif", "bmp"
        ));
        if (!allowedExtensions.contains(extension)) {
            throw new ImageUploadException("<仅支持上传:PNG,JPG,JPEG,GIF,BMP>");
        }
        String url = null;
        String filename = originalFilename+ "user-images/"+UUID.randomUUID()+"."+extension;
        try {
            url = aliOssUtil.uploadFile(filename, file.getInputStream());
        } catch (IOException e) {
            throw new ImageUploadException("<Image upload failed>");
        }
        return url;
    }

    @Override
    public String uploadGraph(MultipartFile file) {
        if(file.isEmpty()){
            throw new FileIsNullException("<File is null>");
        }


        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.lastIndexOf(".") == -1) {
            throw new ImageUploadException("<文件名无效>");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
//        Set<String> allowedExtensions = new HashSet<>(List.of("json"));
//        if (!allowedExtensions.contains(extension)) {
//            throw new ImageUploadException("<仅支持上传:json>");
//        }


        String filename = "class/"+"json/"+"知识图谱"+"." + extension;
        String fullUrl = null;

        try {
            fullUrl = aliOssUtil.uploadFile(filename, file.getInputStream());
        } catch (IOException e) {
            throw new ImageUploadException("<Image upload failed>");
        }
        String pathOnly = aliOssUtil.extractPathFromUrl(fullUrl);

        return fullUrl;
    }

    @Override
    public String uploadResource(MultipartFile file,  String message) {
        if(file.isEmpty()){
            throw new FileIsNullException("<File is null>");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.lastIndexOf(".") == -1) {
            throw new ImageUploadException("<文件名无效>");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        Set<String> allowedExtensions = new HashSet<>(Arrays.asList(
                "txt", "md", "markdown", "mdx", "pdf", "html", "htm", "xlsx", "xls",
                "doc", "docx", "csv", "eml", "msg", "pptx", "ppt", "xml", "epub"
        ));

        if (!allowedExtensions.contains(extension)) {
            throw new ImageUploadException("<仅支持上传:TXT, MD, MDX, MARKDOWN, PDF, HTML, XLSX, XLS, DOC, DOCX, CSV,EML, MSG, PPTX, PPT, XML, EPUB>");
        }


        String baseFilename = "class/" + "resource/"+message +"/"+ message;
        String filename = baseFilename + "." + extension;

        // 检查文件是否存在并生成不重复的文件名
        filename = aliOssUtil.generateUniqueFilename(filename);

        String url = null;
        try {
            url = aliOssUtil.uploadFile(filename, file.getInputStream());
        } catch (IOException e) {
            throw new ImageUploadException("<Resource upload failed>");
        }
        return url;

    }

    @Override
    public List<String> getAllByClassId(Long cid) {
        if(classesRepository.findById(cid).isEmpty()){
            throw new ClassesNotFoundException("<Classes not found>");
        }else{
            Classes classes = classesRepository.findById(cid).get();
            List<String> list = aliOssUtil.listFilesInFolder("class/" + classes.getName() + "/" + "resource/");
            for (int i = 0; i < list.size(); i++) {
                if (!list.get(i).startsWith("https://smart-class-northeast")) {
                    list.set(i, "https://smart-class-northeast.oss-cn-beijing.aliyuncs.com/" + list.get(i));
                }
            }
            return list;
        }

    }
}