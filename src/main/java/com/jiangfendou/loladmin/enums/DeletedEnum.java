package com.jiangfendou.loladmin.enums;

/**
 * Deleted Flag.
 *
 * @author jiangfendou
 */
public enum DeletedEnum {

    /**
     * Record Not Deleted.
     */
    NOT_DELETED(false),

    /**
     * Record Deleted.
     */
    DELETED(true);

    private final Boolean value;

    DeletedEnum(Boolean value) {
        this.value = value;
    }

    public Boolean getValue() {
        return value;
    }
}
