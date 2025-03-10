package com.example.assetManagementSystemServer.repository;

import com.example.assetManagementSystemServer.dto.UserDTO;
import com.example.assetManagementSystemServer.pojo.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问接口
 * 继承JpaRepository，提供基本的CRUD操作
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象（Optional）
     */
    Optional<User> findByUserName(String username);
    @Query("SELECT new com.example.assetManagementSystemServer.dto.UserDTO(u.id, u.userName, u.createdAt, u.role) FROM User u ORDER BY u.id")
    Page<UserDTO> findUsers(Pageable pageable);
}