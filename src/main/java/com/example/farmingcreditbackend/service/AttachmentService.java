package com.example.farmingcreditbackend.service;

import com.example.farmingcreditbackend.entity.Attachment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 附件服务接口
 */
public interface AttachmentService {
    
    /**
     * 上传文件
     */
    Attachment uploadFile(MultipartFile file, String businessType, Long businessId, Long uploaderId, String uploaderName);
    
    /**
     * 根据业务 ID 获取附件列表
     */
    List<Attachment> getAttachmentsByBusiness(String businessType, Long businessId);
    
    /**
     * 删除附件
     */
    void deleteAttachment(Long attachmentId);
    
    /**
     * 批量删除附件
     */
    void deleteAttachments(List<Long> attachmentIds);
    
    /**
     * 清理过期临时文件
     */
    void cleanExpiredTempFiles();
}
