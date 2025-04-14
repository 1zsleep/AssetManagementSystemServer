package com.example.assetManagementSystemServer.repository.asset;

import com.example.assetManagementSystemServer.base.repository.BaseRepository;
import com.example.assetManagementSystemServer.entity.asset.Consumable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConsumableRepository extends BaseRepository<Consumable, Long> {
    Consumable findByConsumableId(Long id);

    Consumable findFirstByConsumableId(Long assetId);

    Consumable findFirstByName(String name);

    /**
     * 获取消耗量最高的前10个耗材
     * 根据 consumptionStatistics 字段降序排序
     */
    List<Consumable> findTop10ByOrderByConsumptionStatisticsDesc();
}
