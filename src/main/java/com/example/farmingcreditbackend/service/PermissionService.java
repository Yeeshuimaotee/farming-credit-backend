package com.example.farmingcreditbackend.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.farmingcreditbackend.entity.Permission;
import com.example.farmingcreditbackend.mapper.PermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 权限服务
 */
@Service
public class PermissionService extends ServiceImpl<PermissionMapper, Permission> {

    @Autowired
    private PermissionMapper permissionMapper;

    /**
     * 根据用户ID获取权限编码
     */
    public List<String> getPermissionCodesByUserId(Long userId) {
        return permissionMapper.selectPermissionCodesByUserId(userId);
    }
}