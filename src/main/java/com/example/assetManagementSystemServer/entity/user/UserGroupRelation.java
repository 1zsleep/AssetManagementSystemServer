package com.example.assetManagementSystemServer.entity.user;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_group_relation")
public class UserGroupRelation {
    @EmbeddedId
    private UserGroupRelationId id = new UserGroupRelationId();

    @ManyToOne
    @MapsId("groupId") // 映射到复合主键的groupId字段
    @JoinColumn(name = "group_id")
    private UserGroup group;

    @ManyToOne
    @MapsId("userId") // 映射到复合主键的userId字段
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}