package com.example.assetManagementSystemServer.dto;

import com.example.assetManagementSystemServer.enums.ResponseStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 基础响应封装类
 * 统一API响应格式
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {
    private int code;       // 状态码
    private String message; // 消息
    private T data;         // 数据
    private Long timestamp = System.currentTimeMillis(); // 时间戳

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(
                ResponseStatusEnum.SUCCESS.getCode(),
                ResponseStatusEnum.SUCCESS.getMessage(),
                data,
                System.currentTimeMillis()
        );
    }

    public static <T> BaseResponse<T> fail(ResponseStatusEnum status) {
        return new BaseResponse<>(
                status.getCode(),
                status.getMessage(),
                null,
                System.currentTimeMillis()
        );
    }

    public static <T> BaseResponse<T> fail(ResponseStatusEnum status, String message) {
        return new BaseResponse<>(
                status.getCode(),
                message,
                null,
                System.currentTimeMillis()
        );
    }
}