package com.example.farmingcreditbackend.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 提醒记录表实体
 */
@Data
@TableName("reminder_log")
public class ReminderLog {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("rule_id")
    private Long ruleId;
    
    @TableField("farmer_id")
    private Long farmerId;
    
    @TableField("store_id")
    private Long storeId;
    
    @TableField("reminder_type")
    private String reminderType;
    
    @TableField("reminder_date")
    private LocalDate reminderDate;
    
    @TableField("message_content")
    private String messageContent;
    
    @TableField("notification_channels")
    private String notificationChannels; // JSON 数组
    
    @TableField("send_status")
    private String sendStatus; // PENDING-待发送，SENT-已发送，FAILED-失败，READ-已读
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("read_time")
    private LocalDateTime readTime;
    
    @TableField("remark")
    private String remark;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
