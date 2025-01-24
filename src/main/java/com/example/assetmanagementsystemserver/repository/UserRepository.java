package com.example.assetmanagementsystemserver.repository;

import com.example.assetmanagementsystemserver.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // 你可以在这里定义自定义查询方法
    Optional<User> findByUserName(String username);
}