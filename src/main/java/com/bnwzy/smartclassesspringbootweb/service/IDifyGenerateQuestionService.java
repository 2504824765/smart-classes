package com.bnwzy.smartclassesspringbootweb.service;

import com.bnwzy.smartclassesspringbootweb.pojo.dto.DifyGenerateQuestionDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface IDifyGenerateQuestionService {
    Mono<String> generateQuestion(DifyGenerateQuestionDTO difyGenerateQuestionDTO);
}
