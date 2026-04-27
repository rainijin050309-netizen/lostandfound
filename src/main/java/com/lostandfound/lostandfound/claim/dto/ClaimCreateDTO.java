package com.lostandfound.lostandfound.claim.dto;

import lombok.Data;

@Data
public class ClaimCreateDTO {
    private Long itemId;
    private Long userId;
    private String message;
}