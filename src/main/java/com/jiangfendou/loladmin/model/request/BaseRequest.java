package com.jiangfendou.loladmin.model.request;

/**
 * BaseRequest.
 * @author jiangmh
 */
public class BaseRequest {

    private Integer size;

    private Integer current;

    public Integer getSize() {
        return size = (size == null || size == 0) ? 10 : this.size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getCurrent() {
        return current = (current == null || current == 0) ? 1 : this.current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }
}
