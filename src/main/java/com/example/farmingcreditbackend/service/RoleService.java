package com.example.farmingcreditbackend.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.farmingcreditbackend.entity.Role;
import com.example.farmingcreditbackend.mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色服务
 */
@Service
public class RoleService extends ServiceImpl<RoleMapper, Role> {

    @Autowired
    private RoleMapper roleMapper;

    /**
     * 根据用户ID获取角色编码
     */
    public List<String> getRoleCodesByUserId(Long userId) {
        return roleMapper.selectRoleCodesByUserId(userId);
    }
}