// src/main/java/com/example/.../entity/asset/AssetFile.java
package com.example.assetManagementSystemServer.entity.asset;

import com.example.assetManagementSystemServer.enums.BucketType;
import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "asset_file")
public class AssetFile {
    //文件ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //关联资产ID
    @Column(name = "asset_id", nullable = false)
    private Long assetId;

    //COS存储路径
    @Column(nullable = false, length = 255)
    private String cosKey;

    //原始文件名
    @Column(nullable = false, length = 100)
    private String fileName;

    //MIME类型（image/jpeg, video/mp4等）
    @Column(nullable = false, length = 50)
    private String fileType;

    //上传者ID
    @Column(name = "upload_user_id", nullable = false)
    private Long uploadUserId;

    //文件大小（字节）
    @Column(nullable = false)
    private Long fileSize;

    //存储类型（STANDARD/ARCHIVE）
    @Column(nullable = false, length = 20)
    private String storageClass = "STANDARD";

    //所存储的桶
    @Column(name = "cos_bucket_type", length = 20)
    @Enumerated(EnumType.STRING)
    private BucketType cosBucketType;

    //上传时间
    @Column(nullable = false)
    private LocalDateTime createdAt;


}