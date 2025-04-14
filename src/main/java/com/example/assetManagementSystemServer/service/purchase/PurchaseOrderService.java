package com.example.assetManagementSystemServer.service.purchase;


import com.example.assetManagementSystemServer.base.service.BaseService;
import com.example.assetManagementSystemServer.dto.MonthlyTotalPrice;
import com.example.assetManagementSystemServer.entity.asset.Book;
import com.example.assetManagementSystemServer.entity.asset.Consumable;
import com.example.assetManagementSystemServer.entity.asset.Equipment;
import com.example.assetManagementSystemServer.entity.purchase.PurchaseOrder;
import com.example.assetManagementSystemServer.repository.purchase.PurchaseOrderRepository;
import com.example.assetManagementSystemServer.service.asset.BookService;
import com.example.assetManagementSystemServer.service.asset.ConsumableService;
import com.example.assetManagementSystemServer.service.asset.EquipmentService;
import com.example.assetManagementSystemServer.service.asset.UserAssetService;
import com.example.assetManagementSystemServer.service.supplier.SupplierService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseOrderService extends BaseService<PurchaseOrder, Long> {
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final BookService bookService;
    private final EquipmentService equipmentService;
    private final ConsumableService consumableService;
    private final SupplierService supplierService;
    private final UserAssetService userAssetService;

    @Override
    protected PurchaseOrderRepository getRepository() {
        return purchaseOrderRepository;
    }

    @Transactional
    public void savePurchaseOrder(PurchaseOrder purchaseOrder) {
        long supplierId = purchaseOrder.getSupplierId();
        purchaseOrder.setSupplierName(supplierService.getSupplierById(supplierId).getName());
        purchaseOrder.setStatus("审核中");
        purchaseOrderRepository.save(purchaseOrder);
    }

    @Transactional
    public void approval(Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findFirstById(id);
        Map<String, Object> assetAttributes = purchaseOrder.getAssetAttributes();
        switch (purchaseOrder.getAssetType()) {
            case "BOOK":
                Book book = bookService.getBookByTitle((String) assetAttributes.get("title"));
                if (book == null) {
                    book = new Book();
                    book.setTitle((String) assetAttributes.get("title"));
                    book.setAuthor((String) assetAttributes.get("author"));
                    book.setPublisher((String) assetAttributes.get("publisher"));
                    book.setIsbn((String) assetAttributes.get("isbn"));
                    book.setStockQuantity(purchaseOrder.getQuantity());
                    bookService.save(book);
                } else {
                    book.setStockQuantity(book.getStockQuantity() + purchaseOrder.getQuantity());
                    bookService.save(book);
                }
                break;
            case "EQUIPMENT":
                Equipment equipment = equipmentService.getEquipmentBySerialNumber((String) assetAttributes.get("serialNumber"));
                if (equipment == null) {
                    equipment = new Equipment();
                    equipment.setName((String) assetAttributes.get("name"));
                    equipment.setSerialNumber((String) assetAttributes.get("serialNumber"));
                    equipment.setPurchaseId(id);
                    equipment.setStatus("正常");
                    equipmentService.save(equipment);
                } else throw new RuntimeException("该设备已存在");
                break;
            case "CONSUMABLE":
                Consumable consumable = consumableService.getByName((String) assetAttributes.get("name"));
                if (consumable == null) {
                    consumable = new Consumable();
                    consumable.setName((String) assetAttributes.get("name"));
                    consumable.setType((String) assetAttributes.get("type"));
                    consumable.setSupplier(purchaseOrder.getSupplierName());
                    consumable.setStockQuantity(purchaseOrder.getQuantity());
                    consumableService.save(consumable);
                } else {
                    consumable.setStockQuantity(consumable.getStockQuantity() + purchaseOrder.getQuantity());
                    consumableService.save(consumable);
                }
        }
        purchaseOrder.setStatus("已归档");
        purchaseOrder.setArchiveDate(LocalDateTime.now());
        purchaseOrderRepository.save(purchaseOrder);
    }

    @Transactional
    public void reject(Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findFirstById(id);
        purchaseOrder.setStatus("审核失败");
        purchaseOrderRepository.save(purchaseOrder);
    }

    public List<MonthlyTotalPrice> getMonthlyArchivedTotalPrice() {
        List<MonthlyTotalPrice> rawData = purchaseOrderRepository.findMonthlyTotalPriceByArchivedStatus();

        // 可选：对数据进行格式化处理
        rawData.forEach(item -> {
            // 保留两位小数
            item.setTotalPrice(Math.round(item.getTotalPrice() * 100.0) / 100.0);
        });

        return rawData;
    }
}
