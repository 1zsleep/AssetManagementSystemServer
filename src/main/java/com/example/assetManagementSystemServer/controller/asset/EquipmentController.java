package com.example.assetManagementSystemServer.controller.asset;

import com.example.assetManagementSystemServer.base.query.Items;
import com.example.assetManagementSystemServer.base.query.ListParam;
import com.example.assetManagementSystemServer.entity.asset.Equipment;
import com.example.assetManagementSystemServer.service.asset.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/equipment")
@RequiredArgsConstructor
public class EquipmentController {
    private final EquipmentService equipmentService;

    @GetMapping
    public Items<Equipment> getEquipmentList(ListParam listParam) {
        return equipmentService.list(listParam);
    }

    @PostMapping
    public void issueEquipment(@RequestBody Long id) {
        equipmentService.issueEquipment(id);
    }
}
