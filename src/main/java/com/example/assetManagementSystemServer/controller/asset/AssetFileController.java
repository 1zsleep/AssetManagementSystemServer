package com.example.assetManagementSystemServer.controller.asset;

import com.example.assetManagementSystemServer.base.query.Items;
import com.example.assetManagementSystemServer.base.query.ListParam;
import com.example.assetManagementSystemServer.entity.asset.AssetFile;
import com.example.assetManagementSystemServer.enums.BucketType;
import com.example.assetManagementSystemServer.enums.ResponseStatusEnum;
import com.example.assetManagementSystemServer.exception.BusinessException;
import com.example.assetManagementSystemServer.service.asset.AssetFileService;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * 资产文件管理控制器
 * 提供文件上传、预览、查询、删除等接口
 * 路径：/assets/{assetId}/files
 */
@RestController
@AllArgsConstructor
@RequestMapping("/assets/{assetId}/files")
public class AssetFileController {

    private final AssetFileService assetFileService;

    /**
     * 上传资产文件到指定存储桶
     * @param assetId     关联的资产ID
     * @param file        上传的文件内容（表单字段名必须为"file"）
     * @param bucketType  存储桶类型（PUBLIC/GROUPS/PRIVATE）
     * @param uploadUserId 上传者用户ID
     * @return 包含文件元数据的保存结果（包含COS存储路径和存储桶类型）
     *
     * 示例请求：
     * POST /assets/123/files?bucketType=GROUPS&uploadUserId=456
     * Content-Type: multipart/form-data
     */
    @PostMapping
    public AssetFile uploadFile(
            @PathVariable Long assetId,
            @RequestParam("file") MultipartFile file,
            @RequestParam BucketType bucketType,
            @RequestParam Long uploadUserId) {
        return assetFileService.uploadFile(bucketType, assetId, file, uploadUserId);
    }

    /**
     * 生成图片缩略图访问URL
     * @param fileId 文件ID
     * @param width  缩略图宽度（像素，默认200）
     * @param height 缩略图高度（像素，默认200）
     * @return 带缩略参数的临时访问URL（仅支持图片类型）
     *
     * 访问逻辑：
     * 1. 根据文件存储的实际存储桶生成URL
     * 2. 自动添加COS图片处理参数
     * 3. 非图片类型返回400错误
     */
    @GetMapping("/{fileId}/preview")
    public String generatePreviewUrl(
            @PathVariable Long fileId,
            @RequestParam(defaultValue = "200") @Min(1) int width,
            @RequestParam(defaultValue = "200") @Min(1) int height) {
        String url = assetFileService.generatePreviewUrl(fileId, width, height);
        if (url == null) {
            throw new BusinessException(ResponseStatusEnum.PREVIEW_NOT_SUPPORTED);
        }
        return url;
    }

    /**
     * 获取原始文件访问URL
     * @param fileId 文件ID
     * @return 原文件的临时访问URL（60分钟有效）
     *
     * 安全说明：
     * 1. 自动根据文件存储的存储桶生成URL
     * 2. 有效期较短防止URL泄露
     */
    @GetMapping("/{fileId}/original")
    public String getOriginalFileUrl(@PathVariable Long fileId) {
        return assetFileService.getOriginalFileUrl(fileId);
    }

    /**
     * 分页查询资产文件（支持跨存储桶）
     * @param assetId    所属资产ID
     * @param listParam  分页参数：
     *                   - bucketType: 存储桶类型（可选）
     *                   - filter: 过滤条件（例如：fileType='image/png'）
     * @return 分页结果集（包含文件列表和总数）
     *
     * 查询逻辑：
     * 1. 当指定bucketType时，只返回对应存储桶的文件
     * 2. 默认展示所有存储桶的文件（需要前端处理）
     */
    @GetMapping
    public Items<AssetFile> listFiles(
            @PathVariable Long assetId,
            ListParam listParam) {
        return assetFileService.listFiles(assetId, listParam);
    }

    /**
     * 批量删除文件（跨存储桶操作）
     * @param fileIds 需要删除的文件ID列表（逗号分隔）
     *
     * 级联操作：
     * 1. 删除数据库记录
     * 2. 删除COS存储文件
     * 3. 更新资产关联资源计数
     */
    @DeleteMapping
    public void deleteFiles(@RequestParam List<Long> fileIds) {
        assetFileService.deleteFiles(fileIds);
    }
}