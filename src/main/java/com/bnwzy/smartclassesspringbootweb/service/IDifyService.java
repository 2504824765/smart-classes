package com.bnwzy.smartclassesspringbootweb.service;

import com.bnwzy.smartclassesspringbootweb.pojo.dto.DifyCreateGraphDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public interface IDifyService {
    Mono<String> createGraph(DifyCreateGraphDTO difyCreateGraphDTO);
}
