package com.example.assetManagementSystemServer.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AssetStatus {
    APPLYING("申请中"),
    ASSIGNED("已分配"),
    RETURNED("已归还");
    private final String description;
}
