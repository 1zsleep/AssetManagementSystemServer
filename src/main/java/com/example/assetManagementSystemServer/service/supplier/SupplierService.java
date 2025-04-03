package com.example.assetManagementSystemServer.service.supplier;

import com.example.assetManagementSystemServer.base.service.BaseService;
import com.example.assetManagementSystemServer.entity.supplier.Supplier;
import com.example.assetManagementSystemServer.repository.supplier.SupplierRepository;
import com.example.assetManagementSystemServer.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierService extends BaseService<Supplier, Long> {
    private final SupplierRepository supplierRepository;
    @Override
    protected SupplierRepository getRepository() {
        return supplierRepository;
    }

    @Transactional
    public Supplier updateSupplier(Supplier supplier) {

        return update(supplier.getId(), supplier);

    }

    public void createSupplier(Supplier supplier) {
        supplierRepository.save(supplier);
    }

    //通过审批
    @Transactional
    public void Approval(Long id){
        Supplier supplier = supplierRepository.findFirstById(id);
        supplier.setStatus("合作中");
        supplier.setStartDate(LocalDate.now());
        supplierRepository.save(supplier);
    }

    @Transactional
    public void blacklists(Long id){
        Supplier supplier = supplierRepository.findFirstById(id);
        supplier.setStatus("黑名单");
        supplierRepository.save(supplier);
    }

    @Transactional
    public void FailureToApprove(Long id){
        Supplier supplier = supplierRepository.findFirstById(id);
        supplier.setStatus("审核失败");
        supplierRepository.save(supplier);
    }

    public Supplier getSupplierById(Long supplierId) {
        return supplierRepository.findFirstById(supplierId);
    }
}
