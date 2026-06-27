package com.example.farmingcreditbackend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("role_permission")
public class RolePermission {

    private Long id;
    private Long roleId;
    private Long permissionId;
    private Long createdBy;
    private Long updatedBy;
    private java.time.LocalDateTime createTime;
    private java.time.LocalDateTime updateTime;
}
