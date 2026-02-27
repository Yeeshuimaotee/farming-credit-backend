package com.example.farmingcreditbackend.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 店铺表实体类
 */
@Data
@TableName("store")
public class Store {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("store_name")
    private String storeName;

    @TableField("store_code")
    private String storeCode;

    @TableField("owner_id")
    private Long ownerId;

    @TableField("address")
    private String address;

    @TableField("phone")
    private String phone;

    @TableField("contact_person")
    private String contactPerson;

    @TableField("logo_url")
    private String logoUrl;

    @TableField("description")
    private String description;

    @TableField("status")
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}