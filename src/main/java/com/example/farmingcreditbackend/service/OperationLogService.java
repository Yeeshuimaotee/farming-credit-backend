package com.example.farmingcreditbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.farmingcreditbackend.entity.OperationLog;

/**
 * 操作日志服务接口
 */
public interface OperationLogService {
    
    /**
     * 保存操作日志
     */
    void saveLog(OperationLog log);
    
    /**
     * 分页查询日志
     */
    Page<OperationLog> getLogList(Integer current, Integer size, Long userId, String startDate, String endDate);
    
    /**
     * 导出日志（暂不实现）
     */
    byte[] exportLogs(Long userId, String startDate, String endDate);
}
