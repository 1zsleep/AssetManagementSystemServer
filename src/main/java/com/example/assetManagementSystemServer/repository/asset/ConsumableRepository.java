package com.example.assetManagementSystemServer.repository.asset;

import com.example.assetManagementSystemServer.base.repository.BaseRepository;
import com.example.assetManagementSystemServer.entity.asset.Consumable;

public interface ConsumableRepository extends BaseRepository<Consumable, Long> {
    Consumable findByConsumableId(Long id);

    Consumable findFirstByConsumableId(Long assetId);

    Consumable findFirstByName(String name);
}
