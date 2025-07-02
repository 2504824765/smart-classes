package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.pojo.dto.DifyCreateGraphDTO;
import com.bnwzy.smartclassesspringbootweb.service.IDifyService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
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
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
// TODO: 将智能体获得的字符串变成文件上传至OSS
public class DifyService implements IDifyService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    @Autowired
    private OssUploadService ossUploadService;

    public DifyService() {
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

    // TODO: outputFile是null，这到底是为什么？！！！
    @Override
    public Mono<String> createGraph(DifyCreateGraphDTO dto) {
        AtomicReference<File> fileRef = new AtomicReference<>();

        try {
            validateRequest(dto);
            String requestBody = objectMapper.writeValueAsString(dto);
            String filename = "dify-response-" + System.currentTimeMillis();

            // 初始化文件引用
            fileRef.set(File.createTempFile(filename, ".json"));
            log.info("临时文件创建成功: {}", fileRef.get().getAbsolutePath());

            return Mono.using(
                    () -> new BufferedWriter(new FileWriter(fileRef.get())),
                    writer -> webClient.post()
                            .uri("/workflows/run")
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(requestBody)
                            .retrieve()
                            .onStatus(status -> status.isError(), this::handleApiError)
                            .bodyToFlux(String.class)
                            .filter(line -> line != null && !line.trim().isEmpty() && !line.startsWith("event: ping"))
                            .concatMap(line -> {
                                try {
                                    if (line.startsWith("data: ")) {
                                        JsonNode rootNode = objectMapper.readTree(line.substring(6));
                                        if ("text_chunk".equals(rootNode.path("event").asText())) {
                                            JsonNode dataNode = rootNode.path("data");
                                            if (!dataNode.isMissingNode()) {
                                                String text = dataNode.path("text").asText();
                                                synchronized (writer) {
                                                    writer.write(text);
                                                }
                                            }
                                        }
                                    }
                                    return Mono.empty();
                                } catch (Exception ex) {
                                    log.error("解析JSON失败: {}", line, ex);
                                    return Mono.error(ex);
                                }
                            })
                            .then(Mono.defer(() -> {
                                File outputFile = fileRef.get();
                                Objects.requireNonNull(outputFile, "临时文件引用丢失");
                                if (!outputFile.exists()) {
                                    throw new IllegalStateException("临时文件已被删除: " + outputFile.getAbsolutePath());
                                }
                                MultipartFile multipartFile = null;
                                try {
                                    multipartFile = convertToMultipartFile(outputFile);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                return uploadFile(multipartFile);
                            })),
                    writer -> {
                        try {
                            writer.close();  // 仅关闭Writer，文件在 uploadFile 完成后删除
                        } catch (IOException e) {
                            log.warn("关闭Writer失败", e);
                        }
                    }
            ).doOnError(e -> log.error("处理失败", e));
        } catch (Exception e) {
            // 清理临时文件（如果创建后发生异常）
            if (fileRef.get() != null && fileRef.get().exists()) {
                fileRef.get().delete();
            }
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
        FileSystemResource resource = new FileSystemResource(file);
        return new MockMultipartFile(
                "file",
                file.getName(),
                "text/plain",
                Files.readAllBytes(file.toPath())
        );
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
        String responseMessage = ossUploadService.uploadGraph(file);
        return Mono.just(responseMessage);
    }
}