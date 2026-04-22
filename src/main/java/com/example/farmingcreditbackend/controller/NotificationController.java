package com.example.farmingcreditbackend.controller;

import com.example.farmingcreditbackend.entity.Notification;
import com.example.farmingcreditbackend.service.NotificationService;
import com.example.farmingcreditbackend.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息通知控制器
 */
@RestController
@RequestMapping("/notifications")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * 获取农户消息列表
     */
    @GetMapping("/farmer/{farmerId}")
    public Result<List<Notification>> getFarmerNotifications(
            @PathVariable Long farmerId,
            @RequestParam(defaultValue = "20") Integer limit) {
        
        List<Notification> notifications = notificationService.getFarmerNotifications(farmerId, limit);
        return Result.success(notifications);
    }
    
    /**
     * 获取农户未读消息
     */
    @GetMapping("/farmer/{farmerId}/unread")
    public Result<List<Notification>> getUnreadNotifications(
            @PathVariable Long farmerId,
            @RequestParam(defaultValue = "10") Integer limit) {
        
        List<Notification> notifications = notificationService.getUnreadNotifications(farmerId, limit);
        return Result.success(notifications);
    }
    
    /**
     * 获取农户未读消息数
     */
    @GetMapping("/farmer/{farmerId}/unread-count")
    public Result<Map<String, Object>> getUnreadCount(@PathVariable Long farmerId) {
        int count = notificationService.getUnreadCount(farmerId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        
        return Result.success(result);
    }
    
    /**
     * 标记消息为已读
     */
    @PutMapping("/{notificationId}/read")
    public Result<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return Result.success();
    }
    
    /**
     * 批量标记为已读
     */
    @PutMapping("/batch-read")
    public Result<Void> markBatchAsRead(@RequestBody List<Long> notificationIds) {
        notificationService.markBatchAsRead(notificationIds);
        return Result.success();
    }
}
