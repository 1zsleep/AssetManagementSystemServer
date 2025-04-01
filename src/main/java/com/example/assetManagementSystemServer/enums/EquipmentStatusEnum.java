package com.example.assetManagementSystemServer.enums;

import lombok.Getter;

@Getter
public enum EquipmentStatusEnum {
    NORMAL("正常"),
    UNDER_REPAIR("使用中"),
    SCRAPPED("报废");

    private final String description;

    EquipmentStatusEnum(String description) {
        this.description = description;
    }
}