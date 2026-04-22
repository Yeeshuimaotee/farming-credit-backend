package com.example.farmingcreditbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.farmingcreditbackend.entity.ReminderLog;
import com.example.farmingcreditbackend.mapper.ReminderLogMapper;
import com.example.farmingcreditbackend.service.ReminderLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 提醒记录服务实现类
 */
@Service
@RequiredArgsConstructor
public class ReminderLogServiceImpl implements ReminderLogService {
    
    private final ReminderLogMapper reminderLogMapper;
    
    @Override
    public Page<ReminderLog> getFarmerMessages(Long farmerId, Integer current, Integer size, Integer isRead) {
        Page<ReminderLog> page = new Page<>(current, size);
        
        QueryWrapper<ReminderLog> wrapper = new QueryWrapper<>();
        wrapper.eq("farmer_id", farmerId);
        
        // 按是否已读筛选
        if (isRead != null) {
            if (isRead == 0) {
                // 未读：read_time IS NULL
                wrapper.isNull("read_time");
            } else if (isRead == 1) {
                // 已读：read_time IS NOT NULL
                wrapper.isNotNull("read_time");
            }
        }
        
        // 按时间倒序排列
        wrapper.orderByDesc("create_time");
        
        return reminderLogMapper.selectPage(page, wrapper);
    }
    
    @Override
    @Transactional
    public void markAsRead(Long messageId, Long farmerId) {
        ReminderLog log = reminderLogMapper.selectById(messageId);
        if (log != null && log.getFarmerId().equals(farmerId)) {
            log.setReadTime(LocalDateTime.now());
            log.setSendStatus("READ");
            reminderLogMapper.updateById(log);
        }
    }
    
    @Override
    @Transactional
    public void batchMarkAsRead(List<Long> messageIds, Long farmerId) {
        for (Long messageId : messageIds) {
            markAsRead(messageId, farmerId);
        }
    }
    
    @Override
    public long getUnreadCount(Long farmerId) {
        QueryWrapper<ReminderLog> wrapper = new QueryWrapper<>();
        wrapper.eq("farmer_id", farmerId);
        // 未读：read_time IS NULL
        wrapper.isNull("read_time");
        return reminderLogMapper.selectCount(wrapper);
    }
}
