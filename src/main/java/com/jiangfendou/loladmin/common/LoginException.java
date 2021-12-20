package com.jiangfendou.loladmin.common;


import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

/**
 * CaptchaException.
 * @author jiangmh
 */
public class LoginException extends AuthenticationException {



    private static final long serialVersionUID = 1321372647800456847L;

    /** error code */
    protected ApiError apiError;

    /** error message */
    protected HttpStatus httpStatus;

    /**
     * Constructor.
     *
     */
    public LoginException(HttpStatus httpStatus, ApiError apiError) {
        super(HttpStatus.BAD_REQUEST.getReasonPhrase());
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
