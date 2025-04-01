package com.example.assetManagementSystemServer.service.asset;

import com.example.assetManagementSystemServer.base.service.BaseService;
import com.example.assetManagementSystemServer.entity.asset.Equipment;
import com.example.assetManagementSystemServer.entity.asset.UserAsset;
import com.example.assetManagementSystemServer.entity.user.User;
import com.example.assetManagementSystemServer.repository.asset.EquipmentRepository;
import com.example.assetManagementSystemServer.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EquipmentService extends BaseService<Equipment, Long> {
    private final EquipmentRepository equipmentRepository;
    private final UserService userService;
    private final UserAssetService userAssetService;
    @Override
    protected EquipmentRepository getRepository() {
        return equipmentRepository;
    }
    @Transactional
    public void issueEquipment(Long id){
        Long currentUserId = userService.getCurrentUserId();
        User user = userService.getUserById(currentUserId);
        Equipment equipment = equipmentRepository.findByEquipmentId(id);
        if (equipment.getStatus()== null || equipment.getStatus().equals("使用中") || equipment.getStatus().equals("报废")){
            throw new RuntimeException("状态异常");
        }

        if (!userAssetService.findByUserIdAndAssetIdAndAssetType(currentUserId, id,"Equipment").isEmpty()){
            throw new RuntimeException("已申请");
        }
        UserAsset userAsset = new UserAsset();
        userAsset.setAssetType("Equipment");
        userAsset.setAssetId(id);
        userAsset.setAssetName(equipment.getName());
        userAsset.setQuantity(1);
        userAsset.setUserId(currentUserId);
        userAsset.setUserName(user.getUserName());
        userAsset.setStatus("申请中");
        userAssetService.saveUserAsset(userAsset);
    }

    public Equipment getEquipmentById(Long assetId) {
        return equipmentRepository.findByEquipmentId(assetId);
    }
}
