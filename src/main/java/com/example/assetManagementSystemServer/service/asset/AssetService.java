// AssetService.java
package com.example.assetManagementSystemServer.service.asset;

import com.example.assetManagementSystemServer.base.query.Items;
import com.example.assetManagementSystemServer.base.query.ListParam;
import com.example.assetManagementSystemServer.base.service.BaseService;
import com.example.assetManagementSystemServer.entity.asset.Asset;
import com.example.assetManagementSystemServer.entity.asset.AssetFile;
import com.example.assetManagementSystemServer.enums.ResponseStatusEnum;
import com.example.assetManagementSystemServer.exception.BusinessException;
import com.example.assetManagementSystemServer.repository.asset.AssetRepository;
import com.example.assetManagementSystemServer.service.user.UserGroupService;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AssetService extends BaseService<Asset, Long> {

    private final AssetRepository assetRepository;
    private final AssetFileService fileService;
    private final UserGroupService userGroupService;

    @Override
    protected AssetRepository getRepository() {
        return assetRepository;
    }

    /**
     * 创建资产（带唯一性校验和乐观锁初始化）
     * @param asset 资产对象（需包含基本字段）
     * @return 创建后的完整资产对象
     */
    @Transactional
    public Asset createAsset(Asset asset) {
        // 唯一性校验（资产名称）
        if (assetRepository.existsByAssetName(asset.getAssetName())) {
            throw new BusinessException(ResponseStatusEnum.ASSET_NAME_EXISTS);
        }

        // 初始化系统字段
        asset.setCreatedAt(LocalDateTime.now());
        asset.setUpdatedAt(LocalDateTime.now());
        asset.setRelatedResourceCount(0);
        asset.setVersion(0L); // 使用Long类型版本号

        return assetRepository.save(asset);
    }

    /**
     * 更新资产（带乐观锁校验和审计记录）
     * @param assetId 要更新的资产ID
     * @param updateAsset 包含新数据的资产对象
     * @return 更新后的资产对象
     */
    @Transactional
    public Asset updateAsset(Long assetId, Asset updateAsset) {
        return assetRepository.findById(assetId).map(asset -> {
            // 严格版本校验
            if (!asset.getVersion().equals(updateAsset.getVersion())) {
                throw new BusinessException(ResponseStatusEnum.CONCURRENT_MODIFICATION);
            }

            // 更新可修改字段
            updateAllowedFields(asset, updateAsset);

            // 更新审计字段
            asset.setUpdatedAt(LocalDateTime.now());

            return assetRepository.save(asset);
        }).orElseThrow(() -> new BusinessException(ResponseStatusEnum.ASSET_NOT_FOUND));
    }

    /**
     * 删除资产及其所有关联文件（事务性操作）
     * @param assetId 要删除的资产ID
     */
    @Transactional
    public void deleteAsset(Long assetId) {
        // 获取关联文件ID列表（触发删除操作前获取）
        List<Long> fileIds = fileService.listFiles(assetId, new ListParam(0, 0, false, null, null))
                .items().stream()
                .map(AssetFile::getId)
                .toList();

        // 批量删除文件（触发资源计数更新）
        if (!fileIds.isEmpty()) {
            fileService.deleteFiles(fileIds);
        }

        // 删除资产记录
        assetRepository.deleteById(assetId);
    }

    /**
     * 分页查询资源（带存储桶权限校验）
     * @param param       分页参数
     * @param currentUser 当前用户ID
     */
    public Items<Asset> listResources(ListParam param, Long currentUser) {
        // 构建基础查询条件
        Specification<Asset> baseSpec = getRepository().parseFilter(param.getFilter());

        // 添加存储桶权限条件
        Specification<Asset> permissionSpec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 必须匹配存储桶类型
            predicates.add(cb.equal(root.get("cosBucketType"), param.getBucketType()));

            // 根据存储桶类型添加权限限制
            switch (param.getBucketType()) {
                case PRIVATE:
                    predicates.add(cb.equal(root.get("ownerUserId"), currentUser));
                    break;
                case GROUPS:
                    List<Long> groupIds = userGroupService.getUserGroups(currentUser);
                    if (groupIds.isEmpty()) {
                        return cb.disjunction(); // 无权限返回空结果
                    }
                    predicates.add(root.get("ownerGroupId").in(groupIds));
                    break;
                case PUBLIC:
                    // 无需额外权限
                    break;
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 组合查询条件
        Specification<Asset> finalSpec = baseSpec.and(permissionSpec);

        // 执行查询
        Page<Asset> pageResult = getRepository().findAll(finalSpec, buildPageRequest(param));
        return buildResult(pageResult, param.isCount());
    }

    //------------------------ 私有方法 ------------------------//

    /**
     * 更新允许修改的字段（安全更新）
     */
    private void updateAllowedFields(Asset target, Asset source) {
        if (source.getAssetName() != null) {
            // 名称修改需要重新校验唯一性
            if (!target.getAssetName().equals(source.getAssetName()) &&
                    assetRepository.existsByAssetName(source.getAssetName())) {
                throw new BusinessException(ResponseStatusEnum.ASSET_NAME_EXISTS);
            }
            target.setAssetName(source.getAssetName());
        }
        if (source.getDescription() != null) {
            target.setDescription(source.getDescription());
        }
        if (source.getStatus() != null) {
            target.setStatus(source.getStatus());
        }
        if (source.getOwnerUserId() != null) {
            target.setOwnerUserId(source.getOwnerUserId());
        }
        // 其他允许修改字段...
    }

    public Asset getById(Long assetId) {
        return assetRepository.findById(assetId)
                .orElseThrow(() -> new BusinessException(ResponseStatusEnum.ASSET_NOT_FOUND));
    }
}