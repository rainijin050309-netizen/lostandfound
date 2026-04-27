package com.lostandfound.lostandfound.claim.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClaimVO {
    private Long id;
    private Long itemId;
    private String itemTitle;
    private Long userId;
    private String username;
    private String message;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
