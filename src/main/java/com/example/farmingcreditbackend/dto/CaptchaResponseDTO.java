package com.example.farmingcreditbackend.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 验证码响应DTO
 */
@Data
@Builder
public class CaptchaResponseDTO {
    private String captchaCode;
    private String captchaImage;
    private Long expireTime;
}