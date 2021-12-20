package com.jiangfendou.loladmin.controller;

import com.jiangfendou.loladmin.common.ApiResponse;
import com.jiangfendou.loladmin.common.BusinessException;
import com.jiangfendou.loladmin.entity.SysUser;
import com.jiangfendou.loladmin.service.SysUserService;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/test")
    public ApiResponse<List<SysUser>> test() {
        return ApiResponse.success(sysUserService.list());
    }


    @PreAuthorize("hasAuthority('sys:user:list')")
    @GetMapping("/test/password")
    public ApiResponse<String> password() {
        // 加密后的密码
        String password = bCryptPasswordEncoder.encode("123");
        boolean matches = bCryptPasswordEncoder.matches("123", password);
        System.out.println("匹配结果" + matches);
        System.out.println(password);
        return ApiResponse.success(password);
    }

//    @Autowired
//    private SysUserService sysUserService;


    @GetMapping("/userInfo")
    public ApiResponse getUserInfo(Long userId) throws BusinessException {
        return ApiResponse.success(sysUserService.getUserInfo(userId));
    }

    public static void main(String[] args) throws ParseException {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        Date returnDate = dateFormat.parse("2021-12-07 13:00");
//        Date pickUpDate = dateFormat.parse("2021-12-04 00:00");
//        long minutes = (returnDate.getTime() - pickUpDate.getTime()) / 1000 / 60;
//        long hour =  (minutes - 240) / 60;
//        hour = minutes % 60 == 0 ? hour : (hour + 1);
//        Long day = hour / 24;
//        day = hour % 24 == 0 ? day : (day + 1);
//        Integer rental = day.intValue();
//        System.out.println("rental:" + rental);
//        String re = "\\d{4}-\\d{2}-\\{2}'T'\\d{2}:\\d{2}:\\d{2}";
//        String data = "2021-10-10T10:10:10";
//        if (data.matches(re)) {
//            System.out.println("false" + false);
//        }

        OffsetDateTime parse = OffsetDateTime
            .parse("2021-10-10T10:10:10+07:00", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        System.out.println(parse);
    }
}
