package com.example.assetManagementSystemServer.controller;

import com.example.assetManagementSystemServer.base.query.Items;
import com.example.assetManagementSystemServer.base.query.ListParam;
import com.example.assetManagementSystemServer.dto.BaseResponse;
import com.example.assetManagementSystemServer.entity.UserGroupRelation;
import com.example.assetManagementSystemServer.enums.ResponseStatusEnum;
import com.example.assetManagementSystemServer.service.UserGroupRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/groups/{groupId}/members")
public class UserGroupMemberController {

    @Autowired
    private UserGroupRelationService relationService;

    // 添加单个成员
    @PostMapping("/{userId}")
    public BaseResponse<UserGroupRelation> addMember(
            @PathVariable Long groupId,
            @PathVariable Long userId) {
        return BaseResponse.success(
                relationService.addUserToGroup(groupId, userId)
        );
    }

    // 批量添加成员
    @PostMapping("/batch")
    public BaseResponse<Integer> batchAddMembers(
            @PathVariable Long groupId,
            @RequestBody List<Long> userIds) {
        return BaseResponse.success(
                relationService.batchAddUsers(groupId, userIds)
        );
    }

    // 移除成员
    @DeleteMapping("/{userId}")
    public BaseResponse<String> removeMember(
            @PathVariable Long groupId,
            @PathVariable Long userId) {
        relationService.removeUserFromGroup(groupId, userId);
        return BaseResponse.success(ResponseStatusEnum.SUCCESS.getMessage());
    }

    // 分页查询组成员
    @GetMapping
    public BaseResponse<Items<UserGroupRelation>> listMembers(
            @PathVariable Long groupId,
            @Valid ListParam param) {
        return BaseResponse.success(
                relationService.listGroupMembers(groupId, param)
        );
    }
}