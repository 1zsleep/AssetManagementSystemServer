package com.example.assetManagementSystemServer.service;

import com.example.assetManagementSystemServer.base.query.Items;
import com.example.assetManagementSystemServer.base.query.ListParam;
import com.example.assetManagementSystemServer.base.service.BaseService;
import com.example.assetManagementSystemServer.entity.user.User;
import com.example.assetManagementSystemServer.entity.user.UserGroup;
import com.example.assetManagementSystemServer.entity.user.UserGroupRelation;
import com.example.assetManagementSystemServer.entity.user.UserGroupRelationId;
import com.example.assetManagementSystemServer.enums.ResponseStatusEnum;
import com.example.assetManagementSystemServer.exception.BusinessException;
import com.example.assetManagementSystemServer.repository.UserGroupRelationRepository;
import com.example.assetManagementSystemServer.repository.UserGroupRepository;
import com.example.assetManagementSystemServer.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserGroupRelationService extends BaseService<UserGroupRelation, UserGroupRelationId> {

    private final UserGroupRelationRepository relationRepository;
    private final UserRepository userRepository;
    private final UserGroupRepository groupRepository;

    public UserGroupRelationService(UserGroupRelationRepository relationRepository,
                                    UserRepository userRepository,
                                    UserGroupRepository groupRepository) {
        this.relationRepository = relationRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    protected UserGroupRelationRepository getRepository() {
        return relationRepository;
    }

    @Transactional
    public UserGroupRelation addUserToGroup(Long groupId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResponseStatusEnum.USER_NOT_FOUND));

        UserGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(ResponseStatusEnum.GROUP_NOT_FOUND));

        UserGroupRelationId relationId = new UserGroupRelationId(groupId, userId);
        if (relationRepository.existsById(relationId)) {
            throw new BusinessException(ResponseStatusEnum.USER_ALREADY_IN_GROUP);
        }

        UserGroupRelation relation = new UserGroupRelation();
        relation.setId(relationId);
        relation.setGroup(group);
        relation.setUser(user);
        groupRepository.changeMemberCount(groupId, 1);
        return relationRepository.save(relation);
    }

    @Transactional
    public void removeUserFromGroup(Long groupId, Long userId) {
        UserGroupRelationId relationId = new UserGroupRelationId(groupId, userId);
        if (!relationRepository.existsById(relationId)) {
            throw new BusinessException(ResponseStatusEnum.USER_NOT_IN_GROUP);
        }

        relationRepository.deleteById(relationId);
        groupRepository.changeMemberCount(groupId, -1);
    }

    public Items<UserGroupRelation> listGroupMembers(Long groupId, ListParam param) {
        Specification<UserGroupRelation> spec = (root, query, cb) ->
                cb.equal(root.get("group").get("id"), groupId);

        Page<UserGroupRelation> page = relationRepository.findAll(spec, buildPageRequest(param));
        return buildResult(page, param.isCount());
    }

    @Transactional
    public int batchAddUsers(Long groupId, List<Long> userIds) {
        UserGroup group = groupRepository.getReferenceById(groupId);

        List<User> existUsers = userRepository.findAllById(userIds);
        if (existUsers.size() != userIds.size()) {
            throw new BusinessException(ResponseStatusEnum.USER_NOT_FOUND);
        }

        List<UserGroupRelation> relations = userIds.stream()
                .filter(userId -> !relationExists(groupId, userId))
                .map(userId -> {
                    UserGroupRelation relation = new UserGroupRelation();
                    relation.setId(new UserGroupRelationId(groupId, userId));
                    relation.setGroup(group);
                    relation.setUser(userRepository.getReferenceById(userId));
                    return relation;
                })
                .collect(Collectors.toList());

        int addedCount = relationRepository.saveAll(relations).size();
        if (addedCount > 0) {
            groupRepository.changeMemberCount(groupId, addedCount);
        }
        return addedCount;
    }

    private boolean relationExists(Long groupId, Long userId) {
        return relationRepository.existsById(new UserGroupRelationId(groupId, userId));
    }


}