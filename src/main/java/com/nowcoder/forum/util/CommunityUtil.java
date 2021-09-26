package com.nowcoder.forum.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class CommunityUtil {

    // 生成随机字符串
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    // MD5加密
    // hello -> abc123xxxx 只能加密 不能解密 同样密码每次加密都是同样的值 黑客库里可能有很多简单密码的加密结果
    // 所以要加盐salt
    public static String md5(String key) {
        if (StringUtils.isBlank(key)){
            return null;
        }

        return DigestUtils.md5DigestAsHex(key.getBytes(StandardCharsets.UTF_8));
    }

}
