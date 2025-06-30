package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.pojo.dto.DifyCreateGraphDTO;
import com.bnwzy.smartclassesspringbootweb.service.IDifyService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class DifyService implements IDifyService {
    @Override
    public Mono<String> createGraph(DifyCreateGraphDTO difyCreateGraphDTO) {
        String apiKey = "app-SfTCiHTX3aqjHm9GL1e9ULxE";
        final WebClient webClient;
        String baseUrl = "https://api.dify.ai/v1";

        webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();

        String requestBody = "{\"inputs\": {}, \"response_mode\": \"blocking\", \"user\": \"abc-123\"}";
        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class);
    }
}
