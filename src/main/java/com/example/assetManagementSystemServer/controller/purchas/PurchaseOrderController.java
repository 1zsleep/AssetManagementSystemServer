package com.example.assetManagementSystemServer.controller.purchas;

import com.example.assetManagementSystemServer.base.BaseResponse;
import com.example.assetManagementSystemServer.base.query.Items;
import com.example.assetManagementSystemServer.base.query.ListParam;
import com.example.assetManagementSystemServer.dto.MonthlyTotalPrice;
import com.example.assetManagementSystemServer.entity.purchase.PurchaseOrder;
import com.example.assetManagementSystemServer.enums.ResponseStatusEnum;
import com.example.assetManagementSystemServer.service.purchase.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/purchaseOrder")
public class PurchaseOrderController {
    private final PurchaseOrderService purchaseOrderService;

    @GetMapping
    public Items<PurchaseOrder> getPurchaseOrder(ListParam listParam) {
        return purchaseOrderService.list(listParam);
    }

    @PostMapping
    public void savePurchaseOrder(@RequestBody PurchaseOrder purchaseOrder) {
        purchaseOrderService.savePurchaseOrder(purchaseOrder);
    }


    @PatchMapping("/approval")
    public void approval(@RequestBody Long id)  {
        purchaseOrderService.approval(id);
    }

    @PatchMapping("/reject")
    public void reject(@RequestBody Long id)  {
        purchaseOrderService.reject(id);
    }

    @GetMapping("/monthlyTotalPrice")
    public List<MonthlyTotalPrice> getMonthlyTotalPrice() {
        return purchaseOrderService.getMonthlyArchivedTotalPrice();
    }
}
