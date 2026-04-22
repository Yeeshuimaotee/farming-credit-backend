package com.example.farmingcreditbackend.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 提醒规则表实体
 */
@Data
@TableName("reminder_rule")
public class ReminderRule {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("rule_name")
    private String ruleName;
    
    @TableField("store_id")
    private Long storeId;
    
    @TableField("rule_type")
    private String ruleType; // SEASONAL-季节性，OVERDUE-逾期，BIRTHDAY-生日，CUSTOM-自定义
    
    @TableField("trigger_type")
    private String triggerType; // DATE-指定日期，DAY_BEFORE-提前 N 天，DAY_AFTER-延后 N 天
    
    @TableField("trigger_days")
    private Integer triggerDays;
    
    @TableField("seasonal_months")
    private String seasonalMonths; // JSON 数组，如 [2,3,4]
    
    @TableField("message_template")
    private String messageTemplate;
    
    @TableField("notification_channels")
    private String notificationChannels; // JSON 数组，如 ["APP","SMS"]
    
    @TableField("is_active")
    private Integer isActive; // 0-禁用，1-启用
    
    @TableField("creator_id")
    private Long creatorId;
    
    @TableField("creator_name")
    private String creatorName;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
