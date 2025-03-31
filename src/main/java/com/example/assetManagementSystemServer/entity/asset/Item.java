package com.example.assetManagementSystemServer.entity.asset;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 常用物品实体
 * 对应数据库 item 表，记录可领取物品的元数据信息
 */
@Data
@Entity
@Table(name = "item")
public class Item {
    /**
     * 主键ID（自增）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 物品名称（唯一约束）
     * 示例：A4打印纸、黑色签字笔
     */
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    /**
     * 物品描述（可选）
     * 示例：80克纸张，每包500张
     */
    @Column(length = 500)
    private String description;

    /**
     * 计量单位（非空）
     * 示例：包、支、个
     */
    @Column(nullable = false, length = 20)
    private String unit;

    /**
     * 年度领取上限（每人每年最大可领数量）
     */
    @Column(name = "annual_limit", nullable = false)
    private Integer annualLimit;

    /**
     * 当前库存量（允许null，null表示无限库存）
     */
    @Column(name = "current_stock")
    private Integer currentStock;

    /**
     * 乐观锁版本号（用于并发控制）
     */
    @Version
    private Integer version;

    /**
     * 创建时间（自动填充，不可更新）
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 最后更新时间（自动更新）
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}