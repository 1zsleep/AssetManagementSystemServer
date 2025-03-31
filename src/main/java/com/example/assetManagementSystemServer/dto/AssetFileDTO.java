package com.example.assetManagementSystemServer.dto;

import com.example.assetManagementSystemServer.enums.BucketType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssetFileDTO {
    private String id;
    private BucketType bucketType;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private Long uploadUserId;
    private String cosKey;
    private LocalDateTime createdAt;
}
