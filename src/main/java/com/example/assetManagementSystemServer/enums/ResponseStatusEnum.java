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

    // 并发控制 (4008xxx)
    CONCURRENT_MODIFICATION(4008001, "数据已被修改，请刷新后重试", HttpStatus.CONFLICT),

    //腾讯云cos(4009xxx)
    INVALID_COS_URL(4009001, "无效的腾讯云cos链接", HttpStatus.BAD_REQUEST),

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