package com.example.assetManagementSystemServer.repository.asset;

import com.example.assetManagementSystemServer.base.repository.BaseRepository;
import com.example.assetManagementSystemServer.entity.asset.UserAsset;

import java.util.List;

public interface UserAssetRepository extends BaseRepository<UserAsset, Long> {
     List<UserAsset> findByUserIdAndAssetIdAndAssetType(Long userId, Long assetId, String assetType);

     UserAsset findFirstById(Long id);
}
