package com.jiangfendou.loladmin.enums;

/**
 * StatusEnum.
 * @author jiangmh
 */
public enum StatusEnum {

    /**
     * Record Not Deleted.
     */
    NORMAL(1),

    /**
     * Record Deleted.
     */
    ABNORMAL(0);

    private final Integer value;

    StatusEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
