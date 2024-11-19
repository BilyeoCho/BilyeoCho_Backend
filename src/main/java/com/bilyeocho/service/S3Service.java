package com.bilyeocho.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${S3_BUCKET_NAME}")
    private String bucket;

    public String uploadFile(MultipartFile file) {
        String fileName = "images/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        try {
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), null));
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3: " + e.getMessage(), e);
        }
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    public void deleteFile(String fileUrl) {
        try {
            String fileName = fileUrl.substring(fileUrl.indexOf("images/"));
            amazonS3.deleteObject(bucket, fileName);
            System.out.println("S3 file deletion successful: " + fileName);
        } catch (Exception e) {
            System.err.println("S3 file deletion failed: " + e.getMessage());
            throw new RuntimeException("Failed to delete file from S3: " + e.getMessage(), e);
        }
    }
}