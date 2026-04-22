package com.example.farmingcreditbackend.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 信用评估记录表实体
 */
@Data
@TableName("credit_evaluation")
public class CreditEvaluation {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("farmer_id")
    private Long farmerId;
    
    @TableField("store_id")
    private Long storeId;
    
    @TableField("evaluation_date")
    private LocalDate evaluationDate;
    
    @TableField("evaluation_type")
    private String evaluationType; // AUTO_TRIGGER-自动触发，MANUAL-手动评估，PERIODIC-定期评估
    
    @TableField("score")
    private Integer score; // 评分 0-100
    
    @TableField("level")
    private String level; // EXCELLENT-优秀，GOOD-良好，NORMAL-一般，POOR-较差，BAD-差
    
    @TableField("repayment_rate")
    private BigDecimal repaymentRate; // 还款准时率（百分比）
    
    @TableField("avg_overdue_days")
    private BigDecimal avgOverdueDays; // 平均逾期天数
    
    @TableField("total_overdue_count")
    private Integer totalOverdueCount; // 累计逾期次数
    
    @TableField("total_order_count")
    private Integer totalOrderCount; // 总订单数
    
    @TableField("total_transaction_amount")
    private BigDecimal totalTransactionAmount; // 总交易金额
    
    @TableField("annual_credit_amount")
    private BigDecimal annualCreditAmount; // 年均赊销金额
    
    @TableField("credit_frequency")
    private Integer creditFrequency; // 赊销频率（近一年次数）
    
    @TableField("active_month_ratio")
    private BigDecimal activeMonthRatio; // 活跃月份占比
    
    @TableField("seasonal_risk_score")
    private BigDecimal seasonalRiskScore; // 季节性风险评分
    
    @TableField("recommended_credit_limit")
    private BigDecimal recommendedCreditLimit; // 建议信用额度
    
    @TableField("risk_factors")
    private String riskFactors; // JSON 格式风险因素
    
    @TableField("improvement_suggestions")
    private String improvementSuggestions; // 改进建议
    
    @TableField("evaluator_id")
    private Long evaluatorId;
    
    @TableField("evaluator_name")
    private String evaluatorName;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
