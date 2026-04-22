package com.example.farmingcreditbackend.service;

import com.example.farmingcreditbackend.entity.ReminderLog;
import com.example.farmingcreditbackend.entity.ReminderRule;

import java.util.List;

/**
 * 提醒服务接口
 */
public interface ReminderService {
    
    /**
     * 获取店铺所有活跃提醒规则
     */
    List<ReminderRule> getActiveRules(Long storeId);
    
    /**
     * 创建提醒规则
     */
    ReminderRule createRule(ReminderRule rule);
    
    /**
     * 更新提醒规则
     */
    void updateRule(ReminderRule rule);
    
    /**
     * 删除提醒规则
     */
    void deleteRule(Long ruleId);
    
    /**
     * 发送逾期提醒
     */
    void sendOverdueReminder(Long orderId, Long farmerId, Long storeId);
    
    /**
     * 发送季节性提醒
     */
    void sendSeasonalReminder(Long farmerId, Long storeId, String ruleName, String messageTemplate);
    
    /**
     * 获取农户提醒记录
     */
    List<ReminderLog> getFarmerReminders(Long farmerId, int limit);
    
    /**
     * 标记提醒为已读
     */
    void markAsRead(Long reminderId);
    
    /**
     * 执行定时任务：扫描并发送提醒
     */
    void executeScheduledReminders();
    
    /**
     * 获取店铺提醒规则列表（分页）
     */
    Object getRuleList(Long storeId, Integer page, Integer size);
    
    /**
     * 检查订单的提醒是否已被农户读取
     */
    boolean checkIfRead(Long orderId, Long farmerId);
}
