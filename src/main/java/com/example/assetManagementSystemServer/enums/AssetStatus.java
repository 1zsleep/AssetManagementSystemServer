// src/main/java/com/example/assetManagementSystemServer/enums/AssetStatus.java
package com.example.assetManagementSystemServer.enums;

import lombok.Getter;

@Getter
public enum AssetStatus {
    IDLE("闲置"),
    IN_USE("使用中"),
    MAINTENANCE("维修中"),
    RETIRED("已报废");

    private final String description;

    AssetStatus(String description) {
        this.description = description;
    }
}