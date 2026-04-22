package com.example.farmingcreditbackend.service.impl;

import com.example.farmingcreditbackend.entity.Farmer;
import com.example.farmingcreditbackend.entity.Order;
import com.example.farmingcreditbackend.entity.ReminderLog;
import com.example.farmingcreditbackend.entity.ReminderRule;
import com.example.farmingcreditbackend.mapper.FarmerMapper;
import com.example.farmingcreditbackend.mapper.OrderMapper;
import com.example.farmingcreditbackend.mapper.ReminderLogMapper;
import com.example.farmingcreditbackend.mapper.ReminderRuleMapper;
import com.example.farmingcreditbackend.service.ReminderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 提醒服务实现类
 */
@Slf4j
@Service
public class ReminderServiceImpl implements ReminderService {
    
    @Autowired
    private ReminderRuleMapper reminderRuleMapper;
    
    @Autowired
    private ReminderLogMapper reminderLogMapper;
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private FarmerMapper farmerMapper;
    
    @Override
    public List<ReminderRule> getActiveRules(Long storeId) {
        return reminderRuleMapper.selectActiveRulesByStoreId(storeId);
    }
    
    @Override
    @Transactional
    public ReminderRule createRule(ReminderRule rule) {
        reminderRuleMapper.insert(rule);
        return rule;
    }
    
    @Override
    @Transactional
    public void updateRule(ReminderRule rule) {
        reminderRuleMapper.updateById(rule);
    }
    
    @Override
    @Transactional
    public void deleteRule(Long ruleId) {
        reminderRuleMapper.deleteById(ruleId);
    }
    
    @Override
    @Transactional
    public void sendOverdueReminder(Long orderId, Long farmerId, Long storeId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return;
        }
        
        Farmer farmer = farmerMapper.selectById(farmerId);
        if (farmer == null) {
            return;
        }
        
        // 重新计算逾期天数
        int overdueDays = 0;
        if (order.getDueDate() != null) {
            overdueDays = (int) java.time.temporal.ChronoUnit.DAYS.between(order.getDueDate(), java.time.LocalDate.now());
            overdueDays = Math.max(0, overdueDays);
        }
        
        String messageTemplate = "尊敬的{farmerName}，您的赊销订单（{orderNo}）已逾期{overdueDays}天，欠款金额{debtAmount}元，请及时还款。";
        String message = messageTemplate
                .replace("{farmerName}", farmer.getFarmerName())
                .replace("{orderNo}", order.getOrderNo())
                .replace("{overdueDays}", String.valueOf(overdueDays))
                .replace("{debtAmount}", order.getDebtAmount().toString());
        
        // 检查是否已经为该订单发送过逾期提醒（今天）- 通过消息内容判断
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ReminderLog> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.eq("farmer_id", farmerId)
                .eq("reminder_type", "OVERDUE")
                .like("message_content", order.getOrderNo())
                .ge("create_time", java.time.LocalDateTime.now().toLocalDate().atStartOfDay());
        
        long count = reminderLogMapper.selectCount(wrapper);
        if (count > 0) {
            // 今天已经发送过该订单的逾期提醒，不再重复发送
            return;
        }
        
        ReminderLog reminderLog = new ReminderLog();
        reminderLog.setFarmerId(farmerId);
        reminderLog.setStoreId(storeId);
        reminderLog.setReminderType("OVERDUE");
        reminderLog.setReminderDate(LocalDate.now());
        reminderLog.setMessageContent(message);
        reminderLog.setNotificationChannels("[\"APP\"]");
        reminderLog.setSendStatus("SENT");
        reminderLog.setCreateTime(LocalDateTime.now());
        reminderLog.setRemark("订单号：" + order.getOrderNo());
        
        reminderLogMapper.insert(reminderLog);
    }
    
    @Override
    @Transactional
    public void sendSeasonalReminder(Long farmerId, Long storeId, String ruleName, String messageTemplate) {
        Farmer farmer = farmerMapper.selectById(farmerId);
        if (farmer == null) {
            return;
        }
        
        String message = messageTemplate.replace("{farmerName}", farmer.getFarmerName());
        
        ReminderLog reminderLog = new ReminderLog();
        reminderLog.setFarmerId(farmerId);
        reminderLog.setStoreId(storeId);
        reminderLog.setReminderType("SEASONAL");
        reminderLog.setReminderDate(LocalDate.now());
        reminderLog.setMessageContent(message);
        reminderLog.setNotificationChannels("[\"APP\"]");
        reminderLog.setSendStatus("SENT");
        reminderLog.setCreateTime(LocalDateTime.now());
        
        reminderLogMapper.insert(reminderLog);
    }
    
    @Override
    public List<ReminderLog> getFarmerReminders(Long farmerId, int limit) {
        return reminderLogMapper.selectByFarmerId(farmerId, limit);
    }
    
    @Override
    @Transactional
    public void markAsRead(Long reminderId) {
        ReminderLog reminderLog = reminderLogMapper.selectById(reminderId);
        if (reminderLog != null) {
            reminderLog.setSendStatus("READ");
            reminderLog.setReadTime(LocalDateTime.now());
            reminderLogMapper.updateById(reminderLog);
        }
    }
    
    @Override
    @Transactional
    @Scheduled(cron = "0 0 8 * * ?")
    public void executeScheduledReminders() {
        log.info("=== 开始执行定时提醒任务 ===");
        
        LocalDate today = LocalDate.now();
        int currentMonth = today.getMonthValue();
        
        List<ReminderRule> rules = reminderRuleMapper.selectAllActiveRules();
        
        for (ReminderRule rule : rules) {
            try {
                if ("SEASONAL".equals(rule.getRuleType())) {
                    // 处理季节性提醒：提前 7 天和 3 天发送
                    handleSeasonalReminder(rule, today, currentMonth);
                } else if ("OVERDUE".equals(rule.getRuleType())) {
                    // 处理逾期提醒
                    handleOverdueReminder(rule);
                } else if ("DUE_SOON".equals(rule.getRuleType())) {
                    // 处理即将到期提醒
                    handleDueSoonReminder(rule, today);
                }
            } catch (Exception e) {
                log.error("处理规则 {} 时出错：{}", rule.getRuleName(), e.getMessage(), e);
            }
        }
        
        log.info("=== 定时提醒任务执行完成 ===");
    }
    
    /**
     * 处理季节性提醒
     */
    private void handleSeasonalReminder(ReminderRule rule, LocalDate today, int currentMonth) {
        if (rule.getSeasonalMonths() == null || !rule.getSeasonalMonths().contains(String.valueOf(currentMonth))) {
            return;
        }
        
        Long storeId = rule.getStoreId();
        
        // 查询 7 天后到期的订单（今天之后 1-7 天）
        List<Order> ordersIn7Days = orderMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Order>()
                .eq("store_id", storeId)
                .eq("order_status", "APPROVED")
                .between("due_date", today.plusDays(1), today.plusDays(7))
        );
        
        log.info("店铺 {} 在 7 天后到期的订单数量：{}", storeId, ordersIn7Days.size());
        
        for (Order order : ordersIn7Days) {
            try {
                // 检查今天是否已经发送过提醒
                if (!hasSentReminderToday(order.getId(), order.getFarmerId(), "SEASONAL_7DAYS")) {
                    sendSeasonalReminder(
                        order.getFarmerId(),
                        order.getStoreId(),
                        rule.getRuleName() + " (提前 7 天)",
                        rule.getMessageTemplate() + "【您的订单将在 7 天后到期，请提前准备还款】"
                    );
                    log.info("已发送提前 7 天提醒：订单号={}, 农户 ID={}", order.getOrderNo(), order.getFarmerId());
                }
            } catch (Exception e) {
                log.error("发送提前 7 天提醒失败：订单号={}, 错误={}", order.getOrderNo(), e.getMessage());
            }
        }
        
        // 查询 3 天后到期的订单（今天之后 1-3 天）
        List<Order> ordersIn3Days = orderMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Order>()
                .eq("store_id", storeId)
                .eq("order_status", "APPROVED")
                .between("due_date", today.plusDays(1), today.plusDays(3))
        );
        
        log.info("店铺 {} 在 3 天后到期的订单数量：{}", storeId, ordersIn3Days.size());
        
        for (Order order : ordersIn3Days) {
            try {
                // 检查今天是否已经发送过提醒
                if (!hasSentReminderToday(order.getId(), order.getFarmerId(), "SEASONAL_3DAYS")) {
                    sendSeasonalReminder(
                        order.getFarmerId(),
                        order.getStoreId(),
                        rule.getRuleName() + " (提前 3 天)",
                        rule.getMessageTemplate() + "【您的订单将在 3 天后到期，请及时还款】"
                    );
                    log.info("已发送提前 3 天提醒：订单号={}, 农户 ID={}", order.getOrderNo(), order.getFarmerId());
                }
            } catch (Exception e) {
                log.error("发送提前 3 天提醒失败：订单号={}, 错误={}", order.getOrderNo(), e.getMessage());
            }
        }
    }
    
    /**
     * 处理即将到期提醒
     */
    private void handleDueSoonReminder(ReminderRule rule, LocalDate today) {
        Long storeId = rule.getStoreId();
        
        // 查询 1 天后到期的订单
        List<Order> ordersIn1Day = orderMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Order>()
                .eq("store_id", storeId)
                .eq("order_status", "APPROVED")
                .between("due_date", today.plusDays(1), today.plusDays(1))
        );
        
        for (Order order : ordersIn1Day) {
            try {
                if (!hasSentReminderToday(order.getId(), order.getFarmerId(), "DUE_SOON_1DAY")) {
                    sendSeasonalReminder(
                        order.getFarmerId(),
                        order.getStoreId(),
                        rule.getRuleName(),
                        rule.getMessageTemplate() + "【您的订单将在明天到期，请务必还款】"
                    );
                }
            } catch (Exception e) {
                log.error("发送即将到期提醒失败：订单号={}, 错误={}", order.getOrderNo(), e.getMessage());
            }
        }
    }
    
    /**
     * 处理逾期提醒
     */
    private void handleOverdueReminder(ReminderRule rule) {
        Long storeId = rule.getStoreId();
        
        List<Order> overdueOrders = orderMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Order>()
                .eq("store_id", storeId)
                .eq("order_status", "OVERDUE")
        );
        
        for (Order order : overdueOrders) {
            try {
                sendOverdueReminder(order.getId(), order.getFarmerId(), order.getStoreId());
            } catch (Exception e) {
                log.error("发送逾期提醒失败：订单号={}, 错误={}", order.getOrderNo(), e.getMessage());
            }
        }
    }
    
    /**
     * 检查今天是否已发送过某种类型的提醒
     */
    private boolean hasSentReminderToday(Long orderId, Long farmerId, String reminderType) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return false;
        }
        
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ReminderLog> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.eq("farmer_id", farmerId)
                .like("message_content", order.getOrderNo())
                .like("message_content", reminderType)
                .ge("create_time", LocalDate.now().atStartOfDay());
        
        return reminderLogMapper.selectCount(wrapper) > 0;
    }
    
    @Override
    public Object getRuleList(Long storeId, Integer page, Integer size) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ReminderRule> pageData = 
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ReminderRule> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.eq("store_id", storeId);
        wrapper.orderByDesc("create_time");
        
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ReminderRule> result = 
            reminderRuleMapper.selectPage(pageData, wrapper);
        
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("records", result.getRecords());
        response.put("total", result.getTotal());
        response.put("size", result.getSize());
        response.put("current", result.getCurrent());
        
        return response;
    }
    
    @Override
    public boolean checkIfRead(Long orderId, Long farmerId) {
        // 通过订单号在消息内容中查找
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return false;
        }
        
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ReminderLog> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.eq("farmer_id", farmerId)
                .like("message_content", order.getOrderNo())
                .isNotNull("read_time");
        
        return reminderLogMapper.selectCount(wrapper) > 0;
    }
}
