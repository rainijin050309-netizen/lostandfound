package com.lostandfound.lostandfound.item.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ItemCreateDTO {
    private Long userId;
    private String type;
    private String title;
    private String description;
    private String category;
    private String location;
    private LocalDate lostDate;
    private LocalDate foundDate;
    private String imageUrl;
}
