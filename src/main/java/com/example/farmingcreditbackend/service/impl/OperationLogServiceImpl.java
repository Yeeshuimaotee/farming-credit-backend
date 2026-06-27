package com.example.farmingcreditbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.farmingcreditbackend.entity.OperationLog;
import com.example.farmingcreditbackend.mapper.OperationLogMapper;
import com.example.farmingcreditbackend.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogMapper operationLogMapper;

    @Override
    public Object getOperationLogList(Integer page, Integer size, String username, String logType) {
        // 创建分页对象
        Page<OperationLog> operationLogPage = new Page<>(page, size);
        
        // 创建查询条件
        QueryWrapper<OperationLog> queryWrapper = new QueryWrapper<>();
        
        // 按用户名筛选
        if (username != null && !username.isEmpty()) {
            queryWrapper.eq("username", username);
        }
        
        // 按日志类型筛选
        if (logType != null && !logType.isEmpty()) {
            queryWrapper.eq("log_type", logType);
        }
        
        // 按创建时间倒序排序
        queryWrapper.orderByDesc("create_time");
        
        // 执行查询
        Page<OperationLog> result = operationLogMapper.selectPage(operationLogPage, queryWrapper);
        
        // 转换为前端需要的格式
        List<Map<String, Object>> logs = new ArrayList<>();
        for (OperationLog log : result.getRecords()) {
            Map<String, Object> logMap = new HashMap<>();
            logMap.put("time", log.getCreateTime());
            logMap.put("username", log.getUsername());
            logMap.put("logType", log.getOperationType());
            logMap.put("operation", log.getOperationModule());
            logMap.put("ip", log.getIpAddress());
            logMap.put("duration", log.getExecutionTime());
            logs.add(logMap);
        }
        
        // 构建响应对象
        Map<String, Object> response = new HashMap<>();
        response.put("records", logs);
        response.put("total", result.getTotal());
        response.put("current", result.getCurrent());
        response.put("size", result.getSize());
        
        return response;
    }

    @Override
    public void saveLog(OperationLog operationLog) {
        operationLogMapper.insert(operationLog);
    }
}
