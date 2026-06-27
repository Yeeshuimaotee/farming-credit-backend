package com.example.farmingcreditbackend.controller;

import com.example.farmingcreditbackend.vo.Result;
import com.example.farmingcreditbackend.service.RolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/role-permission")
public class RolePermissionController {

    @Autowired
    private RolePermissionService rolePermissionService;

    /**
     * 获取角色的权限列表
     */
    @GetMapping("/get-permissions/{roleId}")
    public Result<List<Long>> getRolePermissions(@PathVariable Long roleId) {
        List<Long> permissionIds = rolePermissionService.getRolePermissions(roleId);
        return Result.success(permissionIds);
    }

    /**
     * 保存角色的权限配置
     */
    @PostMapping("/save-permissions/{roleId}")
    public Result<Void> saveRolePermissions(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        rolePermissionService.saveRolePermissions(roleId, permissionIds);
        return Result.success();
    }
}
