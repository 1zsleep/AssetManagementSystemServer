package com.example.assetManagementSystemServer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResponseStatusEnum {
    // 基础状态
    SUCCESS(200, "操作成功", HttpStatus.OK),
    BAD_REQUEST(400, "请求参数错误", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(401, "未授权访问", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(403, "禁止访问", HttpStatus.FORBIDDEN),
    NOT_FOUND(404, "资源不存在", HttpStatus.NOT_FOUND),
    INTERNAL_ERROR(500, "服务器内部错误", HttpStatus.INTERNAL_SERVER_ERROR),

    // 认证相关 (4001xxx)
    USER_EXISTS(4001001, "用户名已存在", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS(4001002, "用户名或密码错误", HttpStatus.BAD_REQUEST),
    TOKEN_EXPIRED(4001003, "身份令牌已过期", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(4001004, "无效身份令牌", HttpStatus.UNAUTHORIZED),

    // 业务通用错误 (4002xxx)
    DATA_VALIDATION_FAILED(4002001, "数据校验失败", HttpStatus.BAD_REQUEST),
    ILLEGAL_OPERATION(4002002, "非法操作", HttpStatus.FORBIDDEN),
    USER_NOT_FOUND(4002003, "用户不存在", HttpStatus.NOT_FOUND),
    INVALID_PARAM(4002004, "无效请求参数", HttpStatus.BAD_REQUEST),
    NO_UPDATE_PERMISSION(4002005, "没有修改权限", HttpStatus.FORBIDDEN),

    // 用户组相关 (4003xxx)
    GROUP_EXISTS(4003001, "用户组已存在", HttpStatus.BAD_REQUEST),
    GROUP_NOT_FOUND(4003002, "用户组不存在", HttpStatus.NOT_FOUND),
    USER_ALREADY_IN_GROUP(4003003, "用户已在组中", HttpStatus.BAD_REQUEST),
    USER_NOT_IN_GROUP(4003004, "用户不在该组中", HttpStatus.BAD_REQUEST),

    // 资产相关 (4006xxx)
    ASSET_NOT_FOUND(4006001, "资产不存在", HttpStatus.NOT_FOUND),
    ASSET_NAME_EXISTS(4006002, "资产名称已存在", HttpStatus.BAD_REQUEST),
    ASSET_STATUS_ILLEGAL(4006003, "资产状态流转非法", HttpStatus.BAD_REQUEST),
    ASSET_OPERATION_FORBIDDEN(4006004, "无权操作该资产", HttpStatus.FORBIDDEN),

    // 文件相关 (4007xxx)
    FILE_UPLOAD_FAILED(4007001, "文件上传失败", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_NOT_FOUND(4007002, "文件不存在", HttpStatus.NOT_FOUND),
    FILE_DELETE_FAILED(4007003, "文件删除失败", HttpStatus.INTERNAL_SERVER_ERROR),
    PREVIEW_NOT_SUPPORTED(4007004, "该文件类型不支持预览", HttpStatus.BAD_REQUEST),
    THUMBNAIL_GENERATE_FAILED(4007005, "缩略图生成失败", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_DOWNLOAD_FAILED(4007006, "文件下载链接生成失败", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_TYPE_NOT_SUPPORTED(4007007, "不支持的文件类型", HttpStatus.BAD_REQUEST),
    PARAM_ERROR(4007008, "参数错误", HttpStatus.BAD_REQUEST),
    INVALID_FILE(4007009, "无效的文件", HttpStatus.BAD_REQUEST),
    FILE_UPLOWD_FAILED(4007010, "文件流处理失败", HttpStatus.INTERNAL_SERVER_ERROR),

    // 并发控制 (4008xxx)
    CONCURRENT_MODIFICATION(4008001, "数据已被修改，请刷新后重试", HttpStatus.CONFLICT),

    //腾讯云cos(4009xxx)
    INVALID_COS_URL(4009001, "无效的腾讯云cos链接", HttpStatus.BAD_REQUEST),
    ACCESS_DENIED(4009002, "访问被拒绝", HttpStatus.FORBIDDEN),
    FILE_TYPE_MISMATCH(4009003, "文件类型不匹配", HttpStatus.BAD_REQUEST),
    INVALID_BUCKET_TYPE(4009004, "无效的Bucket类型", HttpStatus.BAD_REQUEST),
    CONFIG_ERROR(4009005, "配置错误", HttpStatus.BAD_REQUEST),

    //物品（4010xxx）
    ITEM_NOT_FOUND(4010001, "物品不存在", HttpStatus.NOT_FOUND),
    ITEM_NAME_EXISTS(4010002, "物品名称已存在", HttpStatus.BAD_REQUEST),
    ITEM_STATUS_ILLEGAL(4010003, "物品状态流转非法", HttpStatus.BAD_REQUEST),
    ITEM_OPERATION_FORBIDDEN(4010004, "无权操作该物品", HttpStatus.FORBIDDEN),
    ITEM_NOT_IN_GROUP(4010005, "物品不在该组中", HttpStatus.BAD_REQUEST),
    ITEM_ALREADY_IN_GROUP(4010006, "物品已在组中", HttpStatus.BAD_REQUEST),
    ITEM_GROUP_NOT_FOUND(4010007, "物品组不存在", HttpStatus.NOT_FOUND),
    ITEM_GROUP_EXISTS(4010008, "物品组已存在", HttpStatus.BAD_REQUEST),
    ITEM_GROUP_OPERATION_FORBIDDEN(4010009, "无权操作该物品组", HttpStatus.FORBIDDEN),
    ITEM_GROUP_NOT_IN_GROUP(4010010, "物品组不在该组中", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_INVENTORY(4010011,"库存不足",HttpStatus.BAD_REQUEST),
    EXCEED_THE_ANNUAL_LIMIT_FOR_RECEIPT(4010012,"超出年度领用上限",HttpStatus.BAD_REQUEST),

    ;

    private final int code;       // 业务状态码
    private final String message; // 中文描述
    private final HttpStatus httpStatus;  // HTTP状态码

    public static ResponseStatusEnum getByCode(int code) {
        for (ResponseStatusEnum status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return INTERNAL_ERROR;
    }
}