package com.example.farmingcreditbackend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterResponseDTO {
    private Long userId;
    private String username;
    private String realName;
    private String userType;
    private String message;
}