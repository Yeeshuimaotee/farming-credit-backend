package com.example.farmingcreditbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.farmingcreditbackend.entity.SysLog;
import com.example.farmingcreditbackend.entity.User;
import com.example.farmingcreditbackend.mapper.SysLogMapper;
import com.example.farmingcreditbackend.mapper.UserMapper;
import com.example.farmingcreditbackend.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final UserMapper userMapper;
    private final SysLogMapper sysLogMapper;

    @Override
    public Object getDashboardData() {
        Map<String, Object> dashboardData = new HashMap<>();

        // 获取总用户数
        long totalUsers = userMapper.selectCount(null);
        dashboardData.put("totalUsers", totalUsers);

        // 模拟在线用户数（实际项目中可以从Redis或会话管理中获取）
        int onlineUsers = (int) (Math.random() * 10) + 1;
        dashboardData.put("onlineUsers", onlineUsers);

        // 模拟活跃会话数
        int activeSessions = onlineUsers * 2;
        dashboardData.put("activeSessions", activeSessions);

        // 模拟系统消息数
        int systemMessages = (int) (Math.random() * 5);
        dashboardData.put("systemMessages", systemMessages);

        // 模拟用户增长趋势
        List<Map<String, Object>> userTrend = new ArrayList<>();
        String[] days = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        for (int i = 0; i < days.length; i++) {
            Map<String, Object> trend = new HashMap<>();
            trend.put("date", days[i]);
            // 从数据库中查询当天的新用户数（这里使用模拟数据）
            trend.put("newUsers", (int) (Math.random() * 20) + 1);
            userTrend.add(trend);
        }
        dashboardData.put("userTrend", userTrend);

        // 从数据库中查询日志分布
        Map<String, Integer> logDistribution = getLogDistribution();
        dashboardData.put("logDistribution", logDistribution);

        // 模拟系统资源使用情况
        Map<String, String> systemResources = new HashMap<>();
        systemResources.put("cpuUsage", (int) (Math.random() * 50) + 10 + "%");
        systemResources.put("memoryUsage", (int) (Math.random() * 60) + 20 + "%");
        systemResources.put("diskUsage", (int) (Math.random() * 70) + 10 + "%");
        dashboardData.put("systemResources", systemResources);

        return dashboardData;
    }

    @Override
    public Object getSystemLogs(Integer page, Integer size) {
        // 从数据库中查询系统日志
        Page<SysLog> logPage = new Page<>(page, size);
        QueryWrapper<SysLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");

        Page<SysLog> result = sysLogMapper.selectPage(logPage, queryWrapper);

        // 转换为前端需要的格式
        List<Map<String, Object>> logs = new ArrayList<>();
        for (SysLog log : result.getRecords()) {
            Map<String, Object> logMap = new HashMap<>();
            logMap.put("time", log.getCreateTime());
            logMap.put("user", log.getUsername());
            logMap.put("action", log.getOperation());
            logMap.put("ip", log.getIp());
            logs.add(logMap);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("records", logs);
        response.put("total", result.getTotal());
        response.put("current", result.getCurrent());
        response.put("size", result.getSize());

        return response;
    }

    /**
     * 获取日志分布
     */
    private Map<String, Integer> getLogDistribution() {
        Map<String, Integer> logDistribution = new HashMap<>();
        
        // 从数据库中查询不同类型的日志数量
        // 这里使用模拟数据，实际项目中可以根据日志表的level字段进行统计
        logDistribution.put("INFO", 80);
        logDistribution.put("WARN", 10);
        logDistribution.put("ERROR", 5);
        logDistribution.put("DEBUG", 5);
        
        return logDistribution;
    }
}
