package com.example.farmingcreditbackend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 用户信息 DTO
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
    // 新增字段
    private Long farmerId;  // 农户 ID（如果是农户）
    private Long storeId;   // 店铺 ID（如果是店主）
}
