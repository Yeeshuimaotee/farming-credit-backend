package com.example.farmingcreditbackend.controller;

import com.example.farmingcreditbackend.entity.Attachment;
import com.example.farmingcreditbackend.service.AttachmentService;
import com.example.farmingcreditbackend.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * 附件控制器
 */
@RestController
@RequestMapping("/attachments")
public class AttachmentController {
    
    @Autowired
    private AttachmentService attachmentService;
    
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;
    
    /**
     * 上传文件
     */
    @PostMapping
    public Result<Attachment> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "businessType", required = false) String businessType,
            @RequestParam(value = "businessId", required = false) Long businessId,
            @RequestParam(value = "uploaderId", required = false) Long uploaderId,
            @RequestParam(value = "uploaderName", required = false) String uploaderName) {
        
        Attachment attachment = attachmentService.uploadFile(
                file, businessType, businessId, uploaderId, uploaderName);
        
        return Result.success(attachment);
    }
    
    /**
     * 根据业务 ID 获取附件列表
     */
    @GetMapping
    public Result<List<Attachment>> getAttachments(
            @RequestParam String businessType,
            @RequestParam Long businessId) {
        
        List<Attachment> attachments = attachmentService.getAttachmentsByBusiness(businessType, businessId);
        return Result.success(attachments);
    }
    
    /**
     * 删除附件
     */
    @DeleteMapping("/{attachmentId}")
    public Result<Void> deleteAttachment(@PathVariable Long attachmentId) {
        attachmentService.deleteAttachment(attachmentId);
        return Result.success();
    }
    
    /**
     * 下载/预览文件
     */
    @GetMapping("/{fileKey}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileKey) {
        try {
            Path filePath = Paths.get(uploadDir, fileKey);
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }
            
            byte[] fileContent = Files.readAllBytes(filePath);
            
            String fileName = filePath.getFileName().toString();
            String contentType = Files.probeContentType(filePath);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"));
            headers.setContentDispositionFormData("attachment", fileName);
            
            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
