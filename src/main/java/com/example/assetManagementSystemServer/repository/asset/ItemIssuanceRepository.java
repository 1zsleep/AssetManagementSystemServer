package com.example.assetManagementSystemServer.repository.asset;

import com.example.assetManagementSystemServer.base.repository.BaseRepository;
import com.example.assetManagementSystemServer.entity.asset.ItemIssuance;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemIssuanceRepository extends BaseRepository<ItemIssuance, Long> {

    @Query("SELECT COALESCE(SUM(i.quantity), 0) " +
            "FROM ItemIssuance i " +
            "WHERE i.userId = :userId " +
            "AND i.itemId = :itemId " +
            "AND i.issueYear = :year")
    Integer calculateAnnualUsage(
            @Param("userId") Long userId,
            @Param("itemId") Long itemId,
            @Param("year") int year
    );
}
