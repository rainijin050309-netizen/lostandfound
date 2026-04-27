package com.lostandfound.lostandfound.item.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ItemVO {
    private Long id;
    private Long userId;
    private String username;
    private String type;
    private String title;
    private String description;
    private String category;
    private String location;
    private LocalDate lostDate;
    private LocalDate foundDate;
    private String imageUrl;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
