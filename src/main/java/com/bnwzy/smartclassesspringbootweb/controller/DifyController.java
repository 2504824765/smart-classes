package com.bnwzy.smartclassesspringbootweb.controller;

import com.bnwzy.smartclassesspringbootweb.pojo.ResponseMessage;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.DifyCreateGraphDTO;
import com.bnwzy.smartclassesspringbootweb.service.IDifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/dify")
public class DifyController {

    @Autowired
    private IDifyService difyService;

    /**
     * 创建工作流（同步阻塞方式）
     * @param difyCreateGraphDTO 包含：
     *   - inputs: Map<String, List<FileInput>> (必须)
     *     - FileInput 需包含: type, transfer_method, url/upload_file_id
     *   - response_mode: "blocking"|"streaming" (必须)
     *   - user: 用户标识 (必须)
     */
    @PostMapping("/createGraph")
    public ResponseEntity<ResponseMessage> createGraphSync(
            @Validated @RequestBody DifyCreateGraphDTO difyCreateGraphDTO) {
        try {
            log.info("收到创建请求: {}", difyCreateGraphDTO);
            String result = difyService.createGraph(difyCreateGraphDTO).block();
            return ResponseEntity.ok(ResponseMessage.success("工作流创建成功", result));
        } catch (IllegalArgumentException e) {
            log.warn("参数校验失败: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseMessage.fail(400, "参数错误: " + e.getMessage()));
        } catch (Exception e) {
            log.error("API调用异常", e);
            return ResponseEntity.internalServerError()
                    .body(ResponseMessage.fail(500, "服务处理失败"));
        }
    }

    /**
     * 创建工作流（响应式非阻塞方式 - 推荐）
     */
    @PostMapping("/createGraphAsync")
    public Mono<ResponseEntity<ResponseMessage>> createGraphAsync(
            @Validated @RequestBody DifyCreateGraphDTO difyCreateGraphDTO) {
        return difyService.createGraph(difyCreateGraphDTO)
                .map(result -> ResponseEntity.ok(
                        ResponseMessage.success("工作流创建成功", result)
                ))
                .onErrorResume(IllegalArgumentException.class, e -> {
                    log.warn("参数校验失败: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest()
                            .body(ResponseMessage.fail(400, e.getMessage())));
                })
                .onErrorResume(Exception.class, e -> {
                    log.error("API调用异常", e);
                    return Mono.just(ResponseEntity.internalServerError()
                            .body(ResponseMessage.fail(500, "服务处理失败")));
                });
    }
}