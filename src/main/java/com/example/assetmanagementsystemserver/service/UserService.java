package com.example.assetmanagementsystemserver.service;

import com.example.assetmanagementsystemserver.eume.RoleEnum;
import com.example.assetmanagementsystemserver.pojo.User;
import com.example.assetmanagementsystemserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User getUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User insertUser(User user) {
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
}