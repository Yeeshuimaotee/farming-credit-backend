package com.example.farmingcreditbackend.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class RegisterRequestDTO {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度必须在2-20之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 16, message = "密码长度必须在8-16之间")
    private String password;

    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    @NotBlank(message = "手机号不能为空")
    @Size(min = 11, max = 11, message = "手机号必须为11位")
    private String phone;

    @NotBlank(message = "角色不能为空")
    private String role; // "farmer" 或 "store_owner"

    private String storeName;
}