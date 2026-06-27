package com.example.farmingcreditbackend.service;

import java.util.List;

public interface RolePermissionService {

    /**
     * 获取角色的权限列表
     */
    List<Long> getRolePermissions(Long roleId);

    /**
     * 保存角色的权限配置
     */
    void saveRolePermissions(Long roleId, List<Long> permissionIds);
}
