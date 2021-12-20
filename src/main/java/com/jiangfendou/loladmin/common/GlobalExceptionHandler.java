package com.jiangfendou.loladmin.common;

import com.jiangfendou.loladmin.enums.ErrorCodeEnum;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * GlobalExceptionHandler.
 * @author jiangmh
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ApiResponse> handler(RuntimeException runtimeException) {
        log.error("运行时异常异常：-----------{}", runtimeException.getMessage());
        return new ResponseEntity<>(ApiResponse.failed(HttpStatus.INTERNAL_SERVER_ERROR,
            new ApiError(ErrorCodeEnum.SYSTEM_ERROR.getCode(),
                ErrorCodeEnum.SYSTEM_ERROR.getMessage())), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handler(IllegalArgumentException illegalArgumentException) {
        log.error("Assert异常：-----------{}", illegalArgumentException.getMessage());
        return new ResponseEntity<>(ApiResponse.failed(HttpStatus.INTERNAL_SERVER_ERROR,
            new ApiError(ErrorCodeEnum.SYSTEM_ERROR.getCode(),
                ErrorCodeEnum.SYSTEM_ERROR.getMessage())), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<ApiResponse> handler(BusinessException businessException) {
        log.error("Business异常：-----------{}", businessException.apiError.getMessage());
        return new ResponseEntity<>(ApiResponse.failed(businessException.getHttpStatus(),
                new ApiError(businessException.getApiError().getCode(),
                    businessException.getApiError().getMessage())), businessException.getHttpStatus());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handler(AccessDeniedException accessDeniedException) {
        log.error("拒绝访问：-----------{}", ErrorCodeEnum.NO_ACCESS_ALLOWED_ERROR.getMessage());
        return new ResponseEntity<>(ApiResponse.failed(HttpStatus.UNAUTHORIZED,
            new ApiError(ErrorCodeEnum.NO_ACCESS_ALLOWED_ERROR.getCode(),
                ErrorCodeEnum.NO_ACCESS_ALLOWED_ERROR.getMessage())), HttpStatus.UNAUTHORIZED);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = BindException.class)
    public ResponseEntity<ApiResponse> handler(BindException bindException) {
        Map<String, String> errorMap = this.getErrorMap(bindException);
        log.error("参数异常：-----------{}", errorMap);
        return new ResponseEntity<>(ApiResponse.failed(HttpStatus.BAD_REQUEST,
            new ApiError(ErrorCodeEnum.BAD_REQUEST_ERROR.getCode(),
                errorMap.toString())), HttpStatus.BAD_REQUEST);
    }

    private Map<String, String> getErrorMap(BindException bindException) {
        Map<String, String> mapError = new HashMap<>();
        bindException.getAllErrors().forEach(objectError -> {
            String defaultMessage = objectError.getDefaultMessage();
            if(StringUtils.isBlank(defaultMessage)) {
                mapError.put(objectError.getObjectName(), "参数异常");
            } else {
                String[] split = defaultMessage.split(":");
                mapError.put(split[0], split[1]);
            }
        });
        return mapError;
    }
}
