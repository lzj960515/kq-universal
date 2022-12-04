package com.kqinfo.universal.yapi.test;

import com.kqinfo.universal.yapi.domain.ApiInfo;
import com.kqinfo.universal.yapi.handler.YapiHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Zijian Liao
 * @since 2.13.0
 */
class YapiHandlerTest {

    private final String token = "cef29ec085e7135dcd57e3e5764596784979b174010e86b7e33a746f2465a29e";

    private final YapiHandler yapiHandler = YapiHandler.getInstance("https://yapi.cqkqinfo.com",
            token);

    @Test
    void testGetProjectInfo() {
        System.out.println(yapiHandler.getProjectInfo());
    }

    @Test
    void testAddCat() {
        System.out.println(yapiHandler.addCat( "测试", "测试描述"));
    }

    @Test
    void testListCat(){
        System.out.println(yapiHandler.listCat());
    }

    @Test
    void testAddApi(){
        yapiHandler.addApi(new ApiInfo());
    }

    @Test
    void testSaveApi(){
        yapiHandler.saveApi(new ApiInfo());
    }

    @Test
    void testListCatApi(){
        System.out.println(yapiHandler.listCatApi(7621L));
    }
}