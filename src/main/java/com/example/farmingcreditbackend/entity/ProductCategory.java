package com.example.farmingcreditbackend.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品分类表实体
 */
@Data
@TableName("product_category")
public class ProductCategory {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("category_name")
    private String categoryName;
    
    @TableField("category_code")
    private String categoryCode;
    
    @TableField("parent_id")
    private Long parentId; // 父分类 ID，0 表示顶级
    
    @TableField("icon")
    private String icon;
    
    @TableField("description")
    private String description;
    
    @TableField("sort_order")
    private Integer sortOrder;
    
    @TableField("status")
    private Integer status; // 0-禁用，1-启用
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
