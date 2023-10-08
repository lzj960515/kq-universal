package com.kqinfo.universal.func.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kqinfo.universal.func.core.ExpressionEngine;
import com.kqinfo.universal.func.core.entity.ExpressionData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@SpringBootTest
public class SimpleTest {

    @Resource
    private ExpressionEngine expressionEngine;

    @Test
    public void testExpress(){
        String expression = "IF(GTE(#age,18), '成人', IF(LT(#age,12), '儿童','青少年'))";
        {
            String jsonData = "{ \"age\": 9, \"name\": \"zhangsan\" }";
            JSONObject data = JSON.parseObject(jsonData);
            ExpressionData expressionData = new ExpressionData(expression, data);
            Assertions.assertEquals("儿童", expressionEngine.evaluateExpression(expressionData));
        }
        {
            String jsonData = "{ \"age\": 18, \"name\": \"zhangsan\" }";
            JSONObject data = JSON.parseObject(jsonData);
            ExpressionData expressionData = new ExpressionData(expression, data);
            Assertions.assertEquals("成人", expressionEngine.evaluateExpression(expressionData));
        }
    }

    @Test
    public void testDateExpression()  {
        String expression = "DATE(#birth,'yyyy-MM-dd')";
        String jsonData = "{ \"birth\": \"2023年02月02日\", \"name\": \"zhangsan\" }";
        JSONObject data = JSON.parseObject(jsonData);
        ExpressionData expressionData = new ExpressionData(expression, data, 0);
        final Object o = expressionEngine.evaluateExpression(expressionData);
        Assertions.assertEquals("2023-02-02", o.toString());
    }

    @Test
    public void testDeep(){
        String expression = "DATE(#user.birth,'yyyy-MM-dd')";
        String jsonData = "{\"user\": { \"birth\": \"2023年02月02日\", \"name\": \"zhangsan\" }}";
        JSONObject data = JSON.parseObject(jsonData);
        ExpressionData expressionData = new ExpressionData(expression, data, 0);
        final Object o = expressionEngine.evaluateExpression(expressionData);
        Assertions.assertEquals("2023-02-02", o.toString());
    }

    @Test
    public void testArray(){

        JSONArray users = new JSONArray();
        {
            String userStr = "{ \"birth\": \"2023年02月03日\", \"name\": \"zhangsan\" }";
            JSONObject user = JSON.parseObject(userStr);
            users.add(user);
        }
        {
            String userStr = "{ \"birth\": \"2023年02月04日\", \"name\": \"李四\" }";
            JSONObject user = JSON.parseObject(userStr);
            users.add(user);
        }
        {
            String userStr = "{ \"birth\": \"2023年02月05日\", \"name\": \"王五\" }";
            JSONObject user = JSON.parseObject(userStr);
            users.add(user);
        }
        JSONObject data = new JSONObject();
        data.put("users", users);

        String expression = "DATE([users.birth,'yyyy-MM-dd')";

        ExpressionData expressionData = new ExpressionData(expression, data, 1);
        final Object o = expressionEngine.evaluateExpression(expressionData);
        System.out.println(o);
        Assertions.assertEquals("2023-02-04", o.toString());
    }
}
