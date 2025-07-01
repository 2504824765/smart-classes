package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.pojo.dto.DifyCreateGraphDTO;
import com.bnwzy.smartclassesspringbootweb.service.IDifyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DifyService implements IDifyService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public DifyService() {
        String apiKey = "app-SfTCiHTX3aqjHm9GL1e9ULxE";
        this.webClient = WebClient.builder()
                .baseUrl("https://api.dify.ai/v1")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Mono<String> createGraph(DifyCreateGraphDTO dto) {
        try {
            // 1. 参数校验
            validateRequest(dto);

            // 2. 打印调试信息
            log.debug("Request DTO: {}", dto);
            String requestBody = objectMapper.writeValueAsString(dto);
            log.debug("Request JSON: {}", requestBody);

            // 3. 发送请求
            return webClient.post()
                    .uri("/workflows/run")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(
                            status -> status.isError(),
                            response -> handleApiError(response)
                    )
                    .bodyToMono(String.class)
                    .doOnSuccess(response -> log.info("API响应成功: {}", response))
                    .doOnError(e -> log.error("API调用异常", e));

        } catch (Exception e) {
            log.error("请求构建失败", e);
            return Mono.error(e);
        }
    }

    // 参数校验逻辑
    private void validateRequest(DifyCreateGraphDTO dto) {
        if (dto.getInputs() == null || dto.getInputs().isEmpty()) {
            throw new IllegalArgumentException("inputs不能为空");
        }
        System.out.println(dto);

        // 检查每个文件变量是否合规
        for (Map.Entry<String, List<DifyCreateGraphDTO.FileInput>> entry : dto.getInputs().entrySet()) {
            List<DifyCreateGraphDTO.FileInput> files = entry.getValue();
            if (files == null || files.isEmpty()) {
                throw new IllegalArgumentException("文件列表不能为空");
            }

            for (DifyCreateGraphDTO.FileInput file : files) {
                if (file.getType() == null) {
                    throw new IllegalArgumentException("文件类型(type)不能为空");
                }
                if (file.getTransfer_method() == null) {
                    throw new IllegalArgumentException("传输方式(transfer_method)不能为空");
                }
                if ("remote_url".equals(file.getTransfer_method()) && file.getUrl() == null) {
                    throw new IllegalArgumentException("remote_url方式必须提供url");
                }
                if ("local_file".equals(file.getTransfer_method()) && file.getUpload_file_id() == null) {
                    throw new IllegalArgumentException("local_file方式必须提供upload_file_id");
                }
            }
        }
    }

    // 统一错误处理
    private Mono<Throwable> handleApiError(ClientResponse response) {
        return response.bodyToMono(String.class)
                .flatMap(errorBody -> {
                    log.error("API返回错误: HTTP {} - {}", response.statusCode(), errorBody);
                    if (response.statusCode() == HttpStatus.BAD_REQUEST) {
                        return Mono.error(new IllegalArgumentException("API参数错误: " + errorBody));
                    } else {
                        return Mono.error(new RuntimeException("API调用失败: " + errorBody));
                    }
                });
    }
}