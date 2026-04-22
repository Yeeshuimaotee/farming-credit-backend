package com.example.farmingcreditbackend.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 附件表实体
 */
@Data
@TableName("attachment")
public class Attachment {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("file_name")
    private String fileName;
    
    @TableField("file_key")
    private String fileKey;
    
    @TableField("file_url")
    private String fileUrl;
    
    @TableField("file_size")
    private Long fileSize;
    
    @TableField("file_type")
    private String fileType;
    
    @TableField("file_ext")
    private String fileExt;
    
    @TableField("bucket_name")
    private String bucketName;
    
    @TableField("business_type")
    private String businessType; // ORDER-订单，FARMER-农户，PRODUCT-商品
    
    @TableField("business_id")
    private Long businessId;
    
    @TableField("uploader_id")
    private Long uploaderId;
    
    @TableField("uploader_name")
    private String uploaderName;
    
    @TableField("is_temp")
    private Integer isTemp; // 0-正式文件，1-临时文件
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("expire_time")
    private LocalDateTime expireTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
