package com.example.assetManagementSystemServer.repository;

import com.example.assetManagementSystemServer.base.repository.BaseRepository;
import com.example.assetManagementSystemServer.entity.UserGroup;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserGroupRepository extends BaseRepository<UserGroup, Long> {

    /**
     * 自定义存在性检查（更高效的方式）
     * @param groupName 用户组名称
     * @return 是否存在同名组
     */
    default boolean existsByGroupName(String groupName) {
        return count((root, query, cb) ->
                cb.equal(root.get("groupName"), groupName)
        ) > 0;
    }

    /**
     * 根据创建人统计组数量（使用JPA Criteria API）
     *
     * @param userId 用户ID
     * @return 用户创建的组数量
     */
    default long countByCreator(Long userId) {
        return 0;
    }

    /**
     * 原生SQL实现复杂统计（示例）
     * 统计每个用户的平均创建组数量
     */
    @Query(value = "SELECT AVG(group_count) FROM (" +
            "   SELECT COUNT(*) as group_count FROM user_group GROUP BY created_by" +
            ") AS temp",
            nativeQuery = true)
    Double averageGroupsPerUser();

    boolean existsByGroupNameAndIdNot(String newGroupName, Long groupId);

    // 原子增加成员数量
    @Modifying
    @Query("UPDATE UserGroup SET memberCount = memberCount + :delta WHERE id = :groupId")
    void changeMemberCount(@Param("groupId") Long groupId, @Param("delta") int delta);
}