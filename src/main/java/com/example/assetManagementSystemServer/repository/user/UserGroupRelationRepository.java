package com.example.assetManagementSystemServer.repository.user;

import com.example.assetManagementSystemServer.base.repository.BaseRepository;
import com.example.assetManagementSystemServer.entity.user.UserGroupRelation;
import com.example.assetManagementSystemServer.entity.user.UserGroupRelationId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

public interface UserGroupRelationRepository extends BaseRepository<UserGroupRelation, UserGroupRelationId> {

    /**
     * 检查用户是否在组中（高性能实现）
     * @param groupId 用户组ID
     * @param userId 用户ID
     * @return 是否存在关系
     */
    default boolean existsRelation(Long groupId, Long userId) {
        return count((root, query, cb) ->
                cb.and(
                        cb.equal(root.get("group").get("id"), groupId),
                        cb.equal(root.get("user").get("id"), userId)
                )
        ) > 0;
    }

    /**
     * 批量删除组成员（带安全限制）
     * @param groupId 用户组ID
     * @param userIds 要删除的用户ID列表
     * @return 实际删除的记录数
     */
    @Transactional
    @Modifying
    default int deleteMembers(Long groupId, List<Long> userIds) {
        return (int) delete((root, query, cb) ->
                cb.and(
                        cb.equal(root.get("group").get("id"), groupId),
                        root.get("user").get("id").in(userIds)
                )
        );
    }

    /**
     * 获取组的成员数量
     * @param groupId 用户组ID
     * @return 成员总数
     */
    default long countGroupMembers(Long groupId) {
        return count((root, query, cb) ->
                cb.equal(root.get("group").get("id"), groupId)
        );
    }




    /**
     * 根据组ID和用户ID删除关系
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM UserGroupRelation r WHERE r.group.id = :groupId AND r.user.id = :userId")
    int deleteByGroupIdAndUserId(Long groupId, Long userId);

    /**
     * 根据组ID删除所有关系
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM UserGroupRelation r WHERE r.group.id = :groupId")
    int deleteByGroupId(Long groupId);

    /**
     * 检查特定关系是否存在
     */
    default boolean existsByGroupIdAndUserId(Long groupId, Long userId) {
        return count((root, query, cb) ->
                cb.and(
                        cb.equal(root.get("group").get("id"), groupId),
                        cb.equal(root.get("user").get("id"), userId)
                )
        ) > 0;
    }

    @Query("SELECT r.id FROM UserGroupRelation r WHERE r.user.id = ?1")
    List<UserGroupRelationId> findGroupIdsByUserId(Long userId);
}