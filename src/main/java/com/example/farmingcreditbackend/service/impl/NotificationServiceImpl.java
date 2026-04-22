package com.example.farmingcreditbackend.service.impl;

import com.example.farmingcreditbackend.entity.Notification;
import com.example.farmingcreditbackend.mapper.NotificationMapper;
import com.example.farmingcreditbackend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息通知服务实现类
 */
@Service
public class NotificationServiceImpl implements NotificationService {
    
    @Autowired
    private NotificationMapper notificationMapper;
    
    @Override
    @Transactional
    public Notification sendNotification(Long farmerId, Long storeId, String title, String content, String type, String priority) {
        Notification notification = new Notification();
        notification.setFarmerId(farmerId);
        notification.setStoreId(storeId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setPriority(priority != null ? priority : "MEDIUM");
        notification.setIsRead(0);
        notification.setCreateTime(LocalDateTime.now());
        
        notificationMapper.insert(notification);
        return notification;
    }
    
    @Override
    @Transactional
    public void sendRepaymentReminder(Long farmerId, Long storeId, String orderNo, String dueDate) {
        String title = "还款提醒";
        String content = String.format("尊敬的农户，您的订单%s将于%s到期，请及时还款。", orderNo, dueDate);
        sendNotification(farmerId, storeId, title, content, "REPAYMENT_REMINDER", "MEDIUM");
    }
    
    @Override
    @Transactional
    public void sendOverdueAlert(Long farmerId, Long storeId, String orderNo, Integer overdueDays, String debtAmount) {
        String title = "逾期提醒";
        String content = String.format("尊敬的农户，您的订单%s已逾期%d天，欠款金额%s元，请尽快还款。", orderNo, overdueDays, debtAmount);
        sendNotification(farmerId, storeId, title, content, "OVERDUE_ALERT", "HIGH");
    }
    
    @Override
    @Transactional
    public void sendCreditEvaluationNotice(Long farmerId, Long storeId, String creditLevel, Integer creditScore) {
        String title = "信用评估结果通知";
        String content = String.format("尊敬的农户，您的最新信用评分为%d分，等级为%s。请继续保持良好的信用记录。", creditScore, creditLevel);
        sendNotification(farmerId, storeId, title, content, "CREDIT_EVAL", "LOW");
    }
    
    @Override
    public List<Notification> getFarmerNotifications(Long farmerId, int limit) {
        return notificationMapper.selectByFarmerId(farmerId, limit);
    }
    
    @Override
    public List<Notification> getUnreadNotifications(Long farmerId, int limit) {
        return notificationMapper.selectUnread(farmerId, limit);
    }
    
    @Override
    public int getUnreadCount(Long farmerId) {
        return notificationMapper.countUnread(farmerId);
    }
    
    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification != null && notification.getIsRead() == 0) {
            notification.setIsRead(1);
            notification.setReadTime(LocalDateTime.now());
            notificationMapper.updateById(notification);
        }
    }
    
    @Override
    @Transactional
    public void markBatchAsRead(List<Long> notificationIds) {
        for (Long id : notificationIds) {
            markAsRead(id);
        }
    }
}
