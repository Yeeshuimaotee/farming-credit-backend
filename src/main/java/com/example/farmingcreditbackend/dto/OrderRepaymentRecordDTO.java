package com.example.farmingcreditbackend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OrderRepaymentRecordDTO {
    private Long id;
    private String repaymentNo;
    private LocalDate repaymentDate;
    private BigDecimal repaymentAmount;
    private String repaymentType;   // 如 CASH, WECHAT 等
    private BigDecimal beforeDebt;
    private BigDecimal afterDebt;
    private String remark;
}