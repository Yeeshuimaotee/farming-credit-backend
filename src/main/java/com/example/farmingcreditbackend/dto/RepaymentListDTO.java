package com.example.farmingcreditbackend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 还款记录列表 DTO（包含订单号和农户名称）
 */
@Data
public class RepaymentListDTO {
    private Long id;
    private String repaymentNo;
    private String orderNo;
    private String farmerName;
    private LocalDate repaymentDate;
    private BigDecimal repaymentAmount;
    private String repaymentType;
    private BigDecimal beforeDebt;
    private BigDecimal afterDebt;
    private String remark;
    private String operatorName;
}
