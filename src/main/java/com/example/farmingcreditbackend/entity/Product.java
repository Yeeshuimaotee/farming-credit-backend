package com.example.farmingcreditbackend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String productCode;
    private String productName;
    private Long storeId;
    private Long categoryId;
    private String brand;
    private String specification;
    private String unit;
    private BigDecimal costPrice;
    private BigDecimal salePrice;
    private BigDecimal marketPrice;
    private Integer stock;
    private Integer minStock;
    private Integer maxStock;
    private Integer safetyStock;
    private String imageUrl;
    private String imageGallery; // JSON
    private String description;
    private Integer isHot;
    private Integer isRecommend;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}