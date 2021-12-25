package com.jiangfendou.loladmin.enums;

/**
 * Error code.
 *
 * @author lin.duan
 */
public enum ErrorCodeEnum {
    //common
    SYSTEM_ERROR("e.jfd.1001", "系统异常"),
    ACCOUNT_PASSWORD_ERROR("e.jfd.1002", "账户或者密码输入错误"),
    CODE_ERROR("e.jfd.1003", "验证码输入错误"),
    INVALID_TOKEN("e.jfd.1004", "无效的token"),
    EXPIRED_TOKEN("e.jfd.1005", "token已过期"),
    NOT_LOGIN_ERROR("e.jfd.1006", "对不起您尚未登录"),
    NO_ACCESS_ALLOWED_ERROR("e.jfd.1007", "不允许访问"),
    NOT_FOUND("e.jfd.1008", "找不到目标数据"),
    MENU_PERM_CODE_EXIST("e.jfd.1009", "权限编码--{%s}--已存在"),
    BAD_REQUEST_ERROR("e.jfd.1010", "请求参数异常"),
    LOCKED("e.jfd.1011", "目标数据已经被锁定"),
    EXIST_CHILD_NODES("e.jfd.1012", "目标数据存在子节点"),
    USERNAME_EXIST_ERROR("e.jfd.1013", "用户名称--{%s}--已经存在"),
    LOCKED_BATCH_ERROR("e.jfd.1014", "目标数据--{%s}--已被锁定"),
    ROLE_CODE_EXITS_ERROR("e.jfd.1015", "权限编码--{%s}--已存在");

    /** error code. */
    private final String code;
    /** error message. */
    private final String message;

    /**
     * Constructor.
     *
     * @param code code of error code
     */
    ErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
