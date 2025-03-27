package com.example.assetManagementSystemServer.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class UserGroupRelationId implements Serializable {
    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "user_id")
    private Long userId;
}