package com.example.farmingcreditbackend.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 农户表实体类
 */
@Data
@TableName("farmer")
public class Farmer {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("farmer_code")
    private String farmerCode;

    @TableField("user_id")
    private Long userId;

    @TableField("farmer_name")
    private String farmerName;

    @TableField("nickname")
    private String nickname;

    @TableField("phone")
    private String phone;

    @TableField("id_card")
    private String idCard;

    @TableField("gender")
    private Integer gender;

    @TableField("birthday")
    private LocalDate birthday;

    @TableField("address")
    private String address;

    @TableField("village")
    private String village;

    @TableField("total_credit_limit")
    private BigDecimal totalCreditLimit;

    @TableField("total_debt")
    private BigDecimal totalDebt;

    @TableField("credit_score")
    private Integer creditScore;

    @TableField("credit_level")
    private String creditLevel;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("last_evaluation_time")
    private LocalDateTime lastEvaluationTime;

    @TableField("is_blacklist")
    private Integer isBlacklist;

    @TableField("remark")
    private String remark;

    @TableField("status")
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}