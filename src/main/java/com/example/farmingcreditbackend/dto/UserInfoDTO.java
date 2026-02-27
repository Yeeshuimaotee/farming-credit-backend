package com.example.farmingcreditbackend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 用户信息DTO
 */
@Data
@Builder
public class UserInfoDTO {
    private Long id;
    private String username;
    private String realName;
    private String avatar;
    private String userType;
    private List<String> roles;
    private List<String> permissions;
}
