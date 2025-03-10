package com.example.assetManagementSystemServer.config;

import com.example.assetManagementSystemServer.dto.BaseResponse;
import com.example.assetManagementSystemServer.enums.ResponseStatusEnum;
import com.example.assetManagementSystemServer.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    /**
     * 处理自定义业务异常
     * @param ex 业务异常
     * @return 包含错误信息的响应实体
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResponse<?>> handleBusinessException(BusinessException ex) {
        return ResponseEntity.status(ex.getStatus().getHttpStatus())
                .body(BaseResponse.fail(ex.getStatus(), ex.getMessage()));
    }

    /**
     * 处理参数校验异常
     * @param ex 参数校验异常
     * @return 包含校验错误信息的响应实体
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<?>> handleValidationException(MethodArgumentNotValidException ex) {
        // 提取校验失败的字段信息
        String errorMsg = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.fail(ResponseStatusEnum.DATA_VALIDATION_FAILED, "参数校验失败: " + errorMsg));
    }

    /**
     * 处理认证异常
     * @param ex 认证异常
     * @return 包含认证错误信息的响应实体
     */
    @ExceptionHandler({AuthenticationCredentialsNotFoundException.class, BadCredentialsException.class})
    public ResponseEntity<BaseResponse<?>> handleAuthenticationException(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(BaseResponse.fail(ResponseStatusEnum.INVALID_CREDENTIALS, ex.getMessage()));
    }

    /**
     * 处理全局未捕获的异常
     * @param ex 异常
     * @return 包含错误信息的响应实体
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<?>> handleGlobalException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.fail(ResponseStatusEnum.INTERNAL_ERROR));
    }
}