// AssetFile.java
package com.example.assetManagementSystemServer.entity.asset;

import com.example.assetManagementSystemServer.enums.BucketType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import jakarta.persistence.*;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

/**
 * 资产文件实体
 * 对应数据库 asset_file 表，记录文件物理存储信息
 */
@Data
@Entity
@Table(name = "asset_file")
public class AssetFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 关联的资产（多对一）
     * 通过 asset_id 外键关联到 Asset 表
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    @JsonIgnore
    private Asset asset;

    /**
     * 存储桶类型（枚举）
     * 继承自关联资产的 visibility 字段：
     * PUBLIC -> PUBLIC
     * GROUP -> GROUP
     * PRIVATE -> PRIVATE
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "bucket_type", nullable = false)
    private BucketType bucketType;

    /**
     * 原始文件名（含扩展名）
     * 示例：季度报告.pdf
     */
    @Column(name = "file_name", nullable = false)
    private String fileName;

    /**
     * 文件MIME类型
     * 示例：application/pdf, image/png
     */
    @Column(name = "file_type", nullable = false)
    private String fileType;

    /**
     * 文件大小（单位：字节）
     */
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    /**
     * 文件在云存储中的完整路径
     * 示例：groups/456/reports/Q1.pdf
     */
    @Column(name = "cos_key", nullable = false)
    private String cosKey;

    /**
     * 上传者用户ID
     * 记录文件上传操作的主体
     */
    @Column(name = "upload_user_id", nullable = false)
    private Long uploadUserId;

    /**
     * 创建时间（自动填充）
     * 由数据库自动生成，不可更新
     */
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}