package com.example.farmingcreditbackend.service;

public interface AdminDashboardService {

    /**
     * 获取仪表盘数据
     */
    Object getDashboardData();

    /**
     * 获取系统日志
     */
    Object getSystemLogs(Integer page, Integer size);
}
