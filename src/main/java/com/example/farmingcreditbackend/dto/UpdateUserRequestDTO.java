package com.example.farmingcreditbackend.dto;

import lombok.Data;
import javax.validation.constraints.Pattern;

@Data
public class UpdateUserRequestDTO {
    private String realName;
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    private Integer status; // 0-禁用，1-启用
}