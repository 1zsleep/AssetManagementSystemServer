package com.example.assetManagementSystemServer.exception;

import com.example.assetManagementSystemServer.enums.ResponseStatusEnum;
import lombok.Getter;

/**
 * 自定义业务异常
 * 用于处理业务逻辑中的异常情况
 */
@Getter
public class BusinessException extends RuntimeException {
    private final ResponseStatusEnum status; // 异常状态

    public BusinessException(ResponseStatusEnum status) {
        super(status.getMessage());
        this.status = status;
    }

    public BusinessException(ResponseStatusEnum status, String message) {
        super(message);
        this.status = status;
    }
}