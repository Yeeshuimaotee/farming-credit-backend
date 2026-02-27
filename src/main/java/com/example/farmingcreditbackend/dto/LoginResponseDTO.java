package com.example.farmingcreditbackend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 登录响应DTO
 */
@Data
@Builder
public class LoginResponseDTO {
    private Long userId;
    private String username;
    private String realName;
    private String userType;
    private String token;
    private LocalDateTime loginTime;
    private List<String> roles;
    private List<String> permissions;
}