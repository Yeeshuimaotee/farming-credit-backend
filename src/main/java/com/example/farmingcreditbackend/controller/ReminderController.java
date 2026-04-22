package com.example.farmingcreditbackend.controller;

import com.example.farmingcreditbackend.entity.ReminderLog;
import com.example.farmingcreditbackend.entity.ReminderRule;
import com.example.farmingcreditbackend.service.ReminderService;
import com.example.farmingcreditbackend.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 提醒控制器
 */
@RestController
@RequestMapping("/reminders")
public class ReminderController {
    
    @Autowired
    private ReminderService reminderService;
    
    /**
     * 获取店铺活跃提醒规则
     */
    @GetMapping("/rules/store/{storeId}")
    public Result<List<ReminderRule>> getActiveRules(@PathVariable Long storeId) {
        List<ReminderRule> rules = reminderService.getActiveRules(storeId);
        return Result.success(rules);
    }
    
    /**
     * 获取店铺所有提醒规则（分页）
     */
    @GetMapping("/rules")
    public Result<Object> getRuleList(
            @RequestParam Long storeId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        Object result = reminderService.getRuleList(storeId, page, size);
        return Result.success(result);
    }
    
    /**
     * 创建提醒规则
     */
    @PostMapping("/rules")
    public Result<ReminderRule> createRule(@RequestBody ReminderRule rule) {
        ReminderRule createdRule = reminderService.createRule(rule);
        return Result.success(createdRule);
    }
    
    /**
     * 更新提醒规则
     */
    @PutMapping("/rules")
    public Result<Void> updateRule(@RequestBody ReminderRule rule) {
        reminderService.updateRule(rule);
        return Result.success();
    }
    
    /**
     * 删除提醒规则
     */
    @DeleteMapping("/rules/{ruleId}")
    public Result<Void> deleteRule(@PathVariable Long ruleId) {
        reminderService.deleteRule(ruleId);
        return Result.success();
    }
    
    /**
     * 获取农户提醒记录
     */
    @GetMapping("/farmer/{farmerId}")
    public Result<List<ReminderLog>> getFarmerReminders(
            @PathVariable Long farmerId,
            @RequestParam(defaultValue = "20") Integer limit) {
        List<ReminderLog> reminders = reminderService.getFarmerReminders(farmerId, limit);
        return Result.success(reminders);
    }
    
    /**
     * 标记提醒为已读
     */
    @PutMapping("/{reminderId}/read")
    public Result<Void> markAsRead(@PathVariable Long reminderId) {
        reminderService.markAsRead(reminderId);
        return Result.success();
    }
    
    /**
     * 手动发送逾期提醒
     */
    @PostMapping("/overdue")
    public Result<Void> sendOverdueReminder(
            @RequestParam Long orderId,
            @RequestParam Long farmerId,
            @RequestParam Long storeId) {
        reminderService.sendOverdueReminder(orderId, farmerId, storeId);
        return Result.success();
    }
    
    /**
     * 检查提醒是否已被读取
     */
    @GetMapping("/check-read")
    public Result<Map<String, Object>> checkReminderReadStatus(
            @RequestParam Long orderId,
            @RequestParam Long farmerId) {
        
        boolean isRead = reminderService.checkIfRead(orderId, farmerId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("isRead", isRead);
        return Result.success(result);
    }
}
