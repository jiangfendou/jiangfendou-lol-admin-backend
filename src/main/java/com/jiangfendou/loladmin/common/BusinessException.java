package com.jiangfendou.loladmin.common;

import org.springframework.http.HttpStatus;

/**
 * BusinessException class.
 *
 * @author guozheng.xi
 */
public class BusinessException extends Exception {

    private static final long serialVersionUID = 1321372647800456847L;

    /** error code */
    protected ApiError apiError;

    /** error message */
    protected HttpStatus httpStatus;

    /**
     * Constructor.
     *
     */
    public BusinessException(HttpStatus httpStatus, ApiError apiError) {
        this.httpStatus = httpStatus;
        this.apiError = apiError;
    }

    public ApiError getApiError() {
        return apiError;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
