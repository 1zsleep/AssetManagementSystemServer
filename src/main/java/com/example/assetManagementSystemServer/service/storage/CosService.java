package com.example.assetManagementSystemServer.service.storage;

import com.example.assetManagementSystemServer.config.StoragePathConfig;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Getter
public class CosService {
    private final Map<BucketType, String> bucketMap = new EnumMap<>(BucketType.class);
    private final COSClient cosClient;
    private final String regionName;
    private final StoragePathConfig pathConfig;

    private static final Pattern URL_PATTERN = Pattern.compile(
            "^https://([^.]+)\\.cos\\.([^.]+)\\.myqcloud\\.com/(.*)$"
    );

    public CosService(COSClient cosClient,
                      StoragePathConfig pathConfig,
                      @Value("${cos.bucket.public}") String publicBucket,
                      @Value("${cos.bucket.groups}") String groupsBucket,
                      @Value("${cos.bucket.private}") String privateBucket,
                      @Value("${cos.region}") String regionName) {
        this.cosClient = cosClient;
        this.regionName = regionName;
        this.pathConfig = pathConfig;

        bucketMap.put(BucketType.PUBLIC, publicBucket);
        bucketMap.put(BucketType.GROUPS, groupsBucket);
        bucketMap.put(BucketType.PRIVATE, privateBucket);
    }

    public String uploadFile(BucketType type, String category, MultipartFile file, Long ownerId) {
        String cosKey = generateCosKey(type, category, file, ownerId);
        String bucketName = getBucketName(type);

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            cosClient.putObject(bucketName, cosKey, file.getInputStream(), metadata);
            return buildObjectUrl(bucketName, cosKey);
        } catch (IOException e) {
            throw new BusinessException(ResponseStatusEnum.FILE_UPLOAD_FAILED,
                    "文件流处理失败: " + e.getMessage());
        }
    }

    public String generateDynamicUrl(BucketType type, String cosKey,
                                     String processParams, int expireMinutes) {
        Date expiration = new Date(System.currentTimeMillis() + expireMinutes * 60_000L);
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(
                getBucketName(type), cosKey, HttpMethodName.GET
        ).withExpiration(expiration);

        if (processParams != null) {
            request.putCustomQueryParameter("imageMogr2", processParams);
        }

        return cosClient.generatePresignedUrl(request).toString();
    }

    public String generateDownloadUrl(BucketType type, String cosKey,
                                      int expireMinutes,
                                      ResponseHeaderOverrides headers) {
        GeneratePresignedUrlRequest request = buildBaseRequest(type, cosKey, expireMinutes);
        if (headers != null) {
            request.setResponseHeaders(headers);
        }
        return cosClient.generatePresignedUrl(request).toString();
    }
    public String getBucketDomain() {
        return String.format("https://%s.cos.%s.myqcloud.com/",
                getBucketName(BucketType.GROUPS), regionName);
    }
    public String generateProcessedUrl(BucketType type,
                                       String cosKey,
                                       String processParams,
                                       int expireMinutes,
                                       ResponseHeaderOverrides headers) {
        GeneratePresignedUrlRequest request = buildBaseRequest(type, cosKey, expireMinutes);
        if (processParams != null) {
            request.putCustomQueryParameter("imageMogr2", processParams);
        }
        if (headers != null) {
            request.setResponseHeaders(headers);
        }
        return cosClient.generatePresignedUrl(request).toString();
    }

    //------------------------ 核心工具方法 ------------------------//
    private String generateCosKey(BucketType type, String category,
                                  MultipartFile file, Long ownerId) {
        String sanitized = sanitizeFilename(Objects.requireNonNull(file.getOriginalFilename()));
        String encodedFilename = encodeUriComponent(sanitized);

        return pathConfig.getTemplate(type)
                .replace("{category}", category)
                .replace("{ownerId}", ownerId.toString())
                .replace("{date}", LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE))
                .replace("{uuid}", UUID.randomUUID().toString().replace("-", ""))
                .replace("{filename}", encodedFilename)
                .replace("//", "/");
    }

    private String encodeUriComponent(String component) {
        String onceEncoded = URLEncoder.encode(component, StandardCharsets.UTF_8)
                .replace("+", "%20");
        return onceEncoded.replace("%", "%25");
    }

    private GeneratePresignedUrlRequest buildBaseRequest(BucketType type,
                                                         String cosKey,
                                                         int expireMinutes) {
        Date expiration = new Date(System.currentTimeMillis() + expireMinutes * 60_000L);
        return new GeneratePresignedUrlRequest(
                getBucketName(type), cosKey, HttpMethodName.GET
        ).withExpiration(expiration);
    }

    private String sanitizeFilename(String filename) {
        return Optional.ofNullable(filename)
                .orElse("unnamed")
                .replaceAll("[\\\\/:*?\"<>|$%]", "_")
                .replace(" ", "_")
                .trim()
                .toLowerCase();
    }

    //------------------------ 辅助方法 ------------------------//
    public void deleteFiles(List<String> urls) {
        Map<String, List<String>> bucketKeys = new HashMap<>();
        urls.forEach(url -> {
            BucketUrlInfo info = parseCosUrl(url);
            bucketKeys.computeIfAbsent(info.bucketName, k -> new ArrayList<>())
                    .add(info.cosKey);
        });

        bucketKeys.forEach((bucket, keys) -> {
            DeleteObjectsRequest request = new DeleteObjectsRequest(bucket)
                    .withKeys(keys.stream()
                            .map(this::toDeleteKey)
                            .toArray(String[]::new));
            cosClient.deleteObjects(request);
        });
    }

    private String buildObjectUrl(String bucketName, String cosKey) {
        return String.format("https://%s.cos.%s.myqcloud.com/%s",
                bucketName, regionName, cosKey);
    }

    private BucketUrlInfo parseCosUrl(String url) {
        Matcher matcher = URL_PATTERN.matcher(url);
        if (!matcher.find()) {
            throw new BusinessException(ResponseStatusEnum.INVALID_COS_URL,
                    "非法的COS URL格式: " + url);
        }
        return new BucketUrlInfo(matcher.group(1), matcher.group(3));
    }

    public String getBucketName(BucketType type) {
        return bucketMap.get(type);
    }

    private String toDeleteKey(String key) {
        return new DeleteObjectsRequest.KeyVersion(key).getKey();
    }

    // ====================== 内部记录类 ====================== //

    private record BucketUrlInfo(String bucketName, String cosKey) {}
}