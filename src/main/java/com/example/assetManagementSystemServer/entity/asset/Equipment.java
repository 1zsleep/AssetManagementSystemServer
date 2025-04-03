package com.example.assetManagementSystemServer.entity.asset;

import com.example.assetManagementSystemServer.enums.EquipmentStatusEnum;
import com.fasterxml.jackson.databind.annotation.EnumNaming;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

/**
 * 设备表实体类，对应数据库表 equipments
 */
@Entity
@Table(name = "equipments")
@Data
public class Equipment {

    /**
     * 设备唯一标识 (INT)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "equipment_id")
    private Long equipmentId;

    /**
     * 设备名称（如笔记本电脑、投影仪）
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * 设备序列号（唯一）
     */
    @Column(name = "serial_number", unique = true)
    private String serialNumber;

    /**
     * 购买日期
     */
    @Column(name = "purchase_date")
    @CreationTimestamp
    private LocalDate purchaseDate;

    /**
     * 采购单号
     */
    @Column(name = "purchase_id")
    private Long purchaseId;

    /**
     * 设备状态（正常、使用中、报废）
     */

    @Column(name = "status")
    private String status;  // 引用独立枚举类
}