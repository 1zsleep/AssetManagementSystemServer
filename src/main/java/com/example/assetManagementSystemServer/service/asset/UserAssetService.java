package com.example.assetManagementSystemServer.service.asset;

import com.example.assetManagementSystemServer.base.repository.BaseRepository;
import com.example.assetManagementSystemServer.base.service.BaseService;
import com.example.assetManagementSystemServer.entity.asset.Book;
import com.example.assetManagementSystemServer.entity.asset.Consumable;
import com.example.assetManagementSystemServer.entity.asset.Equipment;
import com.example.assetManagementSystemServer.entity.asset.UserAsset;
import com.example.assetManagementSystemServer.repository.asset.BookRepository;
import com.example.assetManagementSystemServer.repository.asset.ConsumableRepository;
import com.example.assetManagementSystemServer.repository.asset.EquipmentRepository;
import com.example.assetManagementSystemServer.repository.asset.UserAssetRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class UserAssetService extends BaseService<UserAsset, Long> {
    private final UserAssetRepository userAssetRepository;
    private final BookRepository bookRepository;
    private final EquipmentRepository equipmentRepository;
    private final ConsumableRepository consumableRepository;

    @Override
    protected BaseRepository<UserAsset, Long> getRepository() {
        return userAssetRepository;
    }

    @Transactional
    public void revertUserAsset(Long id) {
        UserAsset userAsset = userAssetRepository.findFirstById(id);
        if (userAsset == null) return;

        if ("申请中".equals(userAsset.getStatus())) {
            userAsset.setStatus("已归还");
            userAsset.setReturnedDate(LocalDateTime.now());
            userAssetRepository.save(userAsset);
            return;
        }

        switch (userAsset.getAssetType()) {
            case "Book":
                Book book = bookRepository.findFirstByBookId(userAsset.getAssetId());
                if (book != null) {
                    book.setStockQuantity(book.getStockQuantity() + userAsset.getQuantity());
                    bookRepository.save(book);
                }
                break;
            case "Equipment":
                Equipment equipment = equipmentRepository.findFirstByEquipmentId(userAsset.getAssetId());
                if (equipment != null) {
                    equipment.setStatus("正常");
                    equipmentRepository.save(equipment);
                }
                break;
            case "Consumable":
                Consumable consumable = consumableRepository.findFirstByConsumableId(userAsset.getAssetId());
                if (consumable != null) {
                    consumable.setStockQuantity(consumable.getStockQuantity() + userAsset.getQuantity());
                    consumableRepository.save(consumable);
                }
                break;
        }

        userAsset.setStatus("已归还");
        userAsset.setReturnedDate(LocalDateTime.now());
        userAssetRepository.save(userAsset);
    }

    public List<UserAsset> findByUserIdAndAssetIdAndAssetType(Long userId, Long assetId, String assetType) {
        return userAssetRepository.findByUserIdAndAssetIdAndAssetType(userId, assetId, assetType);
    }

    @Transactional
    public void saveUserAsset(UserAsset userAsset) {
        userAssetRepository.save(userAsset);
    }

    //通过审批
    @Transactional
    public void Approval(Long id) {

        UserAsset userAsset = userAssetRepository.findFirstById(id);
        userAsset.setStatus("使用中");
        switch (userAsset.getAssetType()) {
            case "Book":
                Book book = bookRepository.findFirstByBookId(userAsset.getAssetId());
                book.setStockQuantity(book.getStockQuantity() - userAsset.getQuantity());
                bookRepository.save(book);
                break;
            case "Equipment":
                Equipment equipment = equipmentRepository.findFirstByEquipmentId(userAsset.getAssetId());
                equipment.setStatus("使用中");
                equipmentRepository.save(equipment);
                break;
            case "Consumable":
                Consumable consumable = consumableRepository.findFirstByConsumableId(userAsset.getAssetId());
                consumable.setStockQuantity(consumable.getStockQuantity() - userAsset.getQuantity());
                consumableRepository.save(consumable);
                break;
        }
        userAssetRepository.save(userAsset);
    }

    //不通过审批
    @Transactional
    public void FailureToApprove(Long id) {
        UserAsset userAsset = userAssetRepository.findFirstById(id);
        userAsset.setStatus("申请失败");
        userAssetRepository.save(userAsset);
    }
}
