package com.example.farmingcreditbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.farmingcreditbackend.entity.Role;
import com.example.farmingcreditbackend.entity.SysUserRole;
import com.example.farmingcreditbackend.exception.BusinessException;
import com.example.farmingcreditbackend.mapper.RoleMapper;
import com.example.farmingcreditbackend.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色服务
 */
@Service
public class RoleService extends ServiceImpl<RoleMapper, Role> {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    /**
     * 根据用户ID获取角色编码
     */
    public List<String> getRoleCodesByUserId(Long userId) {
        return roleMapper.selectRoleCodesByUserId(userId);
    }

    /**
     * 根据角色编码获取角色ID
     */
//    public Long getRoleIdByCode(String roleCode) {
//        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(Role::getRoleCode, roleCode);
//        Role role = this.getOne(wrapper);
//        return role != null ? role.getId() : null;
//    }

    public Long getRoleIdByCode(String roleCode) {
        return roleMapper.selectIdByCode(roleCode);
    }

    /**
     * 为用户分配角色
     * @param userId 用户ID
     * @param roleId 角色ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void assignRoleToUser(Long userId, Long roleId) {
        // 检查是否已存在该分配
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId)
                .eq(SysUserRole::getRoleId, roleId);
        if (userRoleMapper.selectCount(wrapper) > 0) {
            // 已存在，直接返回
            return;
        }

        // 创建关联记录
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        userRole.setCreateTime(LocalDateTime.now());
        userRoleMapper.insert(userRole);
    }
}