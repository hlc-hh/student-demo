package com.czjt.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatHistory {
    private Long id;
    private String sessionId;
    private Long userId;
    private String message;
    private String response;
    private LocalDateTime createdAt;
}
