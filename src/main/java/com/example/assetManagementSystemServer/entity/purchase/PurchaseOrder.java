package com.example.assetManagementSystemServer.entity.purchase;

import com.example.assetManagementSystemServer.converter.JsonConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "purchase_order")
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //资产类型
    @Column(name = "asset_type")
    private String assetType;
    //资产名字
    @Column(name = "asset_name")
    private String assetName;
    //采购日期
    @CreationTimestamp
    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;
    //单价
    @Column(name = "unit_price")
    private Double unitPrice;
    //数量
    @Column(name = "quantity")
    private Integer quantity;
    //供应商
    @Column(name = "supplier_name")
    private String supplierName;
    //供应商id
    @Column(name = "supplier_id")
    private Long supplierId;
    //总价
    @Column(name = "total_price")
    private Double totalPrice;
    //状态
    @Column(name = "status")
    private String status;
    //货币
    @Column(name = "currency")
    private String currency;
    //归档日期
    @Column(name = "archive_date")
    private LocalDateTime archiveDate;
    //资产属性
    @Convert(converter = JsonConverter.class)
    @Column(name = "asset_attributes")
    private Map<String, Object> assetAttributes = new HashMap<>();
}
