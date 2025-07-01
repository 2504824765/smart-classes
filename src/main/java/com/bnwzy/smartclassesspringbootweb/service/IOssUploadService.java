package com.bnwzy.smartclassesspringbootweb.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface IOssUploadService {
    String uploadResource(MultipartFile file, String message);

    String uploadImage(MultipartFile file);

    String uploadGraph(MultipartFile file);

    List<String> getAllByClassId(Long cid);
}
