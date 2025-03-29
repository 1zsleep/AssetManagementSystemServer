// AssetFileRepository.java
package com.example.assetManagementSystemServer.repository.asset;

import com.example.assetManagementSystemServer.base.repository.BaseRepository;
import com.example.assetManagementSystemServer.entity.asset.AssetFile;

public interface AssetFileRepository extends BaseRepository<AssetFile, Long> {

    /**
     * 统计资产关联文件数（调用BaseRepository的count方法）
     * @param assetId 资产ID
     */
    default long countByAssetId(Long assetId) {
        return count((root, query, cb) ->
                cb.equal(root.get("assetId"), assetId)
        );
    }

    /**
     * 根据资产ID删除所有文件（调用BaseRepository的delete方法）
     * @param assetId 资产ID
     */
    default void deleteByAssetId(Long assetId) {
        delete((root, query, cb) ->
                cb.equal(root.get("assetId"), assetId)
        );
    }
}