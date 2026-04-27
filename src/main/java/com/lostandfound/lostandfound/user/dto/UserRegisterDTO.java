package com.lostandfound.lostandfound.user.dto;

import lombok.Data;

@Data
public class UserRegisterDTO {
    private String studentId;
    private String username;
    private String password;
    private String email;
    private String phone;
}