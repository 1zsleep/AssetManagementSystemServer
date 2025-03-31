package com.example.assetManagementSystemServer.enums;

import lombok.Getter;

@Getter
public enum BucketType {
    PUBLIC,    // 公开资源
    GROUPS,    // 群组资源
    PRIVATE    // 私有资源
}