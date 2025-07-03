package com.bnwzy.smartclassesspringbootweb.service;

import com.bnwzy.smartclassesspringbootweb.pojo.dto.DifyReportCommentDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.DifyStudentAbilityAnalyzeDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface IDifyStudentAbilityAnalyzeService {
    Mono<String> studentAbilityAnalyze(DifyStudentAbilityAnalyzeDTO difyStudentAbilityAnalyzeDTO);
}
