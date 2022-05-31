package com.kqinfo.universal.imagecode.core;

import com.kqinfo.universal.imagecode.properties.ImageCodeProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 图形验证码工具
 *
 * @author Zijian Liao
 * @since 1.5.0
 */
@Slf4j
public class ImageCodeHandler {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ImageCodeProperties imageCodeProperties;

    public void sendCodeImage(String key, OutputStream outputStream){
        ImageCode imageCode = ImageCodeUtil.createImage();
        stringRedisTemplate.opsForValue().set(key, imageCode.getCode(), imageCodeProperties.getCodeExpireSeconds(), TimeUnit.SECONDS);
        ImageCodeUtil.sendImage(imageCode.getBufferedImage(), outputStream);
    }

    public boolean checkCode(@NonNull String key, @NonNull String code){
        String cacheCode = stringRedisTemplate.opsForValue().get(key);
        if(cacheCode != null){
            return code.equals(cacheCode);
        }
        return false;
    }
}
