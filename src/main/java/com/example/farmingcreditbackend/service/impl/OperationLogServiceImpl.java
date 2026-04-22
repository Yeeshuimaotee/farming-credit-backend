package com.example.farmingcreditbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.farmingcreditbackend.entity.OperationLog;
import com.example.farmingcreditbackend.mapper.OperationLogMapper;
import com.example.farmingcreditbackend.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 操作日志服务实现类
 */
@Service
public class OperationLogServiceImpl implements OperationLogService {
    
    @Autowired
    private OperationLogMapper operationLogMapper;
    
    @Override
    public void saveLog(OperationLog log) {
        log.setCreateTime(LocalDateTime.now());
        operationLogMapper.insert(log);
    }
    
    @Override
    public Page<OperationLog> getLogList(Integer current, Integer size, Long userId, String startDate, String endDate) {
        Page<OperationLog> page = new Page<>(current, size);
        QueryWrapper<OperationLog> wrapper = new QueryWrapper<>();
        
        if (userId != null) {
            wrapper.eq("user_id", userId);
        }
        if (startDate != null && endDate != null) {
            wrapper.between("create_time", startDate, endDate);
        }
        
        wrapper.orderByDesc("create_time");
        return operationLogMapper.selectPage(page, wrapper);
    }
    
    @Override
    public byte[] exportLogs(Long userId, String startDate, String endDate) {
        // TODO: 实现导出逻辑
        return new byte[0];
    }
}
