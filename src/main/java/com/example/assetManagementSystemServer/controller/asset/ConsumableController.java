package com.example.assetManagementSystemServer.controller.asset;

import com.example.assetManagementSystemServer.base.query.Items;
import com.example.assetManagementSystemServer.base.query.ListParam;
import com.example.assetManagementSystemServer.entity.asset.Consumable;
import com.example.assetManagementSystemServer.service.asset.ConsumableService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/consumable")
@RequiredArgsConstructor
public class ConsumableController {
    private final ConsumableService consumableService;

    @GetMapping
    public Items<Consumable> getConsumables(ListParam listParam) {
        return consumableService.list(listParam);
    }

    @PostMapping
    public void issueConsumable(@RequestBody Dto dto) {

        consumableService.issueConsumable(dto.getId(), dto.getQuantity());
    }

    @Data
    private static class Dto {
        private Long id;
        private int quantity;
    }
}