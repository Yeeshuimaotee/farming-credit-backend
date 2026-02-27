package com.example.farmingcreditbackend.controller;

import com.example.farmingcreditbackend.vo.Result;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器
 */
@RestController
@RequestMapping("/test")
public class TestController {

    /**
     * 公开访问的测试接口
     */
    @GetMapping("/public")
    public Result<String> publicTest() {
        return Result.success("这是一个公开访问的接口");
    }

    /**
     * 需要认证的测试接口
     */
    @GetMapping("/authenticated")
    @PreAuthorize("isAuthenticated()")
    public Result<String> authenticatedTest() {
        return Result.success("您已通过认证");
    }

    /**
     * 需要管理员角色的测试接口
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> adminTest() {
        return Result.success("您具有管理员权限");
    }

    /**
     * 需要店主角色的测试接口
     */
    @GetMapping("/store-owner")
    @PreAuthorize("hasRole('STORE_OWNER')")
    public Result<String> storeOwnerTest() {
        return Result.success("您具有店主权限");
    }

    /**
     * 需要农户角色的测试接口
     */
    @GetMapping("/farmer")
    @PreAuthorize("hasRole('FARMER')")
    public Result<String> farmerTest() {
        return Result.success("您具有农户权限");
    }
}