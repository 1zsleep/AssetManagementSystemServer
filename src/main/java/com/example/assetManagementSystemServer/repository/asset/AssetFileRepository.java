// AssetFileRepository.java
package com.example.assetManagementSystemServer.repository.asset;

import com.example.assetManagementSystemServer.entity.asset.AssetFile;
import com.example.assetManagementSystemServer.base.repository.BaseRepository;
import com.example.assetManagementSystemServer.enums.BucketType;
import com.example.assetManagementSystemServer.enums.Visibility;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetFileRepository extends BaseRepository<AssetFile, Long> {
    Long countAllByBucketType(BucketType bucketType);
}