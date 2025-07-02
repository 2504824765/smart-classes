package com.bnwzy.smartclassesspringbootweb.service;

import com.bnwzy.smartclassesspringbootweb.pojo.dto.DifyCreateGraphDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface IDifyCreateGraphService {
    Mono<String> createGraph(DifyCreateGraphDTO difyCreateGraphDTO);
}