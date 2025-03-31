package com.example.assetManagementSystemServer.service.asset;

import com.example.assetManagementSystemServer.entity.asset.Asset;
import com.example.assetManagementSystemServer.enums.AssetType;
import com.example.assetManagementSystemServer.enums.ResponseStatusEnum;
import com.example.assetManagementSystemServer.enums.Visibility;
import com.example.assetManagementSystemServer.exception.BusinessException;
import com.example.assetManagementSystemServer.repository.asset.AssetRepository;
import com.example.assetManagementSystemServer.base.service.BaseService;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AssetService extends BaseService<Asset, Long> {

    private final AssetRepository assetRepository;

    @Override
    protected AssetRepository getRepository() {
        return assetRepository;
    }
    public Asset getAssetById(Long id) {
        return assetRepository.findById(id).orElseThrow(() -> new BusinessException(ResponseStatusEnum.ASSET_NOT_FOUND));
    }
    @Transactional
    public Asset saveAsset(Asset asset) {
        return assetRepository.save(asset);
    }
    /**
     * 查找或创建资产记录（带并发控制）
     * @param visibility 可见性
     * @param ownerUserId 用户ID（PRIVATE时必填）
     * @param ownerGroupId 群组ID（GROUP时必填）
     * @param assetType 资产类型
     */
    @Transactional
    public Asset findOrCreateAsset(Visibility visibility,
                                   Long ownerUserId,
                                   Long ownerGroupId,
                                   AssetType assetType) {
        validateOwnership(visibility, ownerUserId, ownerGroupId);

        Long ownerId = switch (visibility) {
            case PRIVATE -> ownerUserId;
            case GROUP -> ownerGroupId;
            case PUBLIC -> 0L;
        };

        Asset asset = assetRepository.findWithLockByVisibilityAndOwner(
                visibility, ownerId, assetType);

        if (asset == null) {
            asset = new Asset();
            asset.setVisibility(visibility);
            asset.setOwnerUserId(ownerUserId);
            asset.setOwnerGroupId(ownerGroupId);
            asset.setAssetType(assetType);
            asset = assetRepository.save(asset);
        }
        return asset;
    }

    private void validateOwnership(Visibility visibility, Long ownerUserId, Long ownerGroupId) {
        switch (visibility) {
            case PRIVATE:
                if (ownerUserId == null) {
                    throw new BusinessException(ResponseStatusEnum.INVALID_PARAM,
                            "PRIVATE资产必须指定ownerUserId");
                }
                break;
            case GROUP:
                if (ownerGroupId == null) {
                    throw new BusinessException(ResponseStatusEnum.INVALID_PARAM,
                            "GROUP资产必须指定ownerGroupId");
                }
                break;
            case PUBLIC:
                if (ownerUserId != null || ownerGroupId != null) {
                    throw new BusinessException(ResponseStatusEnum.INVALID_PARAM,
                            "PUBLIC资产不能指定所有者");
                }
                break;
        }
    }
}