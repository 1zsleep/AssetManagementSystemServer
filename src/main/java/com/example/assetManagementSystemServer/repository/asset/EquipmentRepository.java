package com.example.assetManagementSystemServer.repository.asset;

import com.example.assetManagementSystemServer.base.repository.BaseRepository;
import com.example.assetManagementSystemServer.entity.asset.Equipment;

public interface EquipmentRepository extends BaseRepository<Equipment, Long> {
    Equipment findByEquipmentId(Long id);

    Equipment findFirstByEquipmentId(Long assetId);

    Equipment findFirstByName(String name);

    Equipment findFirstBySerialNumber(String serialNumber);

}
