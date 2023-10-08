package com.kqinfo.universal.func.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kqinfo.universal.func.core.ExpressionEngine;
import com.kqinfo.universal.func.core.entity.ExpressionData;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@SpringBootTest
public class ArrayTest {
    @Resource
    private ExpressionEngine expressionEngine;


    @Test
    public void testToArrayExpression(){
        String expression = "toArray(主任医师,副主任医师,主治医师)";
        String jsonData = "{ \"age\": 9, \"name\": \"zhangsan\" }";
        JSONObject data = JSON.parseObject(jsonData);
        Object result = expressionEngine.evaluateExpression(new ExpressionData(expression, data, 0));
        System.out.println(result);
    }

    @Test
    public void testSortExpression(){
        JSONArray array = new JSONArray();
        array.add(new User("初级开发", 40));
        array.add(new User("中级开发", 40));
        array.add(new User("高级开发", 40));
        array.add(new User("初级开发", 30));
        array.add(new User("中级开发", 30));
        array.add(new User("高级开发", 30));
        array = JSONArray.parseArray(JSON.toJSONString(array));

        Object result = expressionEngine.evaluateArrayExpression(array, "sort(#title, toArray(初级开发,中级开发,高级开发), #age, desc)");
        JSONArray resultArray = (JSONArray) result;
        resultArray.forEach(System.out::println);
    }


    @Test
    public void testFilterExpression(){
        JSONArray array = new JSONArray();
        array.add(new User("初级开发", 40));
        array.add(new User("中级开发", 40));
        array.add(new User("高级开发", 40));
        array.add(new User("初级开发", 30));
        array.add(new User("中级开发", 30));
        array.add(new User("高级开发", 30));
        array = JSONArray.parseArray(JSON.toJSONString(array));

        Object result = expressionEngine.evaluateArrayExpression(array, "filter(eq(#age, 30))");
        JSONArray resultArray = (JSONArray) result;
        resultArray.forEach(System.out::println);
    }

    @Data
    @AllArgsConstructor
    static class User {

        private String title;

        private Integer age;
    }
}
