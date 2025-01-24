package com.example.assetmanagementsystemserver.eume;

import lombok.Getter;

@Getter
public enum RoleEnum {
    ADMIN("管理员"),
    PROCURE("采购员"),
    STAFF("员工");
    private final String description;
    RoleEnum(String description) {
        this.description = description;
    }
}
