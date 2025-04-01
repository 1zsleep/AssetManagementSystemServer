package com.example.assetManagementSystemServer.controller.asset;

import com.example.assetManagementSystemServer.base.query.Items;
import com.example.assetManagementSystemServer.base.query.ListParam;
import com.example.assetManagementSystemServer.entity.asset.UserAsset;
import com.example.assetManagementSystemServer.service.asset.UserAssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/userAsset")
@RequiredArgsConstructor
public class UserAssetController {
    private final UserAssetService userAssetService;

    @GetMapping
    public Items<UserAsset> getUserAsset(ListParam listParam) {
        return userAssetService.list(listParam);
    }

    @PatchMapping
    private void revertUserAsset(@RequestBody Long id) {
        userAssetService.revertUserAsset(id);
    }

    @PatchMapping("/Approval")
    public void Approval(@RequestBody Long id) {
        userAssetService.Approval(id);
    }

    @PatchMapping("/FailureToApprove")
    public void FailureToApprove(@RequestBody Long id) {
        userAssetService.FailureToApprove(id);
    }
}
