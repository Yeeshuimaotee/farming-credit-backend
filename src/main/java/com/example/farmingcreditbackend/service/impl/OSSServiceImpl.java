package com.example.farmingcreditbackend.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.example.farmingcreditbackend.config.OSSConfig;
import com.example.farmingcreditbackend.service.OSSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class OSSServiceImpl implements OSSService {

    @Autowired
    private OSS ossClient;

    @Autowired
    private OSSConfig ossConfig;

    @Override
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        String fileName = folder + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        ossClient.putObject(ossConfig.getBucketName(), fileName, file.getInputStream());
        return "https://" + ossConfig.getBucketName() + "." + ossConfig.getEndpoint().replace("https://", "") + "/" + fileName;
    }

    @Override
    public void deleteFile(String fileUrl) {
        try {
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            ossClient.deleteObject(ossConfig.getBucketName(), fileName);
        } catch (OSSException e) {
            e.printStackTrace();
        }
    }
}
