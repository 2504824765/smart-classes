package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.StudentNotFoundException;
import com.bnwzy.smartclassesspringbootweb.pojo.Student;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.DifyStudentAbilityAnalyzeDTO;
import com.bnwzy.smartclassesspringbootweb.repository.StudentDataRepository;
import com.bnwzy.smartclassesspringbootweb.repository.StudentRepository;
import com.bnwzy.smartclassesspringbootweb.service.IDifyStudentAbilityAnalyzeService;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class DifyStudentAbilityAnalyzeService implements IDifyStudentAbilityAnalyzeService {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private OssUploadService ossUploadService;
    @Autowired
    private StudentDataRepository studentDataRepository;

    public DifyStudentAbilityAnalyzeService() {
//        String apiKey = "app-6rYPAA60WQdzgfucg9QIM2FR"; // 本地
        String apiKey = "app-XWHo2AUJA4pKIoNX3yTLTgQq"; // 远程
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(300))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 100000);

        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build();

        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(exchangeStrategies)
//                .baseUrl("http://localhost/v1")
                .baseUrl("https://api.dify.ai/v1")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Mono<String> studentAbilityAnalyze(DifyStudentAbilityAnalyzeDTO dto) {
        if (dto.getStudentId() == null) {
            throw new IllegalArgumentException("studentId<UNK>");
        }
        log.info("开始处理Dify请求，支持直接JSON格式和data:前缀格式");

        StringBuilder jsonBuilder = new StringBuilder();
        AtomicInteger dataCountRef = new AtomicInteger(0);

        try {
            validateRequest(dto.getUpload());
            String requestBody = objectMapper.writeValueAsString(dto.getUpload());

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
                                
                                jsonBuilder.append(cleanedText);
                                int count = dataCountRef.incrementAndGet();
                                log.info("文本已追加到StringBuilder: '{}' (第{}个数据块)", cleanedText, count);
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
                        // 处理拼接后的完整JSON字符串
                        try {
                            String jsonString = jsonBuilder.toString();
                            String cleanJson = jsonString.replaceAll("(?i)```json|```", "").trim();
                            System.out.println(cleanJson);
                            com.bnwzy.smartclassesspringbootweb.pojo.StudentData studentData = objectMapper.readValue(cleanJson, com.bnwzy.smartclassesspringbootweb.pojo.StudentData.class);
                            log.info("成功将JSON字符串转为StudentData对象: {}", studentData);
                            if (studentRepository.findById(dto.getStudentId()).isEmpty()) {
                                throw new StudentNotFoundException("<Student not found>");
                            } else {
                                Student student = studentRepository.findById(dto.getStudentId()).get();
                                studentDataRepository.save(studentData);
                                student.setStudentData(studentData);
                                studentRepository.save(student);
                                return Mono.just("success");
                            }
                        } catch (Exception e) {
                            log.error("JSON转StudentData对象失败", e);
                            return Mono.error(e);
                        }
                    }));
        } catch (Exception e) {
            log.error("初始化失败", e);
            return Mono.error(e);
        }
    }

    private void validateRequest(DifyStudentAbilityAnalyzeDTO.Upload dto) {
        if (dto.getInputs() == null) {
            throw new IllegalArgumentException("inputs 不能为空");
        }

        DifyStudentAbilityAnalyzeDTO.Inputs inputs = dto.getInputs();
        DifyStudentAbilityAnalyzeDTO.FileInput studentAbilityFile = inputs.getStudentAbility();
        DifyStudentAbilityAnalyzeDTO.FileInput studentReportFile = inputs.getStudentReport();

        if (studentReportFile == null) {
            throw new IllegalArgumentException("studentReportFile不能为空");
        } else {
            if (studentReportFile.getType() == null) throw new IllegalArgumentException("type不能为空");
            if (studentReportFile.getTransfer_method() == null) throw new IllegalArgumentException("transfer_method不能为空");
            if ("remote_url".equals(studentReportFile.getTransfer_method()) && studentReportFile.getUrl() == null)
                throw new IllegalArgumentException("remote_url方式必须提供url");
            if ("local_file".equals(studentReportFile.getTransfer_method()) && studentReportFile.getUpload_file_id() == null)
                throw new IllegalArgumentException("local_file方式必须提供upload_file_id");
        }

        if (studentAbilityFile != null) {
            if (studentAbilityFile.getType() == null) throw new IllegalArgumentException("type不能为空");
            if (studentAbilityFile.getTransfer_method() == null) throw new IllegalArgumentException("transfer_method不能为空");
            if ("remote_url".equals(studentAbilityFile.getTransfer_method()) && studentAbilityFile.getUrl() == null)
                throw new IllegalArgumentException("remote_url方式必须提供url");
            if ("local_file".equals(studentReportFile.getTransfer_method()) && studentAbilityFile.getUpload_file_id() == null)
                throw new IllegalArgumentException("local_file方式必须提供upload_file_id");
        }
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
