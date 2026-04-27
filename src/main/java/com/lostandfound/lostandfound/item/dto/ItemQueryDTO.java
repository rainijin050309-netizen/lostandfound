package com.lostandfound.lostandfound.item.dto;

import lombok.Data;

@Data
public class ItemQueryDTO {
    private String type;
    private String category;
    private String status;
    private String keyword;
    private Integer page;
    private Integer size;
}
