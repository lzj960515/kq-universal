package com.kqinfo.universal.test.test;

import com.alibaba.fastjson.JSONObject;
import com.kqinfo.universal.test.base.BaseRestTest;
import org.junit.jupiter.api.Test;

/**
 * @author Zijian Liao
 * @since 2.0.0
 */
public class TestControllerTest extends BaseRestTest {


    @Test
    public void testGet(){
        JSONObject result = super.get("/test", null, null);
        super.checkGetApi("http://localhost:8090/test", result);
    }

    @Test
    public void testPost(){
        User user = new User("xx", 1, null, null);
        JSONObject result = super.post("/test", null, user);
        super.checkPostApi("http://localhost:8090/test", result, user);
    }

    @Test
    public void testPut(){
        User user = new User("xx", 1, null, null);
        JSONObject result = super.put("/test", null, user);
        super.checkPutApi("http://localhost:8090/test", result, user);
    }

    @Test
    public void testDelete(){
        JSONObject result = super.delete("/test", null, null);
        super.checkDeleteApi("http://localhost:8090/test", result);
    }
}
