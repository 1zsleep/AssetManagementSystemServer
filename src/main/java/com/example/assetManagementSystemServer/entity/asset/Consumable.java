package com.example.assetManagementSystemServer.entity.asset;

import jakarta.persistence.*;
import lombok.Data;


/**
 * 耗材表实体类，对应数据库表 consumables
 */
@Entity
@Table(name = "consumables")
@Data
public class Consumable {

    /**
     * 耗材唯一标识 (INT)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "consumable_id")
    private Long consumableId;

    /**
     * 耗材名称（如A4纸、墨盒）
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * 耗材类型（如文具、办公用品）
     */
    @Column(name = "type")
    private String type;

    /**
     * 供应商
     */
    @Column(name = "supplier")
    private String supplier;

    /**
     * 总库存数量
     */
    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    /**
    * 计量单位（如个、箱、包）
     */
    @Column(name = "unit")
    private String unit;

    /**
     * 消耗统计
     */
    @Column(name = "consumption_statistics")
    private Long consumptionStatistics;

}