package com.example.assetManagementSystemServer.repository.supplier;

import com.example.assetManagementSystemServer.base.repository.BaseRepository;
import com.example.assetManagementSystemServer.entity.supplier.Supplier;

public interface SupplierRepository extends BaseRepository<Supplier, Long> {
    Supplier findFirstById(Long id);

}
