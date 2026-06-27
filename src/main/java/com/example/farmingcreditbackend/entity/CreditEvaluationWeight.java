package com.example.farmingcreditbackend.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 信用评估权重配置实体
 */
@Data
@TableName("credit_evaluation_weight")
public class CreditEvaluationWeight {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("store_id")
    private Long storeId;
    
    @TableField("repayment_rate_weight")
    private Double repaymentRateWeight; // 还款准时率权重
    
    @TableField("overdue_days_weight")
    private Double overdueDaysWeight; // 平均逾期天数权重
    
    @TableField("overdue_count_weight")
    private Double overdueCountWeight; // 逾期次数权重
    
    @TableField("order_count_weight")
    private Double orderCountWeight; // 总订单数权重
    
    @TableField("credit_amount_weight")
    private Double creditAmountWeight; // 年信用额度权重
    
    @TableField("base_score")
    private Integer baseScore; // 基础分数
    
    @TableField("created_by")
    private Long createdBy;
    
    @TableField("updated_by")
    private Long updatedBy;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
