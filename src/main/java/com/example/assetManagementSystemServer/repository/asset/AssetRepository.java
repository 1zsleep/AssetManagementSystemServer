package com.example.assetManagementSystemServer.repository.asset;

import com.example.assetManagementSystemServer.entity.asset.Asset;
import com.example.assetManagementSystemServer.base.repository.BaseRepository;
import com.example.assetManagementSystemServer.enums.AssetType;
import com.example.assetManagementSystemServer.enums.Visibility;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends BaseRepository<Asset, Long> {

    /**
     * 根据可见性类型查询资产（带悲观锁）
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Asset a WHERE " +
            "a.visibility = :visibility AND " +
            "(:visibility = 'PRIVATE' AND a.ownerUserId = :ownerId) OR " +
            "(:visibility = 'GROUP' AND a.ownerGroupId = :ownerId) OR " +
            "(:visibility = 'PUBLIC' AND a.ownerUserId IS NULL AND a.ownerGroupId IS NULL) AND " +
            "a.assetType = :assetType")
    Asset findWithLockByVisibilityAndOwner(
            @Param("visibility") Visibility visibility,
            @Param("ownerId") Long ownerId,
            @Param("assetType") AssetType assetType);
}
