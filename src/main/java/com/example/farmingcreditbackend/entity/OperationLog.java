package com.example.farmingcreditbackend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体
 */
@Data
@TableName("operation_log")
public class OperationLog {
    
    /**
     * 日志 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 操作用户 ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 操作类型（INSERT/UPDATE/DELETE/QUERY）
     */
    private String operationType;
    
    /**
     * 操作模块（USER/PRODUCT/ORDER 等）
     */
    private String operationModule;
    
    /**
     * 请求方法（GET/POST/PUT/DELETE）
     */
    private String operationMethod;
    
    /**
     * 请求 URL
     */
    private String requestUrl;
    
    /**
     * 请求参数
     */
    private String requestParams;
    
    /**
     * 响应数据
     */
    private String responseData;
    
    /**
     * IP 地址
     */
    private String ipAddress;
    
    /**
     * 浏览器信息
     */
    private String userAgent;
    
    /**
     * 执行时长（毫秒）
     */
    private Long executionTime;
    
    /**
     * 状态：0-失败，1-成功
     */
    private Integer status;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
