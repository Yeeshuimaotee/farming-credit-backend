package com.example.farmingcreditbackend.controller;

import com.example.farmingcreditbackend.service.AdminDashboardService;
import com.example.farmingcreditbackend.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    /**
     * 获取管理员仪表盘数据
     */
    @GetMapping("/admin")
    public Result<Object> getAdminDashboard() {
        return Result.success(adminDashboardService.getDashboardData());
    }

    /**
     * 获取系统日志列表
     */
    @GetMapping("/logs")
    public Result<Object> getSystemLogs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(adminDashboardService.getSystemLogs(page, size));
    }
}
