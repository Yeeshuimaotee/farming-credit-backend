package com.example.farmingcreditbackend.dto;

import lombok.Data;

@Data
public class UserListRequestDTO {
    private Integer currentPage = 1;
    private Integer size = 10;
    private String username;
    private String userType; // ADMIN, STORE_OWNER, FARMER
    private String phone;
}