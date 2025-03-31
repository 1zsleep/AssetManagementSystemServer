package com.example.assetManagementSystemServer.controller.user;

import com.example.assetManagementSystemServer.base.query.Items;
import com.example.assetManagementSystemServer.base.query.ListParam;
import com.example.assetManagementSystemServer.base.BaseResponse;
import com.example.assetManagementSystemServer.entity.user.UserGroup;
import com.example.assetManagementSystemServer.enums.ResponseStatusEnum;
import com.example.assetManagementSystemServer.exception.BusinessException;
import com.example.assetManagementSystemServer.service.user.UserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/groups")
public class UserGroupController {

    @Autowired
    private UserGroupService groupService;

    // 创建用户组
    @PostMapping
    public BaseResponse<UserGroup> createGroup(
            @RequestParam String groupName,
            @RequestParam Long creatorId) {
        System.out.println("create group");
        return BaseResponse.success(
                groupService.createGroup(groupName, creatorId)
        );
    }

    // 删除用户组
    @DeleteMapping("/{groupId}")
    public BaseResponse<String> deleteGroup(
            @PathVariable Long groupId,
            @RequestParam Long operatorId) {
        groupService.deleteGroup(groupId, operatorId);
        return BaseResponse.success("删除成功");
    }

    // 分页查询所有组
    @GetMapping
    public Items<UserGroup> listGroups(
            @Valid ListParam param) {
        return groupService.list(param);
    }

    // 查询用户创建的组
    @GetMapping("/created-by/{userId}")
    public BaseResponse<Items<UserGroup>> listGroupsByCreator(
            @PathVariable Long userId,
            @Valid ListParam param) {
        return BaseResponse.success(
                groupService.listGroupsByCreator(userId, param)
        );
    }

    // 获取单个组详情
    @GetMapping("/{groupId}")
    public BaseResponse<UserGroup> getGroupDetail(
            @PathVariable Long groupId) {
        return BaseResponse.success(
                groupService.getRepository().findById(groupId)
                        .orElseThrow(() -> new BusinessException(ResponseStatusEnum.GROUP_NOT_FOUND))
        );
    }

    @PutMapping("/groups/{groupId}/name")
    public ResponseEntity<UserGroup> updateGroupName(
            @PathVariable Long groupId,
            @RequestParam String newName,
            @RequestParam Long currentUserId
    ) {
        UserGroup updatedGroup = groupService.updateGroupName(
                groupId,
                newName,
                currentUserId
        );
        return ResponseEntity.ok(updatedGroup);
    }

    // 获取用户所属群组列表
    @GetMapping("/user/{userId}")
    public List<Long> getUserGroups(@PathVariable Long userId) {
        return groupService.getUserGroups(userId);
    }

}