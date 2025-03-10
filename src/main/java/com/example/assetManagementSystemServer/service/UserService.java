package com.example.assetManagementSystemServer.service;

import com.example.assetManagementSystemServer.dto.UserDTO;
import com.example.assetManagementSystemServer.enums.ResponseStatusEnum;
import com.example.assetManagementSystemServer.exception.BusinessException;
import com.example.assetManagementSystemServer.pojo.User;
import com.example.assetManagementSystemServer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
/**
 * 用户服务类
 * 提供用户相关的业务逻辑
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 插入用户
     * @param user 用户信息
     * @return 插入后的用户
     */
    public User insertUser(User user) {
        if (userRepository.findByUserName(user.getUserName()).isPresent()) {
            throw new BusinessException(ResponseStatusEnum.USER_EXISTS); // 用户名已存在
        }
        // 加密用户密码
        user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
        // 设置创建时间
        user.setCreatedAt(Date.valueOf(LocalDate.now()));

        // 保存用户到数据库
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

    public Page<UserDTO> getUsers(Pageable pageable) {
        return userRepository.findUsers(pageable);
    }
}