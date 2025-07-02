package com.bnwzy.smartclassesspringbootweb.service;

import com.bnwzy.smartclassesspringbootweb.pojo.dto.DifyReportCommentDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface IDifyReportCommentService {
    Mono<String> reportComment(DifyReportCommentDTO difyReportCommentDTO);
}
