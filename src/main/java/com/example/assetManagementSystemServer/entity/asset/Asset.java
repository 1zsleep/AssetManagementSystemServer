// Asset.java
package com.example.assetManagementSystemServer.entity.asset;

import com.example.assetManagementSystemServer.enums.AssetType;
import com.example.assetManagementSystemServer.enums.Visibility;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 资产核心实体
 * 对应数据库 asset 表，用于管理资产的元数据和权限规则
 */
@Data
@Entity
@ToString(exclude = "files")
@Table(name = "asset")
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 私有资产拥有者ID
     * 当 visibility=PRIVATE 时必填
     */
    @Column(name = "owner_user_id")
    private Long ownerUserId;

    /**
     * 群组资产所属群组ID
     * 当 visibility=GROUP 时必填
     */
    @Column(name = "owner_group_id")
    private Long ownerGroupId;

    /**
     * 资产类型（枚举）
     * 示例值：FILE-文件, DEVICE-设备, BOOK-书籍
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "asset_type", nullable = false)
    private AssetType assetType;

    /**
     * 可见性规则（枚举）
     * 控制资产访问权限：PUBLIC-公开, GROUP-群组, PRIVATE-私有
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility visibility;

    /**
     * 创建时间（自动填充）
     * 由数据库自动生成，不可更新
     */
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * 最后更新时间（自动更新）
     * 每次实体更新时自动刷新
     */
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * 关联资源总数
     * 需通过应用层逻辑维护（如文件上传/删除时增减）
     */
    @Column(name = "related_resource_count", nullable = false)
    private Integer relatedResourceCount = 0;

    /**
     * 乐观锁版本号
     * 每次更新时自动递增，用于并发控制
     */
    @Version
    private Integer version;


}