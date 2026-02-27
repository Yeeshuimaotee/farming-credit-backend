package com.example.farmingcreditbackend.controller;

import com.example.farmingcreditbackend.dto.LoginRequestDTO;
import com.example.farmingcreditbackend.dto.LoginResponseDTO;
import com.example.farmingcreditbackend.dto.UserInfoDTO;
import com.example.farmingcreditbackend.entity.User;
import com.example.farmingcreditbackend.service.AuthService;
import com.example.farmingcreditbackend.service.CaptchaService;
import com.example.farmingcreditbackend.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
@Slf4j
@Validated
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private CaptchaService captchaService;

    /**
     * 获取验证码
     */
    @GetMapping("/captcha")
    public Result<?> getCaptcha() {
        try {
            return Result.success(captchaService.generateCaptcha());
        } catch (Exception e) {
            log.error("获取验证码失败", e);
            return Result.error("获取验证码失败");
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            // 执行登录
            LoginResponseDTO loginResponse = authService.login(loginRequest);
            return Result.success(loginResponse);
        } catch (Exception e) {
            log.error("登录异常", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/userinfo")
    @PreAuthorize("isAuthenticated()")
    public Result<UserInfoDTO> getUserInfo(HttpServletRequest request) {
        try {
            String token = extractToken(request);
            UserInfoDTO userInfo = authService.getUserInfo(token);
            return Result.success(userInfo);
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return Result.error("获取用户信息失败");
        }
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public Result<Void> logout(HttpServletRequest request) {
        try {
            String token = extractToken(request);
            authService.logout(token);
            return Result.success();
        } catch (Exception e) {
            log.error("退出登录失败", e);
            return Result.error("退出登录失败");
        }
    }

    /**
     * 测试接口 - 公开访问
     */
    @GetMapping("/test")
    public Result<String> test() {
        return Result.success("认证服务正常运行");
    }

    /**
     * 测试接口 - 需要认证
     */
    @GetMapping("/test-auth")
    @PreAuthorize("isAuthenticated()")
    public Result<String> testAuth() {
        return Result.success("您已通过认证");
    }

    /**
     * 从请求头提取Token
     */
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new IllegalArgumentException("无效的Token");
    }
}