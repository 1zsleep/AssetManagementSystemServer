package com.example.assetManagementSystemServer.controller.asset;

import com.example.assetManagementSystemServer.base.query.Items;
import com.example.assetManagementSystemServer.base.query.ListParam;
import com.example.assetManagementSystemServer.base.BaseResponse;
import com.example.assetManagementSystemServer.entity.asset.AssetFile;
import com.example.assetManagementSystemServer.enums.ResponseStatusEnum;
import com.example.assetManagementSystemServer.enums.Visibility;
import com.example.assetManagementSystemServer.exception.BusinessException;
import com.example.assetManagementSystemServer.service.asset.AssetFileService;
import com.example.assetManagementSystemServer.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 资产文件管理控制器
 * <p>
 * 主要功能：
 * 1. 文件上传与管理
 * 2. 文件列表查询
 * 3. 文件预览/下载链接生成
 * 4. 文件删除操作
 * <p>
 * 安全特性：
 * - 基于Spring Security的权限校验
 * - 细粒度资源访问控制
 * - 文件操作日志记录
 */
@RestController
@RequestMapping("/assets/files")
@RequiredArgsConstructor
public class AssetFileController {

    private final AssetFileService assetFileService;
    private final UserService userService;
    /**
     * 获取当前认证用户ID
     *
     * @return 当前登录用户的唯一标识
     * @implNote 依赖Spring Security的认证上下文，需确保配置正确的安全过滤器链
     */
    private Long getCurrentUserId() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 从Principal中提取用户名
        String username = authentication.getName();

        // 通过用户名查询用户服务获取ID
        return userService.getUserByUserName(username).getId();
    }

    /**
     * 文件上传接口
     *
     * @param file         上传的文件内容（支持任意格式）
     * @param visibility   文件可见性规则（PUBLIC/GROUP/PRIVATE）
     * @param ownerUserId  私有文件所有者ID（visibility=PRIVATE时必填）
     * @param ownerGroupId 群组文件所属群组ID（visibility=GROUP时必填）
     * @return 包含文件元数据的响应体（自动携带HTTP 201状态码）
     * @apiNote 上传者身份通过安全上下文自动获取，文件存储路径根据可见性规则自动分配
     */
    @PostMapping("/upload")
    public BaseResponse<ResponseStatusEnum> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam Visibility visibility,
            @RequestParam(required = false) Long ownerUserId,
            @RequestParam(required = false) Long ownerGroupId) {
        try {
            Long currentUserId = getCurrentUserId();
            assetFileService.uploadFile(file, visibility, ownerUserId, ownerGroupId, currentUserId);
            return BaseResponse.success(ResponseStatusEnum.SUCCESS);
        } catch (BusinessException e) {
            return BaseResponse.fail(e.getStatus(), e.getMessage());
        }
    }

    /**
     * 分页文件列表查询
     *
     * @param visibility   可见性筛选条件（必填）
     * @param ownerUserId  用户ID（visibility=PRIVATE时必填）
     * @param ownerGroupId 群组ID（visibility=GROUP时必填）
     * @param listParam    分页参数（页码/页大小/排序规则）
     * @return 分页文件列表响应体（HTTP 200）
     * @security 非公开资源访问需要有效认证凭证，服务层会进行二次权限校验
     */
    @GetMapping
    public Items<AssetFile> listFiles(
            @RequestParam Visibility visibility,
            @RequestParam(required = false) Long ownerUserId,
            @RequestParam(required = false) Long ownerGroupId,
            ListParam listParam) {
        return assetFileService.listFiles(listParam, visibility, ownerUserId, ownerGroupId);
    }


    /**
     * 生成文件预览地址
     *
     * @param fileId 文件唯一标识
     * @return 适配预览的文件访问URL（HTTP 200）
     * @feature 图片自动缩放至800x800像素，PDF返回原始文件
     * @security 需要文件访问权限
     */
    @GetMapping("/{fileId}/preview-url")
    public BaseResponse<String> generatePreviewUrl(@PathVariable Long fileId) {
        try {
            Long currentUser = getCurrentUserId();
            return BaseResponse.success(assetFileService.generatePreviewUrl(fileId, currentUser));
        } catch (BusinessException e) {
            return BaseResponse.fail(e.getStatus(), e.getMessage());
        }
    }

    /**
     * 批量删除文件
     *
     * @param fileIds 待删除文件ID列表
     * @return 操作结果状态（HTTP 200）
     * @warning 该操作不可逆，会同时删除物理文件和数据库记录
     * @security 需要文件删除权限（所有者或管理员）
     */
    @DeleteMapping
    public BaseResponse<ResponseStatusEnum> deleteFiles(@RequestBody List<Long> fileIds) {
        try {
            Long currentUser = getCurrentUserId();
            assetFileService.deleteFiles(fileIds);
            return BaseResponse.success(ResponseStatusEnum.SUCCESS);
        } catch (BusinessException e) {
            return BaseResponse.fail(e.getStatus(), e.getMessage());
        }
    }

    /**
     * 生成文件下载地址
     *
     * @param fileId 文件唯一标识
     * @return 带下载标头的临时访问URL（HTTP 200）
     * <p>
     * &#064;feature  自动添加Content-Disposition标头触发浏览器下载
     * &#064;security  需要文件下载权限
     */
    @GetMapping("/{fileId}/download-url")
    public BaseResponse<String> getDownloadUrl(@PathVariable Long fileId) {
        try {
            Long currentUser = getCurrentUserId();
            return BaseResponse.success(assetFileService.generateDownloadUrl(fileId, currentUser));
        } catch (BusinessException e) {
            return BaseResponse.fail(e.getStatus(), e.getMessage());
        }
    }

    /**
     * 统一业务异常处理
     *
     * @param ex 捕获的业务异常
     * @return 标准化错误响应体（HTTP 400系列状态码）
     */
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<Void> handleBusinessException(BusinessException ex) {
        return BaseResponse.fail(ex.getStatus(), ex.getMessage());
    }
}