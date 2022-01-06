package com.jiangfendou.loladmin.util;

import com.jiangfendou.loladmin.common.ApiError;
import com.jiangfendou.loladmin.common.BusinessException;
import com.jiangfendou.loladmin.enums.ErrorCodeEnum;
import java.io.IOException;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.Base64;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * QiniuCloudUtil.
 * @author jiangmh
 */
@Slf4j
@Component
public class QiniuCloudUtil {

    /** 设置需要操作的账号的AK和SK */
    private static final String ACCESS_KEY = "77D4636ZhEpfOvNUB6XI9SqIiAfet0MBoVg-ckz7";
    private static final String SECRET_KEY = "ivVmg--oikePNe_fyk7kuFvoBhf7lvQH-qTkwyGJ";

    /** 要上传的空间 */
    private static final String BUCKET_NAME = "jiangfendou";

    /** 密钥 */
    private static final Auth AUTH = Auth.create(ACCESS_KEY, SECRET_KEY);

    private static final String DOMAIN = "http://r55yflgri.hd-bkt.clouddn.com/";

    public String getUpToken() {
        return AUTH.uploadToken(BUCKET_NAME, null, 3600, new StringMap().put("insertOnly", 1));
    }

    /**
     * 普通上传
     */
    public String upload(String filePath, String fileName) throws BusinessException {
        // 创建上传对象
        UploadManager uploadManager = new UploadManager();
        try {
            // 调用put方法上传
            String token = AUTH.uploadToken(BUCKET_NAME);
            if(StringUtils.isEmpty(token)) {
                log.info("upload() ---upload token 不能为空");
                throw new BusinessException(HttpStatus.UNAUTHORIZED,
                    new ApiError(ErrorCodeEnum.UPLOAD_TOKEN_ERROR.getCode(), ErrorCodeEnum.UPLOAD_TOKEN_ERROR.getMessage()));
            }
            Response res = uploadManager.put(filePath, fileName, token);
            // 打印返回的信息
            System.out.println(res.bodyString());
            if (res.isOK()) {
                Ret ret = res.jsonToObject(Ret.class);
                return DOMAIN + ret.key;
            }
        } catch (Exception e) {
            log.info("upload() ---上传图片异常");
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR,
                new ApiError(ErrorCodeEnum.SYSTEM_ERROR.getCode(), ErrorCodeEnum.SYSTEM_ERROR.getMessage()));
        }
        return null;
    }

    /**
     * base64方式上传
     */
    public String put64image(byte[] base64, String key) throws Exception{
        String file64 = Base64.encodeToString(base64, 0);
        Integer l = base64.length;
        String url = "http://upload.qiniu.com/putb64/" + l + "/key/"+ UrlSafeBase64.encodeToString(key);
        //非华东空间需要根据注意事项 1 修改上传域名
        RequestBody rb = RequestBody.create(null, file64);
        Request request = new Request.Builder().
            url(url).
            addHeader("Content-Type", "application/octet-stream")
            .addHeader("Authorization", "UpToken " + getUpToken())
            .post(rb).build();
        OkHttpClient client = new OkHttpClient();
        okhttp3.Response response = client.newCall(request).execute();
        System.out.println(response);
        //如果不需要添加图片样式，使用以下方式
        return DOMAIN + key;
    }


    /**
     * 普通删除(暂未使用以下方法，未测试)
     */
    public void delete(String key) throws IOException {
        // 实例化一个BucketManager对象
        BucketManager bucketManager = new BucketManager(AUTH);
        // 此处的33是去掉：http://ongsua0j7.bkt.clouddn.com/,剩下的key就是图片在七牛云的名称
        key = key.substring(33);
        try {
            // 调用delete方法移动文件
            bucketManager.delete(BUCKET_NAME, key);
        } catch (QiniuException e) {
            // 捕获异常信息
            Response r = e.response;
            System.out.println(r.toString());
        }
    }

    class Ret {
        public long fsize;
        public String key;
        public String hash;
        public int width;
        public int height;
    }
}
