package com.example.assetManagementSystemServer.service.asset;

import com.example.assetManagementSystemServer.base.query.Items;
import com.example.assetManagementSystemServer.base.query.ListParam;
import com.example.assetManagementSystemServer.base.service.BaseService;
import com.example.assetManagementSystemServer.entity.asset.Asset;
import com.example.assetManagementSystemServer.entity.asset.AssetFile;
import com.example.assetManagementSystemServer.enums.BucketType;
import com.example.assetManagementSystemServer.enums.ResponseStatusEnum;
import com.example.assetManagementSystemServer.exception.BusinessException;
import com.example.assetManagementSystemServer.repository.asset.AssetFileRepository;
import com.example.assetManagementSystemServer.repository.asset.AssetRepository;
import com.example.assetManagementSystemServer.service.storage.CosService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.counting;

/**
 * 资产文件管理服务
 * 功能：
 * 1. 资产文件上传
 * 2. 生成预览URL
 * 3. 文件分页查询
 * 4. 批量删除文件
 */
@Service
public class AssetFileService extends BaseService<AssetFile, Long> {

    private final AssetFileRepository fileRepository;
    private final AssetRepository assetRepository;
    private final CosService cosService;

    /**
     * 构造函数（依赖注入）
     */
    public AssetFileService(AssetFileRepository fileRepository,
                            AssetRepository assetRepository,
                            CosService cosService) {
        this.fileRepository = fileRepository;
        this.assetRepository = assetRepository;
        this.cosService = cosService;
    }

    @Override
    protected AssetFileRepository getRepository() {
        return fileRepository;
    }

    /**
     * 上传文件到指定类型的存储桶
     * @param bucketType 存储桶类型（PUBLIC/GROUPS/PRIVATE）
     * @param assetId    关联资产ID
     * @param file       上传的文件
     * @param uploadUserId 上传者ID
     */
    @Transactional
    public AssetFile uploadFile(BucketType bucketType, Long assetId,
                                MultipartFile file, Long uploadUserId) {
        // 校验资产存在性
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new BusinessException(ResponseStatusEnum.ASSET_NOT_FOUND));

        // 根据存储桶类型上传到不同位置
        String cosUrl = cosService.uploadFile(
                bucketType,  // 动态指定存储桶
                file,
                assetId
        );

        // 构建文件记录
        AssetFile assetFile = new AssetFile();
        assetFile.setAssetId(assetId);
        assetFile.setFileName(file.getOriginalFilename());
        assetFile.setFileType(file.getContentType());
        assetFile.setFileSize(file.getSize());
        assetFile.setUploadUserId(uploadUserId);
        assetFile.setCosKey(extractCosKeyFromUrl(cosUrl));
        assetFile.setCosBucketType(bucketType);  // 新增存储桶类型字段
        assetFile.setCreatedAt(LocalDateTime.now());

        // 保存记录并更新计数
        AssetFile savedFile = fileRepository.save(assetFile);
        assetRepository.updateResourceCount(assetId, 1);
        return savedFile;
    }

    /**
     * 生成图片缩略图访问URL（固定宽高等比缩放）
     * @param fileId 文件ID
     * @param width 缩略图最大宽度（像素）
     * @param height 缩略图最大高度（像素）
     * @return 缩略图URL（非图片类型返回null）
     */
    public String generatePreviewUrl(Long fileId, int width, int height) {
        AssetFile file = fileRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException(ResponseStatusEnum.FILE_NOT_FOUND));

        // 仅处理图片类型
        if (!file.getFileType().startsWith("image/")) return null;

        // 构建处理参数
        String processedKey = String.format("%s?imageMogr2/thumbnail/%dx%d",
                file.getCosKey(), width, height);

        // 动态使用文件实际存储桶
        return cosService.generatePresignedUrl(
                file.getCosBucketType(),  // 关键修改点
                processedKey,
                30
        );
    }

    /**
     * 获取原文件访问URL（所有文件类型通用）
     * @param fileId 文件ID
     * @return 原文件的临时访问URL（60分钟有效）
     */
    public String getOriginalFileUrl(Long fileId) {
        AssetFile file = fileRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException(ResponseStatusEnum.FILE_NOT_FOUND));

        return cosService.generatePresignedUrl(
                BucketType.PRIVATE,
                file.getCosKey(),
                60
        );
    }

    /**
     * 分页查询资产文件
     * @param assetId 资产ID
     * @param param 分页参数
     * @return 分页结果
     */
    public Items<AssetFile> listFiles(Long assetId, ListParam param) {
        return list(param, (root, query, cb) ->
                cb.equal(root.get("assetId"), assetId)
        );
    }

    /**
     * 批量删除文件
     * @param fileIds 要删除的文件ID列表
     */
    @Transactional
    public void deleteFiles(List<Long> fileIds) {
        // 获取所有文件记录
        List<AssetFile> files = fileRepository.findAllById(fileIds);

        // 构建要删除的URL列表
        List<String> urls = files.stream()
                .map(f -> buildCosUrl(f.getCosKey()))
                .toList();

        // 执行COS删除和数据库删除
        cosService.deleteFiles(urls);
        fileRepository.deleteAllById(fileIds);

        // 按资产分组统计删除数量
        Map<Long, Long> countMap = files.stream()
                .collect(groupingBy(AssetFile::getAssetId, counting()));

        // 更新每个资产的资源计数
        countMap.forEach((assetId, count) ->
                assetRepository.updateResourceCount(assetId, -count.intValue())
        );
    }

    //------------------------ 私有工具方法 ------------------------//

    /**
     * 从URL中提取COS存储路径
     */
    private String extractCosKeyFromUrl(String url) {
        return url.substring(url.indexOf(".com/") + 5);
    }

    /**
     * 构建完整的COS访问URL
     */
    private String buildCosUrl(String cosKey) {
        return "https://" +
                cosService.getBucketName(BucketType.PRIVATE) +
                ".cos." +
                cosService.getRegionName() +
                ".myqcloud.com/" +
                cosKey;
    }
}