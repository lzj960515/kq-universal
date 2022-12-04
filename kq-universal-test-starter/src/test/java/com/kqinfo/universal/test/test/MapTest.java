package com.kqinfo.universal.test.test;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.ShearCaptcha;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
public class MapTest {

    @Test
    public void testMap(){
        Map<String, Integer> map = new HashMap<>(8);
        map.put("d", 9);
        map.put("e", 10);
        map.put("f", 11);
        map.put("A", 1);
        map.put("g", 2);
        map.put("C", 3);

        TreeMap<Integer, String> treeMap = new TreeMap<>();
        map.forEach((key, value) ->{
            treeMap.put(value, key);
        });
        System.out.println(treeMap.values());

        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(200, 100, 4, 4);
        //ShearCaptcha captcha = new ShearCaptcha(200, 100, 4, 4);
        //图形验证码写出，可以写出到文件，也可以写出到流
        captcha.write("d:/shear.png");
        //验证图形验证码的有效性，返回boolean值
        captcha.verify("1234");
    }
}
