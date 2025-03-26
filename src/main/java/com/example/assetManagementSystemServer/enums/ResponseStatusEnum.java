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
    USER_NOT_IN_GROUP(4003004, "用户不在该组中", HttpStatus.BAD_REQUEST);

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