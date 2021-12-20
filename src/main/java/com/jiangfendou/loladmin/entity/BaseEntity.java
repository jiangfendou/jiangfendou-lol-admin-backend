package com.jiangfendou.loladmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author jobob
 * @since 2021-11-07
 */
@Data
public class BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 0: enabled; 1: disabled
     */
    private boolean isDeleted;

    private String creator;

    private LocalDateTime createDatetime;

    private String updater;

    private LocalDateTime updateDatetime;

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
