package com.example.assetManagementSystemServer.service;

import com.example.assetManagementSystemServer.base.query.Items;
import com.example.assetManagementSystemServer.base.query.ListParam;
import com.example.assetManagementSystemServer.base.service.BaseService;
import com.example.assetManagementSystemServer.entity.user.User;
import com.example.assetManagementSystemServer.entity.user.UserGroup;
import com.example.assetManagementSystemServer.enums.ResponseStatusEnum;
import com.example.assetManagementSystemServer.exception.BusinessException;
import com.example.assetManagementSystemServer.repository.UserGroupRelationRepository;
import com.example.assetManagementSystemServer.repository.UserGroupRepository;
import com.example.assetManagementSystemServer.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserGroupService extends BaseService<UserGroup, Long> {

    private final UserGroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserGroupRelationRepository relationRepository;
    private final UserService userService;

    public UserGroupService(UserGroupRepository groupRepository,
                            UserRepository userRepository, UserGroupRelationRepository relationRepository, UserService userService) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.relationRepository = relationRepository;
        this.userService = userService;
    }

    @Override
    public UserGroupRepository getRepository() {
        return groupRepository;
    }

    @Transactional
    public UserGroup createGroup(String groupName, Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new BusinessException(ResponseStatusEnum.USER_NOT_FOUND));

        if (groupRepository.existsByGroupName(groupName)) {
            throw new BusinessException(ResponseStatusEnum.GROUP_EXISTS);
        }

        UserGroup group = new UserGroup();
        group.setGroupName(groupName);
        group.setCreatedBy(creator);
        return groupRepository.save(group);
    }

    @Transactional
    public void deleteGroup(Long groupId, Long operatorId) {
        UserGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(ResponseStatusEnum.GROUP_NOT_FOUND));

        if (!userService.getUserById(operatorId).getRole().getDescription().equals("管理员") && !group.getCreatedBy().getId().equals(operatorId)) {
            throw new BusinessException(ResponseStatusEnum.ILLEGAL_OPERATION);
        }
        relationRepository.deleteByGroupId(groupId);
        groupRepository.delete(group);
    }

    public Items<UserGroup> listGroupsByCreator(Long creatorId, ListParam param) {
        Specification<UserGroup> spec = (root, query, cb) ->
                cb.equal(root.get("createdBy").get("id"), creatorId);

        Page<UserGroup> page = groupRepository.findAll(spec, buildPageRequest(param));
        return buildResult(page, param.isCount());
    }

    @Transactional
    public UserGroup updateGroupName(Long groupId, String newGroupName, Long operatorId) {
        // 1. 验证组是否存在
        UserGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(ResponseStatusEnum.GROUP_NOT_FOUND));

        // 2. 验证操作权限
        if (!group.getCreatedBy().getId().equals(operatorId)) {
            throw new BusinessException(ResponseStatusEnum.NO_UPDATE_PERMISSION);
        }

        // 3. 检查名称是否变化
        if (group.getGroupName().equals(newGroupName)) {
            return group; // 名称未改变直接返回
        }

        // 4. 检查名称唯一性（排除自己）
        if (groupRepository.existsByGroupNameAndIdNot(newGroupName, groupId)) {
            throw new BusinessException(ResponseStatusEnum.GROUP_EXISTS);
        }

        // 5. 执行更新
        group.setGroupName(newGroupName);
        return groupRepository.save(group);
    }
}