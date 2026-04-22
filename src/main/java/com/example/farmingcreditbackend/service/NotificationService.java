package com.example.farmingcreditbackend.service;

import com.example.farmingcreditbackend.entity.Notification;

import java.util.List;

/**
 * 消息通知服务接口
 */
public interface NotificationService {
    
    /**
     * 发送消息
     */
    Notification sendNotification(Long farmerId, Long storeId, String title, String content, String type, String priority);
    
    /**
     * 发送还款提醒
     */
    void sendRepaymentReminder(Long farmerId, Long storeId, String orderNo, String dueDate);
    
    /**
     * 发送逾期提醒
     */
    void sendOverdueAlert(Long farmerId, Long storeId, String orderNo, Integer overdueDays, String debtAmount);
    
    /**
     * 发送信用评估通知
     */
    void sendCreditEvaluationNotice(Long farmerId, Long storeId, String creditLevel, Integer creditScore);
    
    /**
     * 获取农户消息列表
     */
    List<Notification> getFarmerNotifications(Long farmerId, int limit);
    
    /**
     * 获取农户未读消息
     */
    List<Notification> getUnreadNotifications(Long farmerId, int limit);
    
    /**
     * 获取农户未读消息数
     */
    int getUnreadCount(Long farmerId);
    
    /**
     * 标记消息为已读
     */
    void markAsRead(Long notificationId);
    
    /**
     * 批量标记为已读
     */
    void markBatchAsRead(List<Long> notificationIds);
}
