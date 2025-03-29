package com.example.assetManagementSystemServer.controller.asset;

import com.example.assetManagementSystemServer.base.query.Items;
import com.example.assetManagementSystemServer.base.query.ListParam;
import com.example.assetManagementSystemServer.entity.asset.Asset;
import com.example.assetManagementSystemServer.enums.ResponseStatusEnum;
import com.example.assetManagementSystemServer.exception.BusinessException;
import com.example.assetManagementSystemServer.service.asset.AssetService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 资产管理控制器
 * 提供资产的新增、修改、删除、查询等接口
 * 路径：/assets
 */
@RestController
@RequestMapping("/assets")
@AllArgsConstructor
public class AssetController {

    private final AssetService assetService;

    /**
     * 创建资产（带存储桶类型记录）
     * @param asset 资产数据（JSON格式）
     * @return 创建完成的资产对象（包含存储桶类型字段）
     *
     * 业务规则：
     * 1. 资产默认存储桶类型为PRIVATE
     * 2. 存储桶类型不可修改
     */
    @PostMapping
    public Asset createAsset(@RequestBody Asset asset) {
        return assetService.createAsset(asset);
    }

    /**
     * 更新资产信息（禁止修改存储桶类型）
     * @param assetId     资产ID
     * @param updateAsset 更新数据（自动忽略cosBucketType字段）
     * @return 更新后的资产对象
     */
    @PutMapping("/{assetId}")
    public Asset updateAsset(
            @PathVariable Long assetId,
            @RequestBody Asset updateAsset) {
        return assetService.updateAsset(assetId, updateAsset);
    }

    /**
     * 删除资产及关联文件（跨存储桶删除）
     * @param assetId 资产ID
     *
     * 级联操作：
     * 1. 删除所有关联文件（不同存储桶）
     * 2. 更新相关资源计数
     */
    @DeleteMapping("/{assetId}")
    public void deleteAsset(@PathVariable Long assetId) {
        assetService.deleteAsset(assetId);
    }

    /**
     * 分页查询资产（带存储桶权限过滤）
     * @param listParam      分页参数：
     *                       - bucketType: 必须指定（PUBLIC/GROUPS/PRIVATE）
     * @param currentUserId 当前用户ID（从安全上下文中获取）
     * @return 分页结果集（包含权限过滤后的数据）
     *
     * 权限规则：
     * 1. PRIVATE: 只返回当前用户拥有的资产
     * 2. GROUPS:  返回用户所属群组的资产
     * 3. PUBLIC:  返回所有公开资产
     */
    @GetMapping
    public Items<Asset> listAssets(
            ListParam listParam,
            @RequestAttribute Long currentUserId) {
        if (listParam.getBucketType() == null) {
            throw new BusinessException(ResponseStatusEnum.INVALID_PARAM, "必须指定存储桶类型");
        }
        return assetService.listResources(listParam, currentUserId);
    }

    /**
     * 获取资产详情（带存储桶类型信息）
     * @param assetId 资产ID
     * @return 资产详细信息（包含存储桶类型字段）
     *
     * 访问控制：
     * 1. 私有资产：必须为拥有者
     * 2. 群组资产：用户需在相关群组
     * 3. 公开资产：无限制
     */
    @GetMapping("/{assetId}")
    public Asset getAsset(@PathVariable Long assetId) {
        return assetService.getById(assetId);
    }
}