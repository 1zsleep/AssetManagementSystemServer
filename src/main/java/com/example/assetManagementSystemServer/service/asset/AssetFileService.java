package com.example.assetManagementSystemServer.service.asset;

import com.example.assetManagementSystemServer.base.query.Items;
import com.example.assetManagementSystemServer.base.query.ListParam;
import com.example.assetManagementSystemServer.base.repository.BaseRepository;
import com.example.assetManagementSystemServer.base.service.BaseService;
import com.example.assetManagementSystemServer.entity.asset.Asset;
import com.example.assetManagementSystemServer.entity.asset.AssetFile;
import com.example.assetManagementSystemServer.enums.*;
import com.example.assetManagementSystemServer.exception.BusinessException;
import com.example.assetManagementSystemServer.repository.asset.AssetFileRepository;
import com.example.assetManagementSystemServer.repository.asset.AssetRepository;
import com.example.assetManagementSystemServer.service.storage.CosService;
import com.example.assetManagementSystemServer.service.user.UserGroupService;
import com.qcloud.cos.model.ResponseHeaderOverrides;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssetFileService extends BaseService<AssetFile, Long> {

    private final AssetService assetService;
    private final UserGroupService userGroupService;
    private final AssetFileRepository assetFileRepository;
    private final AssetRepository assetRepository;
    private final CosService cosService;

    // 文件签名白名单
    private static final Map<String, String[]> FILE_SIGNATURES = Map.of(
            "image/png", new String[]{"89504E47"},
            "image/jpeg", new String[]{"FFD8FF"},
            "image/webp", new String[]{"52494646"},
            "application/pdf", new String[]{"25504446"}
    );

    @Transactional
    public AssetFile uploadFile(MultipartFile file,
                                Visibility visibility,
                                Long ownerUserId,
                                Long ownerGroupId,
                                Long uploadUserId) {
        try {
            // 1. 基础校验
            validateFileHeader(file);

            // 2. 创建资产记录
            Asset asset = assetService.findOrCreateAsset(
                    visibility, ownerUserId, ownerGroupId, AssetType.FILE);

            // 3. 上传到COS
            BucketType bucketType = getBucketType(visibility);
            String cosUrl = cosService.uploadFile(
                    bucketType,
                    "documents",
                    file,
                    getOwnerId(visibility, ownerUserId, ownerGroupId)
            );

            // 4. 创建文件记录
            AssetFile assetFile = new AssetFile();
            assetFile.setAsset(asset);
            assetFile.setBucketType(bucketType);
            assetFile.setFileName(sanitizeFilename(file.getOriginalFilename()));
            assetFile.setFileType(file.getContentType());
            assetFile.setFileSize(file.getSize());
            assetFile.setCosKey(extractCosKey(cosUrl));
            assetFile.setUploadUserId(uploadUserId);

            // 5. 保存记录
            AssetFile savedFile = assetFileRepository.save(assetFile);
            updateAssetResourceCount(asset, 1);

            return savedFile;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage());
            throw new BusinessException(ResponseStatusEnum.FILE_UPLOAD_FAILED,
                    "文件上传失败: " + e.getMessage());
        }
    }

    public Items<AssetFile> listFiles(ListParam param,
                                      Visibility visibility,
                                      Long ownerUserId,
                                      Long ownerGroupId) {
        validateVisibilityParams(visibility, ownerUserId, ownerGroupId);
        return super.list(param, buildFileSpecification(visibility, ownerUserId, ownerGroupId));
    }

    @Transactional(readOnly = true)
    public String generatePreviewUrl(Long fileId, Long currentUser) {
        AssetFile file = getFileEntity(fileId);
        checkAccessPermission(file, currentUser);

        if (!isPreviewSupported(file.getFileType())) {
            throw new BusinessException(ResponseStatusEnum.PREVIEW_NOT_SUPPORTED);
        }

        String processParams = null;
        if (isImage(file.getFileType())) {
            processParams = "imageMogr2/thumbnail/!200x200/format/webp";
        }

        return cosService.generateDynamicUrl(
                file.getBucketType(),
                file.getCosKey(),
                processParams,
                120
        );
    }

    public String getThumbnailUrl(Long fileId, Long currentUser) {
        AssetFile file = getFileEntity(fileId);
        checkAccessPermission(file, currentUser);

        if (!isImage(file.getFileType())) {
            throw new BusinessException(ResponseStatusEnum.PREVIEW_NOT_SUPPORTED);
        }

        return cosService.generateDynamicUrl(
                file.getBucketType(),
                file.getCosKey(),
                "imageMogr2/thumbnail/!300x300r/format/webp",
                120
        );
    }

    public String generateDownloadUrl(Long fileId, Long currentUser) {
        AssetFile file = getFileEntity(fileId);
        checkAccessPermission(file, currentUser);

        ResponseHeaderOverrides headers = new ResponseHeaderOverrides();
        headers.setContentDisposition("attachment; filename=\"" + file.getFileName() + "\"");

        return cosService.generateDownloadUrl(
                file.getBucketType(),
                file.getCosKey(),
                30,
                headers
        );
    }

    @Transactional
    public void deleteFiles(List<Long> fileIds) {
        List<AssetFile> files = assetFileRepository.findAllById(fileIds);

        cosService.deleteFiles(files.stream()
                .map(f -> buildCosUrl(f.getBucketType(), f.getCosKey()))
                .toList()
        );

        assetFileRepository.deleteAllInBatch(files);

        files.stream()
                .collect(Collectors.groupingBy(AssetFile::getAsset))
                .forEach((asset, list) -> updateAssetResourceCount(asset, -list.size()));
    }

    //================= 私有工具方法 =================//
    private AssetFile getFileEntity(Long fileId) {
        return assetFileRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException(ResponseStatusEnum.FILE_NOT_FOUND));
    }

    private void checkAccessPermission(AssetFile file, Long currentUser) {
        switch (file.getBucketType()) {
            case PRIVATE:
                if (!Objects.equals(file.getAsset().getOwnerUserId(), currentUser)) {
                    throw new BusinessException(ResponseStatusEnum.ACCESS_DENIED);
                }
                break;
            case GROUPS:
                if (!userGroupService.isUserInGroup(currentUser, file.getAsset().getOwnerGroupId())) {
                    throw new BusinessException(ResponseStatusEnum.ACCESS_DENIED);
                }
                break;
            case PUBLIC:
                break;
            default:
                throw new BusinessException(ResponseStatusEnum.INVALID_BUCKET_TYPE);
        }
    }

    private void validateFileHeader(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[4];
            if (is.read(header) != header.length) {
                throw new BusinessException(ResponseStatusEnum.INVALID_FILE);
            }

            String hexHeader = bytesToHex(header);
            String mimeType = file.getContentType();

            if (!FILE_SIGNATURES.containsKey(mimeType) ||
                    Arrays.stream(FILE_SIGNATURES.get(mimeType))
                            .noneMatch(hexHeader::startsWith)) {
                throw new BusinessException(ResponseStatusEnum.FILE_TYPE_MISMATCH);
            }
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    private String sanitizeFilename(String filename) {
        return Optional.ofNullable(filename)
                .orElse("unnamed")
                .replaceAll("[\\\\/:*?\"<>|$]", "_")
                .replace(" ", "_");
    }

    private Specification<AssetFile> buildFileSpecification(Visibility visibility,
                                                            Long ownerUserId,
                                                            Long ownerGroupId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<AssetFile, Asset> assetJoin = root.join("asset", JoinType.INNER);

            predicates.add(cb.equal(root.get("bucketType"), getBucketType(visibility)));

            switch (visibility) {
                case PRIVATE:
                    predicates.add(cb.equal(assetJoin.get("ownerUserId"), ownerUserId));
                    break;
                case GROUP:
                    predicates.add(cb.equal(assetJoin.get("ownerGroupId"), ownerGroupId));
                    break;
                case PUBLIC:
                    predicates.add(cb.and(
                            cb.isNull(assetJoin.get("ownerUserId")),
                            cb.isNull(assetJoin.get("ownerGroupId"))
                    ));
                    break;
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void validateVisibilityParams(Visibility visibility,
                                          Long ownerUserId,
                                          Long ownerGroupId) {
        switch (visibility) {
            case PRIVATE:
                if (ownerUserId == null) throw paramError("PRIVATE类型必须提供ownerUserId");
                break;
            case GROUP:
                if (ownerGroupId == null) throw paramError("GROUP类型必须提供ownerGroupId");
                break;
            case PUBLIC:
                if (ownerUserId != null || ownerGroupId != null) {
                    throw paramError("PUBLIC资产不能指定所有者");
                }
                break;
        }
    }

    private BusinessException paramError(String message) {
        return new BusinessException(ResponseStatusEnum.PARAM_ERROR, message);
    }

    private BucketType getBucketType(Visibility visibility) {
        return switch (visibility) {
            case PUBLIC -> BucketType.PUBLIC;
            case GROUP -> BucketType.GROUPS;
            case PRIVATE -> BucketType.PRIVATE;
        };
    }

    private void updateAssetResourceCount(Asset asset, int delta) {
        int retry = 3;
        while (retry-- > 0) {
            try {
                asset.setRelatedResourceCount(asset.getRelatedResourceCount() + delta);
                assetRepository.save(asset);
                return;
            } catch (ObjectOptimisticLockingFailureException e) {
                asset = assetRepository.findById(asset.getId()).orElse(null);
                if (asset == null) break;
            }
        }
        throw new BusinessException(ResponseStatusEnum.CONCURRENT_MODIFICATION);
    }

    private String extractCosKey(String cosUrl) {
        // 正确提取完整路径（如 groups/documents/2/20250330/filename.jpg）
        String prefix = cosService.getBucketDomain(); // 获取存储桶域名部分
        return cosUrl.replace(prefix, ""); // 移除域名保留完整路径
    }

    private String buildCosUrl(BucketType bucketType, String cosKey) {
        return String.format("https://%s.cos.%s.myqcloud.com/%s",
                cosService.getBucketName(bucketType),
                cosService.getRegionName(),
                cosKey);
    }

    private boolean isPreviewSupported(String fileType) {
        return fileType != null &&
                (fileType.startsWith("image/") || "application/pdf".equals(fileType));
    }

    private boolean isImage(String fileType) {
        return fileType != null && fileType.startsWith("image/");
    }

    private Long getOwnerId(Visibility visibility, Long userId, Long groupId) {
        return switch (visibility) {
            case PRIVATE -> userId;
            case GROUP -> groupId;
            case PUBLIC -> 0L;
        };
    }

    @Override
    protected BaseRepository<AssetFile, Long> getRepository() {
        return assetFileRepository;
    }
}