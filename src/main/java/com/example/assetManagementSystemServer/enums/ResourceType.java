// src/main/java/com/example/.../enums/ResourceType.java
package com.example.assetManagementSystemServer.enums;

import lombok.Getter;

@Getter
public enum ResourceType {
    FILE("关联文件"),
    SUB_ASSET("子资产"),
    MAINTENANCE_RECORD("维护记录");

    private final String description;

    ResourceType(String description) {
        this.description = description;
    }
}