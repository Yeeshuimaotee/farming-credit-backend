package com.example.farmingcreditbackend.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserListResponseDTO {
    private List<UserListDTO> list;
    private Long total;
}