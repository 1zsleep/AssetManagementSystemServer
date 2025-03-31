package com.example.assetManagementSystemServer.controller.asset;

import com.example.assetManagementSystemServer.entity.asset.ItemIssuance;
import com.example.assetManagementSystemServer.service.asset.ItemIssuanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/itemIssuance")
@RequiredArgsConstructor
public class ItemIssuanceController {
    private final ItemIssuanceService itemIssuanceService;

    @PostMapping
    public void issueItem(@RequestBody ItemIssuance itemIssuance) {
        itemIssuanceService.issueItem(itemIssuance);
    }
}
