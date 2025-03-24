package com.example.assetManagementSystemServer.repository;

import com.example.assetManagementSystemServer.base.repository.BaseRepository;
import com.example.assetManagementSystemServer.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问接口
 *
 */
@Repository
public interface UserRepository extends BaseRepository<User, Integer> {
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象（Optional）
     */
    Optional<User> findByUserName(String username);
    // 批量删除
    @Modifying
    @Query("DELETE FROM User u WHERE u.id IN :ids")
    void deleteBatch(@Param("ids") List<Integer> ids);

    // 批量更新状态
    @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.id IN :ids")
    int updateStatusBatch(@Param("status") boolean status, @Param("ids") List<Integer> ids);
}