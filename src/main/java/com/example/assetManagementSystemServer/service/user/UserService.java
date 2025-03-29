package com.example.assetManagementSystemServer.service.user;

import com.example.assetManagementSystemServer.base.repository.BaseRepository;
import com.example.assetManagementSystemServer.base.service.BaseService;
import com.example.assetManagementSystemServer.enums.ResponseStatusEnum;
import com.example.assetManagementSystemServer.exception.BusinessException;
import com.example.assetManagementSystemServer.entity.user.User;
import com.example.assetManagementSystemServer.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * 用户服务类
 * 提供用户相关的业务逻辑
 */
@Service
@RequiredArgsConstructor
public class UserService extends BaseService<User, Long>{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResponseStatusEnum.USER_NOT_FOUND));
    }
    public User getUserByUserName(String userName) {
        return userRepository.findByUserName(userName).orElse(null);
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

    @Transactional
    public void updateUser(Long userId, User updatedUser) {
        userRepository.findById(userId)
                .map(user -> {
                    // 更新需要修改的字段
                    if (updatedUser.getUserName() != null) {
                        user.setUserName(updatedUser.getUserName());
                    }
                    if (updatedUser.getUserPassword() != null) {
                        user.setUserPassword(passwordEncoder.encode(updatedUser.getUserPassword()));
                    }
                    if (updatedUser.getRole() != null) {
                        user.setRole(updatedUser.getRole());
                    }
                    if (updatedUser.isStatus()!= user.isStatus()) {
                        user.setStatus(updatedUser.isStatus());
                    }
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new BusinessException(ResponseStatusEnum.USER_NOT_FOUND));
    }
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public void batchDeleteUsers(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ResponseStatusEnum.INVALID_PARAM);
        }
        userRepository.deleteBatch(ids);
    }


    @Transactional
    public int batchUpdateStatus(boolean status, List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ResponseStatusEnum.INVALID_PARAM);
        }
        return userRepository.updateStatusBatch(status, ids);
    }


    @Override
    protected BaseRepository<User, Long> getRepository() {
        return userRepository;
    }
}