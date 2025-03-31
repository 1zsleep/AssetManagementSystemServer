package com.example.assetManagementSystemServer.entity.asset;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

/**
 * 物品领取记录实体
 * 对应数据库 item_issuance 表，记录用户物品领取明细
 */
@Data
@Entity
@Table(name = "item_issuance")
public class ItemIssuance {
    /**
     * 主键ID（自增）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 领取用户ID（关联用户表主键）
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 物品ID（关联item表主键）
     */
    @Column(name = "item_id", nullable = false)
    private Long itemId;

    /**
     * 领取数量（必须大于0）
     */
    @Column(nullable = false)
    private Integer quantity;

    /**
     * 领取年度（存储4位数年份）
     * 示例：2024
     */
    @Column(name = "issue_year", columnDefinition = "YEAR(4)")
    private Integer issueYear;

    /**
     * 领取时间（自动记录）
     */
    @CreationTimestamp
    @Column(name = "issued_at", updatable = false)
    private LocalDateTime issuedAt;
}