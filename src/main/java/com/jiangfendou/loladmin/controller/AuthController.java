package com.jiangfendou.loladmin.controller;

import cn.hutool.core.map.MapUtil;
import com.google.code.kaptcha.Producer;
import com.jiangfendou.loladmin.common.ApiResponse;
import com.jiangfendou.loladmin.common.Const;
import com.jiangfendou.loladmin.util.RedisUtil;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AuthController extends BaseController {

    @Autowired
    private Producer producer;

    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/captcha")
    public ApiResponse captcha() throws IOException {
        String key = UUID.randomUUID().toString();
        String code = producer.createText();
        log.info("captchaImg is code = {}", code);

        key = "aaaaa";
        code = "11111";

        BufferedImage image = producer.createImage(code);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);
        Base64 base64 = new Base64();
        String encode = base64.encodeAsString(outputStream.toByteArray());
        String str = "data:image/jpeg;base64,";
        String base64Img = str + encode;
        redisUtil.hset(Const.CAPTCHA_KEY, key, code, 120);
        log.info("token--key = {}", key);
        return ApiResponse.success(
            MapUtil.builder().
                put("token", key).
                put("captchaImg", base64Img).
                build()
        );
    }



}
