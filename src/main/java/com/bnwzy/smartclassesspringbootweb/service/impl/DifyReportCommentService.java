package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.pojo.dto.DifyReportCommentDTO;
import com.bnwzy.smartclassesspringbootweb.service.IDifyReportCommentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class DifyReportCommentService implements IDifyReportCommentService {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Autowired
    private OssUploadService ossUploadService;

    public DifyReportCommentService() {
//        String apiKey = "app-569FKczHOtQkaHwEFk0QdEcW"; // 本地
        String apiKey = "app-IyD6i2DIxGDteTT2JoMethKN"; // 远程
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
//                .baseUrl("http://localhost/v1")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Mono<String> reportComment(DifyReportCommentDTO dto) {
        log.info("开始处理Dify请求，支持直接JSON格式和data:前缀格式");

        AtomicReference<File> fileRef = new AtomicReference<>();
        AtomicReference<BufferedWriter> writerRef = new AtomicReference<>();
        AtomicInteger dataCountRef = new AtomicInteger(0);

        try {
            validateRequest(dto);
            String requestBody = objectMapper.writeValueAsString(dto);
            String filename = "dify-response-" + System.currentTimeMillis();

            File tempFile = File.createTempFile(filename, ".json");
            fileRef.set(tempFile);
            log.info("临时文件创建成功: {}", tempFile.getAbsolutePath());

            return webClient.post()
                    .uri("/workflows/run")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> status.isError(), this::handleApiError)
                    .bodyToFlux(String.class)
                    .timeout(Duration.ofMinutes(5))
                    .doOnSubscribe(s -> log.info("开始接收流式数据"))
                    .doOnComplete(() -> log.info("流式数据传输完成，总共接收到 {} 个数据块", dataCountRef.get()))
                    .doOnError(e -> log.error("流式数据传输出错", e))
                    .filter(line -> line != null && !line.trim().isEmpty() && !line.startsWith("event: ping"))
                    .doOnNext(line -> log.info("接收到原始数据流: {}", line))
                    .concatMap(line -> {
                        try {
                            log.info("开始处理数据行: {}", line);
                            String jsonData = line.startsWith("data: ") ? line.substring(6) : line;

                            if (jsonData == null || jsonData.trim().isEmpty()) return Mono.empty();

                            JsonNode rootNode = objectMapper.readTree(jsonData);
                            String event = rootNode.path("event").asText();
                            log.info("JSON解析成功，event类型: {}", event);

                            if ("text_chunk".equals(event)) {
                                JsonNode dataNode = rootNode.path("data");
                                String text = dataNode.path("text").asText();
                                if (text == null || text.trim().isEmpty()) return Mono.empty();

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

                                BufferedWriter writer = writerRef.get();
                                if (writer != null) {
                                    synchronized (writer) {
                                        writer.write(cleanedText);
                                        writer.flush();
                                        int count = dataCountRef.incrementAndGet();
                                        log.info("文本已写入文件: '{}' (第{}个数据块)", cleanedText, count);
                                    }
                                }
                            } else if ("workflow_failed".equals(event)) {
                                log.error("检测到workflow_failed事件");
                            }

                            return Mono.empty();
                        } catch (Exception ex) {
                            log.error("解析JSON失败: {}", line, ex);
                            return Mono.error(ex);
                        }
                    })
                    .then(Mono.defer(() -> {
                        BufferedWriter writer = writerRef.get();
                        if (writer != null) {
                            try {
                                writer.close();
                            } catch (IOException e) {
                                log.warn("关闭Writer失败", e);
                            }
                        }

                        File outputFile = fileRef.get();
                        if (outputFile == null || !outputFile.exists()) {
                            throw new IllegalStateException("临时文件错误");
                        }

                        MultipartFile multipartFile = null;
                        try {
                            multipartFile = convertToMultipartFile(outputFile);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        return uploadFile(multipartFile)
                                .doFinally(signal -> {
                                    if (outputFile.exists() && outputFile.delete()) {
                                        log.info("临时文件已删除: {}", outputFile.getAbsolutePath());
                                    }
                                });
                    }))
                    .doOnSubscribe(s -> {
                        try {
                            BufferedWriter writer = new BufferedWriter(new FileWriter(fileRef.get()));
                            writerRef.set(writer);
                        } catch (IOException e) {
                            throw new RuntimeException("创建Writer失败", e);
                        }
                    })
                    .doOnError(e -> cleanupResources(writerRef, fileRef));
        } catch (Exception e) {
            log.error("初始化失败", e);
            cleanupResources(writerRef, fileRef);
            return Mono.error(e);
        }
    }

    private void validateRequest(DifyReportCommentDTO dto) {
        if (dto.getInputs() == null) {
            throw new IllegalArgumentException("inputs 不能为空");
        }

        DifyReportCommentDTO.Inputs inputs = dto.getInputs();
        DifyReportCommentDTO.FileInput studentReportFile = inputs.getStudentReport();
        DifyReportCommentDTO.FileInput moBanFile = inputs.getMoBan();

        if (studentReportFile == null || moBanFile == null) {
            throw new IllegalArgumentException("文件不能为空");
        }

        if (studentReportFile.getType() == null) throw new IllegalArgumentException("type不能为空");
        if (studentReportFile.getTransfer_method() == null) throw new IllegalArgumentException("transfer_method不能为空");
        if ("remote_url".equals(studentReportFile.getTransfer_method()) && studentReportFile.getUrl() == null)
            throw new IllegalArgumentException("remote_url方式必须提供url");
        if ("local_file".equals(studentReportFile.getTransfer_method()) && studentReportFile.getUpload_file_id() == null)
            throw new IllegalArgumentException("local_file方式必须提供upload_file_id");

        if (moBanFile.getType() == null) throw new IllegalArgumentException("type不能为空");
        if (moBanFile.getTransfer_method() == null) throw new IllegalArgumentException("transfer_method不能为空");
        if ("remote_url".equals(moBanFile.getTransfer_method()) && moBanFile.getUrl() == null)
            throw new IllegalArgumentException("remote_url方式必须提供url");
        if ("local_file".equals(moBanFile.getTransfer_method()) && moBanFile.getUpload_file_id() == null)
            throw new IllegalArgumentException("local_file方式必须提供upload_file_id");

    }

    private Mono<Throwable> handleApiError(ClientResponse response) {
        return response.bodyToMono(String.class)
                .defaultIfEmpty("[空响应体]")
                .flatMap(body -> {
                    HttpStatus status = (HttpStatus) response.statusCode();
                    log.error("API返回错误: 状态码={}，响应体={}", status, body);
                    return Mono.error(new RuntimeException("API调用失败: " + status + " - " + body));
                });
    }

    private void cleanupResources(AtomicReference<BufferedWriter> writerRef, AtomicReference<File> fileRef) {
        try {
            BufferedWriter writer = writerRef.get();
            if (writer != null) writer.close();
        } catch (IOException e) {
            log.warn("关闭Writer失败", e);
        }

        File file = fileRef.get();
        if (file != null && file.exists() && !file.delete()) {
            log.warn("未能删除临时文件: {}", file.getAbsolutePath());
        }
    }

    private MultipartFile convertToMultipartFile(File file) throws IOException {
        byte[] fileContent = Files.readAllBytes(file.toPath());
        return new MockMultipartFile(
                "file",
                file.getName(),
                "application/json",
                fileContent
        );
    }

    public Mono<String> generateAndUploadTxt(String content, String baseFilename) {
        try {
            File txtFile = createTextFile(content, baseFilename);
            MultipartFile multipartFile = convertToMultipartFile(txtFile);
            return uploadFile(multipartFile)
                    .doFinally(signal -> {
                        if (txtFile.exists() && !txtFile.delete()) {
                            log.warn("未能删除TXT临时文件: {}", txtFile.getAbsolutePath());
                        }
                    });
        } catch (IOException e) {
            return Mono.error(e);
        }
    }

    private File createTextFile(String content, String filename) throws IOException {
        File file = File.createTempFile(filename, ".txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
        return file;
    }

    private Mono<String> uploadFile(MultipartFile file) {
        try {
            String response = ossUploadService.uploadGraph(file);
            return Mono.just(response);
        } catch (Exception e) {
            return Mono.error(new RuntimeException("上传失败: " + e.getMessage(), e));
        }
    }
}
