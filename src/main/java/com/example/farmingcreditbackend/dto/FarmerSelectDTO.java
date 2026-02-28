package com.example.farmingcreditbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
//@NoArgsConstructor
//@AllArgsConstructor
public class FarmerSelectDTO {
    private Long id;
    private String farmerName;
}
//使用@Builder
//UserInfoDTO user = UserInfoDTO.builder()
//        .id(1L)
//        .username("zhangsan")
//        .realName("张三")
//        .build();

//使用@AllArgsConstructor
//UserInfoDTO user = new UserInfoDTO(1L, "zhangsan", "张三", null, null, null, null);
