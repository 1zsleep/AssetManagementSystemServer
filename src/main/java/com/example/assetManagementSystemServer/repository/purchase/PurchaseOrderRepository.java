package com.example.assetManagementSystemServer.repository.purchase;

import com.example.assetManagementSystemServer.base.repository.BaseRepository;
import com.example.assetManagementSystemServer.entity.purchase.PurchaseOrder;

public interface PurchaseOrderRepository extends BaseRepository<PurchaseOrder, Long>{
    PurchaseOrder findFirstById(Long id);
}
