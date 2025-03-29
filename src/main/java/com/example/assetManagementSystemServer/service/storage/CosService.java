package com.example.assetManagementSystemServer.service.storage;

import com.example.assetManagementSystemServer.enums.BucketType;
import com.example.assetManagementSystemServer.enums.ResponseStatusEnum;
import com.example.assetManagementSystemServer.exception.BusinessException;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 腾讯云COS存储服务（多存储桶版）
 * 功能：
 * 1. 文件上传到指定类型的存储桶
 * 2. 生成预签名访问URL
 * 3. 批量删除文件
 */
@Service
public class CosService {
    // 存储桶名称映射（类型 -> 桶名）
    private final Map<BucketType, String> buckets = new EnumMap<>(BucketType.class);
    private final COSClient cosClient;
    /**
     * -- GETTER --
     *  获取区域名称（公共方法）
     */
    @Getter
    private final String regionName;

    /**
     * 构造函数（Spring依赖注入）
     * @param cosClient COS客户端实例
     * @param publicBucket 公共存储桶名称
     * @param groupsBucket 群组存储桶名称
     * @param privateBucket 私有存储桶名称
     * @param regionName COS区域名称（如ap-shanghai）
     */
    public CosService(
            COSClient cosClient,
            @Value("${cos.bucket.public}") String publicBucket,
            @Value("${cos.bucket.groups}") String groupsBucket,
            @Value("${cos.bucket.private}") String privateBucket,
            @Value("${cos.region}") String regionName
    ) {
        this.cosClient = cosClient;
        this.regionName = regionName;

        // 初始化存储桶映射关系
        buckets.put(BucketType.PUBLIC, publicBucket);
        buckets.put(BucketType.GROUPS, groupsBucket);
        buckets.put(BucketType.PRIVATE, privateBucket);
    }

    /**
     * 上传文件到指定类型的存储桶
     * @param bucketType 存储桶类型枚举
     * @param file 上传的文件对象
     * @param ownerId 资源所有者ID（用户/群组/资产ID）
     * @return 文件的完整访问URL
     */
    public String uploadFile(BucketType bucketType, MultipartFile file, Long ownerId) {
        String cosKey = generateCosKey(bucketType, file, ownerId);
        String bucketName = getBucketName(bucketType);

        try {
            // 构建文件元数据
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            // 执行上传操作
            cosClient.putObject(new PutObjectRequest(bucketName, cosKey,
                    file.getInputStream(), metadata));

            return buildObjectUrl(bucketName, cosKey);
        } catch (IOException e) {
            throw new BusinessException(ResponseStatusEnum.FILE_UPLOAD_FAILED,
                    "文件流读取失败: " + e.getMessage());
        }
    }

    /**
     * 生成临时访问URL（预签名）
     * @param bucketType 存储桶类型
     * @param cosKey 文件在COS的完整路径
     * @param expireMinutes 有效期（分钟）
     * @return 带签名的临时访问URL
     */
    public String generatePresignedUrl(BucketType bucketType, String cosKey, int expireMinutes) {
        Date expiration = new Date(System.currentTimeMillis() + (long) expireMinutes * 60 * 1000);
        return cosClient.generatePresignedUrl(
                getBucketName(bucketType),
                cosKey,
                expiration,
                HttpMethodName.GET
        ).toString();
    }

    /**
     * 批量删除文件（支持跨存储桶操作）
     * @param urls 待删除文件的完整URL列表
     */
    public void deleteFiles(List<String> urls) {
        // 按存储桶分组待删除的Key
        Map<String, List<String>> bucketKeys = new HashMap<>();

        urls.forEach(url -> {
            BucketUrlInfo info = parseCosUrl(url);
            bucketKeys.computeIfAbsent(info.bucketName, k -> new ArrayList<>())
                    .add(info.cosKey);
        });

        // 分桶执行批量删除
        bucketKeys.forEach((bucketName, keys) -> {
            DeleteObjectsRequest request = new DeleteObjectsRequest(bucketName)
                    .withKeys(keys.toArray(new String[0]));
            cosClient.deleteObjects(request);
        });
    }

    //------------------------ 私有方法 ------------------------//

    /**
     * 生成标准化的存储路径
     * 路径规则：
     * - 公共桶：public/avatars/{ownerId}/日期/uuid_文件名
     * - 群组桶：groups/{ownerId}/files/日期/uuid_文件名
     * - 私有桶：private/assets/{ownerId}/日期/uuid_文件名
     */
    private String generateCosKey(BucketType type, MultipartFile file, Long ownerId) {
        String fileName = sanitizeFilename(file.getOriginalFilename());
        String dateDir = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String uuid = UUID.randomUUID().toString().replace("-", "");

        switch (type) {
            case PUBLIC:
                return String.format("public/avatars/%d/%s/%s_%s",
                        ownerId, dateDir, uuid, fileName);
            case GROUPS:
                return String.format("groups/%d/files/%s/%s_%s",
                        ownerId, dateDir, uuid, fileName);
            case PRIVATE:
                return String.format("private/assets/%d/%s/%s_%s",
                        ownerId, dateDir, uuid, fileName);
            default:
                throw new IllegalArgumentException("无效的存储桶类型");
        }
    }

    /**
     * 从URL解析存储桶名称和文件路径
     */
    private BucketUrlInfo parseCosUrl(String url) {
        String prefixPattern = String.format("https://%s.cos.%s.myqcloud.com/",
                "(.*?)", regionName);

        if (!url.matches(prefixPattern + ".*")) {
            throw new BusinessException(ResponseStatusEnum.INVALID_COS_URL);
        }

        String bucketName = url.replaceFirst("https://(.*?)\\..*", "$1");
        String cosKey = url.replaceFirst("https://.*?/", "");
        return new BucketUrlInfo(bucketName, cosKey);
    }

    /**
     * 文件名消毒处理（去除特殊字符）
     */
    private String sanitizeFilename(String filename) {
        return Objects.requireNonNull(filename)
                .replace(" ", "_")
                .replaceAll("[\\\\/:*?\"<>|]", "");
    }

    /**
     * 构建完整的对象访问URL
     */
    private String buildObjectUrl(String bucketName, String cosKey) {
        return String.format("https://%s.cos.%s.myqcloud.com/%s",
                bucketName, regionName, cosKey);
    }

    //------------------------ 公共辅助方法 ------------------------//

    /**
     * 获取存储桶名称（公共方法）
     */
    public String getBucketName(BucketType type) {
        return buckets.get(type);
    }

    //------------------------ 内部辅助类 ------------------------//

    /**
     * 存储桶URL解析结果封装
     */
    private static class BucketUrlInfo {
        final String bucketName;
        final String cosKey;

        BucketUrlInfo(String bucketName, String cosKey) {
            this.bucketName = bucketName;
            this.cosKey = cosKey;
        }
    }
}