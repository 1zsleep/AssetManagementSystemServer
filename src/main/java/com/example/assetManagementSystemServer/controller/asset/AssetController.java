package com.example.assetManagementSystemServer.controller.asset;

import com.example.assetManagementSystemServer.base.BaseResponse;
import com.example.assetManagementSystemServer.entity.asset.Asset;
import com.example.assetManagementSystemServer.enums.ResponseStatusEnum;
import com.example.assetManagementSystemServer.exception.BusinessException;
import com.example.assetManagementSystemServer.service.asset.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    /**
     * 根据ID获取资产详情
     * @param assetId 资产ID
     */
    @GetMapping("/{assetId}")
    public BaseResponse<Asset> getAsset(@PathVariable Long assetId) {
        try {
            Asset asset = assetService.getAssetById(assetId);
            return BaseResponse.success(asset);
        } catch (BusinessException e) {
            return BaseResponse.fail(e.getStatus(), e.getMessage());
        }
    }

    /**
     * 更新资产基本信息
     * @param assetId 资产ID
     * @param asset 更新后的资产对象
     */
    @PutMapping("/{assetId}")
    public BaseResponse<Asset> updateAsset(
            @PathVariable Long assetId,
            @RequestBody Asset asset) {
        try {
            if (!assetId.equals(asset.getId())) {
                throw new BusinessException(ResponseStatusEnum.INVALID_PARAM, "ID不匹配");
            }
            Asset updated = assetService.saveAsset(asset);
            return BaseResponse.success(updated);
        } catch (BusinessException e) {
            return BaseResponse.fail(e.getStatus(), e.getMessage());
        }
    }

    // 统一异常处理
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<Void> handleBusinessException(BusinessException ex) {
        return BaseResponse.fail(ex.getStatus(), ex.getMessage());
    }
}