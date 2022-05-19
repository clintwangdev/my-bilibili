package com.clint.mybilibili.service.util;

import com.clint.mybilibili.domain.exception.ConditionException;
import com.google.common.base.Strings;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * MD5加密
 * 单向加密算法
 * 特点：加密速度快，不需要秘钥，但是安全性不高，需要搭配随机盐值使用
 */
public class MD5Util {

    public static String sign(String content, String salt, String charset) {
        content = content + salt;
        return DigestUtils.md5Hex(getContentBytes(content, charset));
    }

    public static boolean verify(String content, String sign, String salt, String charset) {
        content = content + salt;
        String mysign = DigestUtils.md5Hex(getContentBytes(content, charset));
        return mysign.equals(sign);
    }

    private static byte[] getContentBytes(String content, String charset) {
        if (!"".equals(charset)) {
            try {
                return content.getBytes(charset);
            } catch (UnsupportedEncodingException var3) {
                throw new RuntimeException("MD5签名过程中出现错误,指定的编码集错误");
            }
        } else {
            return content.getBytes();
        }
    }

    /**
     * 获取文件MD5加密后的字符串
     */
    public static String getFileMD5(MultipartFile file) {
        String md5Str = "";
        try (InputStream is = file.getInputStream();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] bytes = new byte[1024];
            int len = 0;
            // 循环读取文件数据写入字节数组输出流
            while ((len = is.read(bytes)) > 0) {
                baos.write(bytes, 0, len);
            }
            // 将字节数组进行MD5加密，返回加密后的字符串
            md5Str = DigestUtils.md5Hex(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Strings.isNullOrEmpty(md5Str)) {
            throw new ConditionException("加密失败！");
        }
        return md5Str;
    }
}