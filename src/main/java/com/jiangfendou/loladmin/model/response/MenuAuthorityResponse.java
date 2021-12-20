package com.jiangfendou.loladmin.model.response;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;


/**
 * MenuAuthorityResponse.
 * @author jiangmh
 */
@Data
public class MenuAuthorityResponse {

    private List<String> authorities;

    private List<Menu> nav;

    @Data
    public static class Menu {

        private Long id;

        private String name;

        private String title;

        private String icon;

        private String component;

        private String path;

        private List<Menu> children = new ArrayList<>();
    }


}
