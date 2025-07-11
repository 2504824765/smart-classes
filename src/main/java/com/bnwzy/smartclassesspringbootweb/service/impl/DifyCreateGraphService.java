package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.pojo.dto.DifyCreateGraphDTO;
import com.bnwzy.smartclassesspringbootweb.service.IDifyCreateGraphService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class DifyCreateGraphService implements IDifyCreateGraphService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    @Autowired
    private OssUploadService ossUploadService;

    public DifyCreateGraphService() {
        String apiKey = "app-SfTCiHTX3aqjHm9GL1e9ULxE";
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(300))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 100000);

        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build();

        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(exchangeStrategies)
                .baseUrl("https://api.dify.ai/v1")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Mono<String> createGraph(DifyCreateGraphDTO dto) {
        log.info("开始处理Dify请求，支持直接JSON格式和data:前缀格式");
        
        AtomicReference<File> fileRef = new AtomicReference<>();
        AtomicReference<BufferedWriter> writerRef = new AtomicReference<>();
        AtomicInteger dataCountRef = new AtomicInteger(0); // 添加数据计数器

        try {
            validateRequest(dto);
            String requestBody = objectMapper.writeValueAsString(dto);
            String filename = "dify-response-" + System.currentTimeMillis();

            // 初始化文件引用
            File tempFile = File.createTempFile(filename, ".json");
            fileRef.set(tempFile);
            log.info("临时文件创建成功: {}", tempFile.getAbsolutePath());

            log.info("准备发送请求到Dify API，请求体: {}", requestBody);

            return webClient.post()
                    .uri("/workflows/run")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> status.isError(), this::handleApiError)
                    .bodyToFlux(String.class)
                    .timeout(Duration.ofMinutes(5)) // 添加5分钟超时
                    .doOnSubscribe(s -> {
                        log.info("开始接收流式数据");
                    })
                    .doOnComplete(() -> log.info("流式数据传输完成，总共接收到 {} 个数据块", dataCountRef.get()))
                    .doOnError(e -> log.error("流式数据传输出错", e))
                    .filter(line -> line != null && !line.trim().isEmpty() && !line.startsWith("event: ping"))
                    .doOnNext(line -> log.info("接收到原始数据流: {}", line)) // 添加原始数据流日志
                    .concatMap(line -> {
                        try {
                            log.info("开始处理数据行: {}", line);
                            
                            String jsonData = null;
                            
                            // 检查是否以"data: "开头
                            if (line.startsWith("data: ")) {
                                log.info("检测到data前缀，开始解析JSON");
                                jsonData = line.substring(6);
                            } else if (line.trim().startsWith("{")) {
                                // 直接是JSON格式
                                log.info("检测到直接JSON格式");
                                jsonData = line;
                            } else {
                                log.info("未知数据格式，跳过: {}", line);
                                return Mono.empty();
                            }
                            
                            log.info("JSON数据: {}", jsonData);
                            
                            // 验证JSON格式
                            if (jsonData == null || jsonData.trim().isEmpty()) {
                                log.warn("JSON数据为空，跳过处理");
                                return Mono.empty();
                            }
                            
                            JsonNode rootNode = objectMapper.readTree(jsonData);
                            log.info("JSON解析成功，event类型: {}", rootNode.path("event").asText());
                            
                            if ("text_chunk".equals(rootNode.path("event").asText())) {
                                log.info("检测到text_chunk事件");
                                JsonNode dataNode = rootNode.path("data");
                                if (!dataNode.isMissingNode()) {
                                    String text = dataNode.path("text").asText();
                                    log.info("提取到文本内容: '{}'", text);
                                    
                                    // 检查文本是否为空或只包含空白字符
                                    if (text == null || text.trim().isEmpty()) {
                                        log.info("文本内容为空或只包含空白字符，跳过写入");
                                        return Mono.empty();
                                    }
                                    
                                    // 去掉JSON格式字符串开头的```json和结尾的```
                                    String cleanedText = text;
                                    if (cleanedText.startsWith("```json")) {
                                        cleanedText = cleanedText.substring(7);
                                    }
                                    if (cleanedText.endsWith("```")) {
                                        cleanedText = cleanedText.substring(0, cleanedText.length() - 3);
                                    }
                                    // 去掉开头和结尾的空白字符
                                    cleanedText = cleanedText.trim();
                                    
                                    // 确保writer存在
                                    BufferedWriter writer = writerRef.get();
                                    if (writer != null) {
                                        synchronized (writer) {
                                            writer.write(cleanedText);
                                            writer.flush(); // 确保数据立即写入
                                            int count = dataCountRef.incrementAndGet();
                                            log.info("文本已写入文件: '{}' (第{}个数据块)", cleanedText, count);
                                        }
                                    } else {
                                        log.error("Writer为null，无法写入数据");
                                    }
                                } else {
                                    log.warn("data节点缺失或为空");
                                }
                            } else if ("workflow_started".equals(rootNode.path("event").asText())) {
                                log.info("检测到workflow_started事件");
                            } else if ("workflow_finished".equals(rootNode.path("event").asText())) {
                                log.info("检测到workflow_finished事件");
                            } else if ("workflow_failed".equals(rootNode.path("event").asText())) {
                                log.error("检测到workflow_failed事件");
                            } else {
                                log.info("其他事件类型: {}", rootNode.path("event").asText());
                            }
                            return Mono.empty();
                        } catch (Exception ex) {
                            log.error("解析JSON失败: {}", line, ex);
                            return Mono.error(ex);
                        }
                    })
                    .then(Mono.defer(() -> {
                        // 确保writer已关闭
                        BufferedWriter writer = writerRef.get();
                        if (writer != null) {
                            try {
                                writer.close();
                                log.info("Writer已关闭");
                            } catch (IOException e) {
                                log.warn("关闭Writer失败", e);
                            }
                        }

                        // 获取文件引用
                        File outputFile = fileRef.get();
                        if (outputFile == null) {
                            throw new IllegalStateException("临时文件引用丢失");
                        }
                        
                        if (!outputFile.exists()) {
                            throw new IllegalStateException("临时文件已被删除: " + outputFile.getAbsolutePath());
                        }

                        // 检查文件大小
                        if (outputFile.length() == 0) {
                            log.warn("生成的文件为空，可能没有接收到有效数据");
                        }

                        log.info("准备上传文件: {}, 大小: {} bytes", outputFile.getAbsolutePath(), outputFile.length());

                        MultipartFile multipartFile = null;
                        try {
                            multipartFile = convertToMultipartFile(outputFile);
                        } catch (IOException e) {
                            throw new RuntimeException("转换文件失败", e);
                        }
                        
                        return uploadFile(multipartFile)
                                .doFinally(signalType -> {
                                    // 上传完成后删除临时文件
                                    if (outputFile.exists() && outputFile.delete()) {
                                        log.info("临时文件已删除: {}", outputFile.getAbsolutePath());
                                    } else {
                                        log.warn("未能删除临时文件: {}", outputFile.getAbsolutePath());
                                    }
                                });
                    }))
                    .doOnSubscribe(s -> {
                        // 在订阅时创建writer
                        try {
                            BufferedWriter writer = new BufferedWriter(new FileWriter(fileRef.get()));
                            writerRef.set(writer);
                            log.info("Writer已创建");
                        } catch (IOException e) {
                            log.error("创建Writer失败", e);
                            throw new RuntimeException(e);
                        }
                    })
                    .doOnError(e -> {
                        log.error("处理失败", e);
                        // 清理资源
                        cleanupResources(writerRef, fileRef);
                    });
        } catch (Exception e) {
            log.error("初始化失败", e);
            // 清理临时文件（如果创建后发生异常）
            cleanupResources(writerRef, fileRef);
            return Mono.error(e);
        }
    }

    /**
     * 清理资源的辅助方法
     */
    private void cleanupResources(AtomicReference<BufferedWriter> writerRef, AtomicReference<File> fileRef) {
        // 关闭writer
        BufferedWriter writer = writerRef.get();
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                log.warn("关闭Writer失败", e);
            }
        }

        // 删除临时文件
        File file = fileRef.get();
        if (file != null && file.exists()) {
            if (file.delete()) {
                log.info("临时文件已删除: {}", file.getAbsolutePath());
            } else {
                log.warn("未能删除临时文件: {}", file.getAbsolutePath());
            }
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
                .defaultIfEmpty("[空响应体]")
                .flatMap(errorBody -> {
                    HttpStatus status = (HttpStatus) response.statusCode();
                    log.error("API返回错误: 状态码={}，响应体={}", status, errorBody);

                    if (status == HttpStatus.GATEWAY_TIMEOUT) {
                        return Mono.error(new RuntimeException("504 网关超时：Dify 接口长时间无响应"));
                    } else if (status == HttpStatus.BAD_REQUEST) {
                        return Mono.error(new IllegalArgumentException("API参数错误: " + errorBody));
                    } else {
                        return Mono.error(new RuntimeException("API调用失败: HTTP " + status + " - " + errorBody));
                    }
                });
    }


    /**
     * 将字符串内容保存为TXT文件
     * @param content 文件内容
     * @param filename 文件名(不带扩展名)
     * @return 生成的File对象
     */
    private File createTextFile(String content, String filename) throws IOException, IOException {
        File file = File.createTempFile(filename, ".txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
        log.info("TXT文件已生成: {}", file.getAbsolutePath());
        return file;
    }

    /**
     * 将File转换为MultipartFile
     */
    private MultipartFile convertToMultipartFile(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("文件不能为null");
        }
        
        if (!file.exists()) {
            throw new IllegalArgumentException("文件不存在: " + file.getAbsolutePath());
        }
        
        if (file.length() == 0) {
            log.warn("文件为空: {}", file.getAbsolutePath());
        }
        
        log.info("开始转换文件: {}, 大小: {} bytes", file.getAbsolutePath(), file.length());
        
        try {
            FileSystemResource resource = new FileSystemResource(file);
            byte[] fileContent = Files.readAllBytes(file.toPath());
            
            MultipartFile multipartFile = new MockMultipartFile(
                    "file",
                    file.getName(),
                    "application/json", // 改为application/json，因为生成的是JSON文件
                    fileContent
            );
            
            log.info("文件转换成功: {}, 内容大小: {} bytes", file.getName(), fileContent.length);
            return multipartFile;
        } catch (IOException e) {
            log.error("转换文件失败: {}", file.getAbsolutePath(), e);
            throw new IOException("转换文件失败: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * 生成TXT文件并上传
     * @param content 文件内容
     * @param baseFilename 基础文件名(不带扩展名)
     * @return 上传结果
     */
    public Mono<String> generateAndUploadTxt(String content, String baseFilename) {
        try {
            // 1. 生成TXT文件
            File txtFile = createTextFile(content, baseFilename);

            // 2. 转换为MultipartFile
            MultipartFile multipartFile = convertToMultipartFile(txtFile);

            // 3. 调用上传逻辑 - 这里需要你实现具体的上传方法
            // 假设你有一个uploadFile方法
            return uploadFile(multipartFile)
                    .doFinally(signalType -> {
                        // 4. 上传完成后删除临时文件
                        if (txtFile.delete()) {
                            log.info("临时文件已删除: {}", txtFile.getAbsolutePath());
                        } else {
                            log.warn("未能删除临时文件: {}", txtFile.getAbsolutePath());
                        }
                    });

        } catch (IOException e) {
            log.error("生成或上传TXT文件失败", e);
            return Mono.error(e);
        }
    }

    // 你需要实现这个上传方法
    private Mono<String> uploadFile(MultipartFile file) {
        if (file == null) {
            return Mono.error(new IllegalArgumentException("上传文件不能为null"));
        }
        
        if (file.isEmpty()) {
            return Mono.error(new IllegalArgumentException("上传文件为空"));
        }
        
        log.info("开始上传文件: {}, 大小: {} bytes", file.getOriginalFilename(), file.getSize());
        
        try {
            String responseMessage = ossUploadService.uploadGraph(file);
            log.info("文件上传成功: {}, 返回结果: {}", file.getOriginalFilename(), responseMessage);
            return Mono.just(responseMessage);
        } catch (Exception e) {
            log.error("文件上传失败: {}", file.getOriginalFilename(), e);
            return Mono.error(new RuntimeException("文件上传失败: " + file.getOriginalFilename(), e));
        }
    }
}