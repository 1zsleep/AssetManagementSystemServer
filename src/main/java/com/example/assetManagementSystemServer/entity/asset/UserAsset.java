package com.example.assetManagementSystemServer.entity.asset;

import com.example.assetManagementSystemServer.enums.AssetStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "user_asset")
@Data
public class UserAsset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "asset_type")
    private String assetType;

    @Column(name = "asset_id")
    private Long assetId;

    @Column(name = "asset_name")
    private String assetName;

    @Column(name = "quantity")
    private Integer quantity;

    //申请中，已分配，已归还
    @Column(nullable = false)
    private String status ;

    @CreationTimestamp
    private LocalDateTime acquiredDate;

    private LocalDateTime returnedDate;

}