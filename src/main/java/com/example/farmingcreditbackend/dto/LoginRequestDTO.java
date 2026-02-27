package com.example.farmingcreditbackend.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
/**
 * 登录请求DTO
 */
@Data
public class LoginRequestDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank
    private String captchaKey;  // 验证码UUID

    @NotBlank(message = "验证码不能为空")
    private String captchaCode; // 用户输入的验证码

    private String role; // farmer, store_owner, admin

}