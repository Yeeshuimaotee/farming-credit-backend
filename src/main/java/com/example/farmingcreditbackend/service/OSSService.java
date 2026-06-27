package com.example.farmingcreditbackend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface OSSService {

    /**
     * 上传文件到OSS
     */
    String uploadFile(MultipartFile file, String folder) throws IOException;

    /**
     * 删除OSS中的文件
     */
    void deleteFile(String fileUrl);
}
