// AssetRepository.java
package com.example.assetManagementSystemServer.repository.asset;

import com.example.assetManagementSystemServer.base.repository.BaseRepository;
import com.example.assetManagementSystemServer.entity.asset.Asset;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends BaseRepository<Asset, Long> {

    /**
     * 资产名称唯一性校验
     * @param assetName 需要检查的资产名称
     * @return 是否存在同名资产
     */
    default boolean existsByAssetName(String assetName) {
        return count((root, query, cb) ->
                cb.equal(root.get("assetName"), assetName)
        ) > 0;
    }

    /**
     * 原子更新资源计数器
     * @param assetId 目标资产ID
     * @param delta 增减数量（正数增加，负数减少）
     */
    @Modifying
    @Query("UPDATE Asset SET relatedResourceCount = relatedResourceCount + :delta WHERE id = :assetId")
    void updateResourceCount(@Param("assetId") Long assetId, @Param("delta") int delta);


}