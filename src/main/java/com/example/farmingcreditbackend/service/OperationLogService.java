package com.example.farmingcreditbackend.service;

import com.example.farmingcreditbackend.entity.OperationLog;

public interface OperationLogService {

    /**
     * 获取操作日志列表
     */
    Object getOperationLogList(Integer page, Integer size, String username, String logType);

    /**
     * 保存操作日志
     */
    void saveLog(OperationLog operationLog);
}
