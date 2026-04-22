package com.example.farmingcreditbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.farmingcreditbackend.entity.ReminderLog;

import java.util.List;

/**
 * 提醒记录服务接口
 */
public interface ReminderLogService {
    
    /**
     * 获取农户消息列表
     */
    Page<ReminderLog> getFarmerMessages(Long farmerId, Integer current, Integer size, Integer isRead);
    
    /**
     * 标记消息为已读
     */
    void markAsRead(Long messageId, Long farmerId);
    
    /**
     * 批量标记消息为已读
     */
    void batchMarkAsRead(List<Long> messageIds, Long farmerId);
    
    /**
     * 获取未读消息数量
     */
    long getUnreadCount(Long farmerId);
}
