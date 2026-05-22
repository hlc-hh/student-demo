package com.czjt.controller;

import com.czjt.pojo.ChatHistory;
import com.czjt.pojo.ChatRequest;
import com.czjt.pojo.Result;
import com.czjt.service.CustomerServiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RestController
@RequestMapping("/ai")
@CrossOrigin(origins = "*")
public class CustomerServiceController {

    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Autowired
    private CustomerServiceService customerServiceService;

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(@RequestBody ChatRequest request) {
        SseEmitter emitter = new SseEmitter(120000L);

        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            try {
                emitter.send(SseEmitter.event().name("error").data("消息不能为空"));
                emitter.complete();
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
            return emitter;
        }

        if (request.getSessionId() == null || request.getSessionId().trim().isEmpty()) {
            request.setSessionId(UUID.randomUUID().toString());
        }

        final String sessionId = request.getSessionId();
        final Long userId = request.getUserId();

        executor.execute(() -> {
            try {
                // 发送会话ID
                emitter.send(SseEmitter.event()
                        .name("session")
                        .data(sessionId));

                // 流式处理
                customerServiceService.chatStream(
                        request.getMessage(),
                        sessionId,
                        userId,
                        chunk -> {
                            try {
                                emitter.send(SseEmitter.event()
                                        .name("message")
                                        .data(chunk));
                            } catch (IOException e) {
                                log.error("发送SSE消息失败", e);
                                throw new RuntimeException(e);
                            }
                        }
                );

                // 发送结束标志
                emitter.send(SseEmitter.event()
                        .name("end")
                        .data("[DONE]"));
                emitter.complete();

            } catch (Exception e) {
                log.error("AI客服处理消息失败", e);
                try {
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data("抱歉，我暂时遇到了一些问题，请稍后再试。"));
                } catch (IOException ex) {
                    log.error("发送错误消息失败", ex);
                }
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    @GetMapping("/history")
    public Result<List<ChatHistory>> getHistory(@RequestParam String sessionId) {
        List<ChatHistory> history = customerServiceService.getChatHistory(sessionId);
        return Result.success(history);
    }
}