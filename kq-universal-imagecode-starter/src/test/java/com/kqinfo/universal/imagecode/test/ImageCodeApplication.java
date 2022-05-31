package com.kqinfo.universal.imagecode.test;

import com.kqinfo.universal.imagecode.core.ImageCode;
import com.kqinfo.universal.imagecode.core.ImageCodeHandler;
import com.kqinfo.universal.imagecode.core.ImageCodeUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zijian Liao
 * @since 1.5.0
 */
@RestController
@SpringBootApplication
public class ImageCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImageCodeApplication.class, args);
    }

    @Resource
    private ImageCodeHandler imageCodeHandler;

    @GetMapping("/send")
    public void send(HttpServletResponse response) throws IOException {
        imageCodeHandler.sendCodeImage("aaa", response.getOutputStream());
    }

    @GetMapping("/check/{code}")
    public boolean check(@PathVariable String code) throws IOException {
        return imageCodeHandler.checkCode("aaa", code);
    }

    /**
     * 模拟缓存
     */
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    @GetMapping("/send2")
    public void send2(HttpServletResponse response) throws IOException {
        ImageCode imageCode = ImageCodeUtil.createImage();
        ImageCodeUtil.sendImage(imageCode.getBufferedImage(), response.getOutputStream());
        cache.put("aaa", imageCode.getCode());
    }

    @GetMapping("/check2/{code}")
    public boolean check2(@PathVariable String code) throws IOException {
        String value = cache.get("aaa");
        return value != null && value.equals(code);
    }
}
