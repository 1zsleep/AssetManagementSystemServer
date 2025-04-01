package com.example.assetManagementSystemServer.service.asset;

import com.example.assetManagementSystemServer.base.service.BaseService;
import com.example.assetManagementSystemServer.entity.asset.Item;
import com.example.assetManagementSystemServer.entity.asset.ItemIssuance;
import com.example.assetManagementSystemServer.entity.asset.UserAsset;
import com.example.assetManagementSystemServer.enums.AssetStatus;
import com.example.assetManagementSystemServer.enums.ResponseStatusEnum;
import com.example.assetManagementSystemServer.exception.BusinessException;
import com.example.assetManagementSystemServer.repository.asset.ItemIssuanceRepository;
import com.example.assetManagementSystemServer.repository.asset.ItemRepository;
import com.example.assetManagementSystemServer.repository.asset.UserAssetRepository;
import com.example.assetManagementSystemServer.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
@RequiredArgsConstructor
public class ItemIssuanceService extends BaseService<ItemIssuance, Long> {

    private final ItemIssuanceRepository issuanceRepo;
    private final ItemRepository itemRepo;
    private final UserService userService;
    private final UserAssetRepository userAssetRepository;
    @Override
    protected ItemIssuanceRepository getRepository() {
        return issuanceRepo;
    }

    @Transactional
    public ItemIssuance issueItem(ItemIssuance itemIssuance) {
        // 1. 获取物品 与 当前用户
        Item item = itemRepo.findById(itemIssuance.getItemId()).orElseThrow(() -> new BusinessException(ResponseStatusEnum.ITEM_NOT_FOUND));
        Long currentUserId = userService.getCurrentUserId();

        // 2. 校验库存

            if (item.getCurrentStock() == null || item.getCurrentStock() < itemIssuance.getQuantity()) {
                throw new BusinessException(ResponseStatusEnum.INSUFFICIENT_INVENTORY);
            }


        // 3. 计算年度已领用量
        int currentYear = Year.now().getValue();
        Integer totalIssued = issuanceRepo.calculateAnnualUsage(
                itemIssuance.getUserId(),
                itemIssuance.getItemId(),
                currentYear
        );

        if (totalIssued + itemIssuance.getQuantity() > item.getAnnualLimit())
        {
            throw new BusinessException(ResponseStatusEnum.EXCEED_THE_ANNUAL_LIMIT_FOR_RECEIPT);
        }

        // 5. 创建领取记录
        ItemIssuance record = new ItemIssuance();
        record.setUserId(itemIssuance.getUserId());
        record.setItemId(itemIssuance.getItemId());
        record.setQuantity(itemIssuance.getQuantity());
        record.setIssueYear(currentYear);
        issuanceRepo.save(record);

        // 6. 更新库存（如果管理库存）
        if (item.getCurrentStock() != null) {
            item.setCurrentStock(item.getCurrentStock() - itemIssuance.getQuantity());
            itemRepo.save(item);
        }

        return record;
    }

}