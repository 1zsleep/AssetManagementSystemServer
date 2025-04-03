package com.example.assetManagementSystemServer.controller.supplier;

import com.example.assetManagementSystemServer.base.query.Items;
import com.example.assetManagementSystemServer.base.query.ListParam;
import com.example.assetManagementSystemServer.entity.supplier.Supplier;
import com.example.assetManagementSystemServer.service.supplier.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/suppliers")
public class SupplierController {
    private final SupplierService supplierService;

    @GetMapping
    public Items<Supplier> getSuppliers(ListParam listParam) {
        return supplierService.list(listParam);
    }

    @PatchMapping
    public void updateSupplier(@RequestBody Supplier supplier) {
        supplierService.updateSupplier(supplier);
    }

    @PostMapping
    public void createSupplier(@RequestBody Supplier supplier) {
        supplierService.createSupplier(supplier);
    }

    @PatchMapping("/approval")
    public void Approval(@RequestBody Long id) {
        supplierService.Approval(id);
    }

    @PatchMapping("/blacklists")
    public void blacklists(@RequestBody Long id) {
        supplierService.blacklists(id);
    }

    @PatchMapping("/failure-to-approve")
    public void FailureToApprove(@RequestBody Long id) {
        supplierService.FailureToApprove(id);
    }
}
