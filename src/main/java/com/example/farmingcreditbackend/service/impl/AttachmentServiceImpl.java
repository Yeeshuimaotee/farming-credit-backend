package com.example.farmingcreditbackend.service.impl;

import com.example.farmingcreditbackend.entity.Attachment;
import com.example.farmingcreditbackend.mapper.AttachmentMapper;
import com.example.farmingcreditbackend.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 附件服务实现类
 */
@Service
public class AttachmentServiceImpl implements AttachmentService {
    
    @Autowired
    private AttachmentMapper attachmentMapper;
    
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;
    
    @Override
    @Transactional
    public Attachment uploadFile(MultipartFile file, String businessType, Long businessId, Long uploaderId, String uploaderName) {
        if (file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }
        
        String originalFileName = file.getOriginalFilename();
        String fileExt = originalFileName != null && originalFileName.contains(".") 
                ? originalFileName.substring(originalFileName.lastIndexOf(".")) 
                : "";
        
        String fileKey = UUID.randomUUID().toString().replace("-", "") + fileExt;
        
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            Path filePath = uploadPath.resolve(fileKey);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            Attachment attachment = new Attachment();
            attachment.setFileName(originalFileName);
            attachment.setFileKey(fileKey);
            attachment.setFileUrl("/api/attachments/" + fileKey);
            attachment.setFileSize(file.getSize());
            attachment.setFileType(file.getContentType());
            attachment.setFileExt(fileExt);
            attachment.setBucketName("local");
            attachment.setBusinessType(businessType);
            attachment.setBusinessId(businessId);
            attachment.setUploaderId(uploaderId);
            attachment.setUploaderName(uploaderName);
            attachment.setIsTemp(0);
            attachment.setCreateTime(LocalDateTime.now());
            
            attachmentMapper.insert(attachment);
            
            return attachment;
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败：" + e.getMessage());
        }
    }
    
    @Override
    public List<Attachment> getAttachmentsByBusiness(String businessType, Long businessId) {
        return attachmentMapper.selectByBusiness(businessType, businessId);
    }
    
    @Override
    @Transactional
    public void deleteAttachment(Long attachmentId) {
        Attachment attachment = attachmentMapper.selectById(attachmentId);
        if (attachment != null) {
            try {
                Path filePath = Paths.get(uploadDir, attachment.getFileKey());
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }
            } catch (IOException e) {
                System.err.println("删除文件失败：" + e.getMessage());
            }
            
            attachmentMapper.deleteById(attachmentId);
        }
    }
    
    @Override
    @Transactional
    public void deleteAttachments(List<Long> attachmentIds) {
        for (Long id : attachmentIds) {
            deleteAttachment(id);
        }
    }
    
    @Override
    @Transactional
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanExpiredTempFiles() {
        System.out.println("=== 清理过期临时文件 ===");
        
        List<Attachment> expiredFiles = attachmentMapper.selectExpiredTempFiles(100);
        
        for (Attachment attachment : expiredFiles) {
            try {
                Path filePath = Paths.get(uploadDir, attachment.getFileKey());
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }
                attachmentMapper.deleteById(attachment.getId());
            } catch (IOException e) {
                System.err.println("删除临时文件失败：" + e.getMessage());
            }
        }
        
        System.out.println("=== 清理完成，共删除 " + expiredFiles.size() + " 个文件 ===");
    }
}
