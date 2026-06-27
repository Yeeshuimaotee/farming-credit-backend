package com.example.farmingcreditbackend.controller;

import com.example.farmingcreditbackend.service.OSSService;
import com.example.farmingcreditbackend.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/oss")
public class OSSController {

    @Autowired
    private OSSService ossService;

    /**
     * 上传文件到OSS
     */
    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("orderId") Long orderId) {
        try {
            String fileUrl = ossService.uploadFile(file, "credit-order-attachment" + "/" + orderId);
            return Result.success(fileUrl);
        } catch (IOException e) {
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 删除OSS中的文件
     */
    @DeleteMapping("/delete")
    public Result<Void> deleteFile(@RequestParam("fileUrl") String fileUrl) {
        ossService.deleteFile(fileUrl);
        return Result.success();
    }
}
