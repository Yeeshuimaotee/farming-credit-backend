package com.example.farmingcreditbackend.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限表实体类
 */
@Data
@TableName("sys_permission")
public class Permission {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("permission_name")
    private String permissionName;

    @TableField("permission_code")
    private String permissionCode;

    @TableField("parent_id")
    private Long parentId;

    @TableField("menu_path")
    private String menuPath;

    @TableField("menu_icon")
    private String menuIcon;

    @TableField("menu_order")
    private Integer menuOrder;

    @TableField("permission_type")
    private String permissionType;

    @TableField("description")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}