package com.bnwzy.smartclassesspringbootweb.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface IOssUploadService {
    String uploadResource(MultipartFile file, Long id, String message);

    String uploadImage(MultipartFile file, Long id);

    String uploadGraph(MultipartFile file, Long id);

    List<String> getAllByClassId(Long cid);
}
