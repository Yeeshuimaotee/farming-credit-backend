package com.example.farmingcreditbackend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class FarmerCreditInfoDTO {
    private Long farmerId;
    private BigDecimal totalCreditLimit;  // 信用额度
    private BigDecimal usedCredit;        // 已用额度（当前欠款）
    private BigDecimal availableCredit;   // 可用额度
}