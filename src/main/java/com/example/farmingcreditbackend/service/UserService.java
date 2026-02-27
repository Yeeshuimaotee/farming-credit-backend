package com.example.farmingcreditbackend.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.farmingcreditbackend.entity.User;
import com.example.farmingcreditbackend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务
 */
@Service
@Slf4j
public class UserService extends ServiceImpl<UserMapper, User> {

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据用户名查询用户
     */
    public User findByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    /**
     * 根据ID查询用户
     */
    public User findById(Long id) {
        return userMapper.selectById(id);
    }

    /**
     * 更新登录信息
     */
    public void updateLoginInfo(Long userId, String loginIp, LocalDateTime loginTime) {
        userMapper.updateLoginInfo(userId, loginIp, loginTime);
    }

    /**
     * 获取用户角色
     */
    public List<String> getUserRoles(Long userId) {
        return userMapper.selectRoleCodesByUserId(userId);
    }

    /**
     * 获取用户权限
     */
    public List<String> getUserPermissions(Long userId) {
        return userMapper.selectPermissionCodesByUserId(userId);
    }

    /**
     * 检查用户名是否存在
     */
    public boolean existsByUsername(String username) {
        return userMapper.countByUsername(username) > 0;
    }
}