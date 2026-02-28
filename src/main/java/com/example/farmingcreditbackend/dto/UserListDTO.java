package com.example.farmingcreditbackend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserListDTO {
    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String userType;
    private Integer status;
    private LocalDateTime createTime;
}