package com.example.farmingcreditbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.farmingcreditbackend.entity.Farmer;
import com.example.farmingcreditbackend.entity.ReminderLog;
import com.example.farmingcreditbackend.service.AuthService;
import com.example.farmingcreditbackend.service.FarmerService;
import com.example.farmingcreditbackend.service.ReminderLogService;
import com.example.farmingcreditbackend.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 农户消息接口
 */
@RestController
@RequestMapping("/farmer/messages")
@RequiredArgsConstructor
public class FarmerMessageController {
    
    private final ReminderLogService reminderLogService;
    private final AuthService authService;
    private final FarmerService farmerService;
    
    /**
     * 获取农户消息列表
     */
    @GetMapping
    public Result<Page<ReminderLog>> getMessages(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer isRead) {
        
        Long userId = authService.getCurrentUser().getId();
        // 通过 userId 查询 farmer_id
        com.example.farmingcreditbackend.entity.Farmer farmer = farmerService.getFarmerByUserId(userId);
        Long farmerId = (farmer != null) ? farmer.getId() : userId;
        
        Page<ReminderLog> page = reminderLogService.getFarmerMessages(farmerId, current, size, isRead);
        return Result.success(page);
    }
    
    /**
     * 标记消息为已读
     */
    @PutMapping("/{messageId}/read")
    public Result<Void> markAsRead(@PathVariable Long messageId) {
        Long userId = authService.getCurrentUser().getId();
        // 通过 userId 查询 farmer_id
        com.example.farmingcreditbackend.entity.Farmer farmer = farmerService.getFarmerByUserId(userId);
        Long farmerId = (farmer != null) ? farmer.getId() : userId;
        reminderLogService.markAsRead(messageId, farmerId);
        return Result.success(null);
    }
    
    /**
     * 批量标记消息为已读
     */
    @PutMapping("/batch-read")
    public Result<Void> batchMarkAsRead(@RequestBody List<Long> messageIds) {
        Long userId = authService.getCurrentUser().getId();
        // 通过 userId 查询 farmer_id
        com.example.farmingcreditbackend.entity.Farmer farmer = farmerService.getFarmerByUserId(userId);
        Long farmerId = (farmer != null) ? farmer.getId() : userId;
        reminderLogService.batchMarkAsRead(messageIds, farmerId);
        return Result.success(null);
    }
    
    /**
     * 获取未读消息数量
     */
    @GetMapping("/unread-count")
    public Result<Map<String, Object>> getUnreadCount() {
        Long farmerId = authService.getCurrentUser().getId();
        long count = reminderLogService.getUnreadCount(farmerId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("unreadCount", count);
        return Result.success(result);
    }
}
