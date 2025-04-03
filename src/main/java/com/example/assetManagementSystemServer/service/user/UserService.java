package com.example.assetManagementSystemServer.service.user;

import com.example.assetManagementSystemServer.base.repository.BaseRepository;
import com.example.assetManagementSystemServer.base.service.BaseService;
import com.example.assetManagementSystemServer.enums.BucketType;
import com.example.assetManagementSystemServer.enums.ResponseStatusEnum;
import com.example.assetManagementSystemServer.exception.BusinessException;
import com.example.assetManagementSystemServer.entity.user.User;
import com.example.assetManagementSystemServer.repository.user.UserRepository;
import com.example.assetManagementSystemServer.service.storage.CosService;
import com.qcloud.cos.model.ResponseHeaderOverrides;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


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
    private final CosService cosService;

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
                    if (StringUtils.isNotBlank(updatedUser.getUserName())) {
                        user.setUserName(updatedUser.getUserName());
                    }
                    if (StringUtils.isNotBlank(updatedUser.getUserPassword())) {
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
    public Long getCurrentUserId() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 从Principal中提取用户名
        String username = authentication.getName();

        // 通过用户名查询用户服务获取ID
        return getUserByUserName(username).getId();
    }

    @Override
    protected BaseRepository<User, Long> getRepository() {
        return userRepository;
    }

    //上传头像
    @Transactional
    public void uploadAvatar(MultipartFile file) {
        Long currentUserId = getCurrentUserId();
        String url = cosService.uploadFile(BucketType.PUBLIC, "avatar", file, currentUserId);
        User user = getUserById(currentUserId);
        user.setAvatarCosKey(extractCosKey(url));
        userRepository.save(user);
    }

    //头像地址
    public String getAvatarUrl() {
        Long currentUserId = getCurrentUserId();
        User user = getUserById(currentUserId);
        if (user.getAvatarCosKey() != null){
            return cosService.generateDynamicUrl(BucketType.PUBLIC, user.getAvatarCosKey().substring(user.getAvatarCosKey().lastIndexOf(".com")+5), null, 120);
        }
        return "";
    }

    private String extractCosKey(String cosUrl) {
        // 正确提取完整路径（如 groups/documents/2/20250330/filename.jpg）
        String prefix = cosService.getBucketDomain(); // 获取存储桶域名部分
        return cosUrl.replace(prefix, ""); // 移除域名保留完整路径
    }
}