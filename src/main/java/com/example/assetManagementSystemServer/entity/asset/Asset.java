// src/main/java/com/example/.../entity/asset/Asset.java
package com.example.assetManagementSystemServer.entity.asset;

import com.example.assetManagementSystemServer.enums.BucketType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "asset")
public class Asset {
    //资产ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //资产名称
    @Column(nullable = false, length = 100)
    private String assetName;

    //详细描述
    @Column(columnDefinition = "TEXT")
    private String description;

    //资产类型（DEVICE/VEHICLE/EQUIPMENT等）
    @Column(nullable = false, length = 30)
    private String assetType;

    //状态
    @Column(nullable = false, length = 20)
    private String status;

    //责任人ID
    @Column(name = "owner_user_id")
    private Long ownerUserId;

    //所属组ID
    @Column(name = "owner_group_id")
    private Long ownerGroupId;

    //存放位置
    @Column(nullable = false, length = 200)
    private String location;

    //采购日期
    private LocalDateTime purchaseDate;

    //采购价格
    @Column(precision = 12, scale = 2)
    private BigDecimal purchasePrice;

    //创建时间
    @Column(nullable = false)
    private LocalDateTime createdAt;

    //最后更新时间
    @Column
    private LocalDateTime updatedAt;

    // 关联资源总数
    @Column(nullable = false, columnDefinition = "INT UNSIGNED DEFAULT 0")
    private Integer relatedResourceCount = 0;

    // 乐观锁版本控制
    @Version
    @Column(nullable = false, columnDefinition = "INT UNSIGNED DEFAULT 0")
    private Long version;  // 乐观锁版本号

    @Column(name = "cos_bucket_type", length = 20)
    @Enumerated(EnumType.STRING)
    private BucketType cosBucketType = BucketType.PRIVATE; // 默认私有存储
}