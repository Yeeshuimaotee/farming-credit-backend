package com.example.farmingcreditbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.farmingcreditbackend.entity.OperationLog;
import com.example.farmingcreditbackend.service.OperationLogService;
import com.example.farmingcreditbackend.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志控制器
 */
@RestController
@RequestMapping("/admin/operation-logs")
@RequiredArgsConstructor
public class OperationLogController {
    
    private final OperationLogService operationLogService;
    
    /**
     * 分页查询操作日志
     */
    @GetMapping
    public Result<Page<OperationLog>> getLogList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        Page<OperationLog> page = operationLogService.getLogList(current, size, userId, startDate, endDate);
        return Result.success(page);
    }
}
