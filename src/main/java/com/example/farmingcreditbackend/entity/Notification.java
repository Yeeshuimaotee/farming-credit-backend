package com.example.farmingcreditbackend.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息通知表实体
 */
@Data
@TableName("notification")
public class Notification {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("farmer_id")
    private Long farmerId;
    
    @TableField("store_id")
    private Long storeId;
    
    @TableField("title")
    private String title;
    
    @TableField("content")
    private String content;
    
    @TableField("type")
    private String type; // REPAYMENT_REMINDER-还款提醒，OVERDUE_ALERT-逾期提醒，CREDIT_EVAL-信用评估，SYSTEM-系统消息
    
    @TableField("priority")
    private String priority; // HIGH-高，MEDIUM-中，LOW-低
    
    @TableField("is_read")
    private Integer isRead; // 0-未读，1-已读
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("read_time")
    private LocalDateTime readTime;
    
    @TableField("related_type")
    private String relatedType; // 关联业务类型（ORDER, REPAYMENT等）
    
    @TableField("related_id")
    private Long relatedId; // 关联业务 ID
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
