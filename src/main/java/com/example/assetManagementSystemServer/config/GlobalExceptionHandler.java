package com.example.assetManagementSystemServer.config;

import com.example.assetManagementSystemServer.base.BaseResponse;
import com.example.assetManagementSystemServer.enums.ResponseStatusEnum;
import com.example.assetManagementSystemServer.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 用于统一处理应用程序中的异常，并返回规范的响应格式
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 捕获所有未处理的异常
    @ExceptionHandler(Exception.class)
    public BaseResponse<Void> handleException(Exception ex) {
        ex.printStackTrace(); // 打印完整堆栈
        return BaseResponse.fail(
                ResponseStatusEnum.INTERNAL_ERROR,
                "服务器内部错误: " + ex.getMessage()
        );
    }
}