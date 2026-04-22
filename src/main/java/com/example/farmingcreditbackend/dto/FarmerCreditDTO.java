package com.example.farmingcreditbackend.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 设置农户信用额度 DTO
 */
@Data
public class FarmerCreditDTO {
    
    /**
     * 信用额度
     */
    private BigDecimal creditLimit;
    
    /**
     * 备注说明
     */
    private String remark;
}
