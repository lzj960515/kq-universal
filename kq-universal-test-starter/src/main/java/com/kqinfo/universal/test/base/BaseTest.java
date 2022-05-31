package com.kqinfo.universal.test.base;

import com.kqinfo.universal.test.util.ApiUtil;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test抽象类，用于测试类的继承
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@SpringBootTest
public abstract class BaseTest {

    /**
     * 校验get请求url返回数据与传入数据结构是否一致
     *
     * @param url    url
     * @param source 需要比对的数据
     */
    protected void checkGetApi(String url, Object source) {
        try{
            ApiUtil.checkGetApi(url, source);
        }catch (RuntimeException e){
            MatcherAssert.assertThat(e.getMessage(), e.getMessage(), CoreMatchers.nullValue());
        }
    }

    /**
     * 校验post请求url返回数据与传入数据结构是否一致
     *
     * @param url    url
     * @param param  请求参数
     * @param source 需要比对的数据
     */
    protected void checkPostApi(String url, Object param, Object source) {
        try{
            ApiUtil.checkPostApi(url, param, source);
        }catch (RuntimeException e){
            MatcherAssert.assertThat(e.getMessage(), e.getMessage(), CoreMatchers.nullValue());
        }
    }

    /**
     * 校验put请求url返回数据与传入数据结构是否一致
     *
     * @param url    url
     * @param param  请求参数
     * @param source 需要比对的数据
     */
    protected void checkPutApi(String url, Object param, Object source) {
        try{
            ApiUtil.checkPutApi(url, param, source);
        }catch (RuntimeException e){
            MatcherAssert.assertThat(e.getMessage(), e.getMessage(), CoreMatchers.nullValue());
        }
    }

    /**
     * 校验delete请求url返回数据与传入数据结构是否一致
     *
     * @param url    url
     * @param source 需要比对的数据
     */
    protected void checkDeleteApi(String url, Object source) {
        try{
            ApiUtil.checkDeleteApi(url, source);
        }catch (RuntimeException e){
            MatcherAssert.assertThat(e.getMessage(), e.getMessage(), CoreMatchers.nullValue());
        }
    }
}
