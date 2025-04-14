package com.example.assetManagementSystemServer.service.asset;

import com.example.assetManagementSystemServer.base.service.BaseService;
import com.example.assetManagementSystemServer.entity.asset.Consumable;
import com.example.assetManagementSystemServer.entity.asset.UserAsset;
import com.example.assetManagementSystemServer.entity.user.User;
import com.example.assetManagementSystemServer.repository.asset.ConsumableRepository;
import com.example.assetManagementSystemServer.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ConsumableService extends BaseService<Consumable, Long> {
    private final ConsumableRepository consumableRepository;
    private final UserService userService;
    private final UserAssetService userAssetService;
    @Override
    protected ConsumableRepository getRepository() {
        return consumableRepository;
    }
    @Transactional
    public void issueConsumable(Long id,int quantity){
        Long currentUserId = userService.getCurrentUserId();
        User user = userService.getUserById(currentUserId);
        Consumable consumable = consumableRepository.findByConsumableId(id);
        if (consumable.getStockQuantity() == null || consumable.getStockQuantity() < 1){
            throw new RuntimeException("库存不足");
        }

        UserAsset userAsset = new UserAsset();
        userAsset.setAssetType("Consumable");
        userAsset.setAssetId(id);
        userAsset.setAssetName(consumable.getName());
        userAsset.setQuantity(quantity);
        userAsset.setUserId(currentUserId);
        userAsset.setUserName(user.getUserName());
        userAsset.setStatus("申请中");
        userAssetService.saveUserAsset(userAsset);

    }

    public Consumable getConsumableById(Long assetId) {
        return consumableRepository.findByConsumableId(assetId);
    }
    public Consumable getByName(String Name) {
        return consumableRepository.findFirstByName(Name);
    }

    public List<Consumable> getTop10Consumables() {
        return consumableRepository.findTop10ByOrderByConsumptionStatisticsDesc();
    }
}
