package com.example.assetManagementSystemServer.controller.user;

import com.example.assetManagementSystemServer.base.query.Items;
import com.example.assetManagementSystemServer.base.query.ListParam;

import com.example.assetManagementSystemServer.base.BaseResponse;

import com.example.assetManagementSystemServer.dto.BatchUpdateStatusDTO;
import com.example.assetManagementSystemServer.entity.asset.AssetFile;
import com.example.assetManagementSystemServer.entity.user.User;
import com.example.assetManagementSystemServer.enums.ResponseStatusEnum;
import com.example.assetManagementSystemServer.enums.Visibility;
import com.example.assetManagementSystemServer.service.asset.AssetFileService;
import com.example.assetManagementSystemServer.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final AssetFileService assetFileService;
    @GetMapping("/list")
    public Items<User> getUsers(ListParam listParam) {
        return userService.list(listParam);
    }

    @PatchMapping
    public ResponseEntity<BaseResponse<String>> updateUser(@RequestBody User user) {
        try {
            userService.updateUser(user.getId(), user);
            return ResponseEntity.ok(BaseResponse.success("更新成功"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    // 批量更新状态接口
    @PostMapping("/batch-status")
    public ResponseEntity<BaseResponse<Integer>> batchUpdateStatus(
            @Valid @RequestBody BatchUpdateStatusDTO dto
    ) {
        int count = userService.batchUpdateStatus(dto.getStatus(), dto.getIds());
        return ResponseEntity.ok(BaseResponse.success(count));
    }

    @PostMapping("/batch-delete")
    public ResponseEntity<BaseResponse<String>> batchDelete(
            @Valid @RequestBody List<Integer> ids
    ) {
        userService.batchDeleteUsers(ids);
        return ResponseEntity.ok(BaseResponse.success("删除成功"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<String>> deleteUser(@PathVariable long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(BaseResponse.success("删除成功"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 用户注册
     * @param user 用户对象
     * @return 注册成功消息
     */
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<User>> register(@Valid @RequestBody User user) {
        User registeredUser = userService.insertUser(user);
        return ResponseEntity.ok(BaseResponse.success(registeredUser));
    }

    /**
     * 更改头像
     */
    @PostMapping("/avatar")
    public BaseResponse<ResponseStatusEnum> updateAvatar(
            @RequestParam("file") MultipartFile file
    ) {
        userService.uploadAvatar(file);
        return BaseResponse.success(ResponseStatusEnum.SUCCESS);
    }

    /**
     * 获得头像地址
     */
    @GetMapping("/avatar")
    public String getAvatar() {
        return userService.getAvatarUrl();
    }
}
