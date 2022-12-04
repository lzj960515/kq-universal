package com.kqinfo.universal.yapi.test;

import org.junit.jupiter.api.Test;

/**
 * @author Zijian Liao
 * @since 2.13.0
 */
public class StringBuildTest {

    @Test
    public void testDeleteChar(){
        StringBuilder stringBuilder = new StringBuilder("{\n" +
                "  \"code\": 0,\n" +
                "  \"message\": \"success\",\n" +
                "  \"data\": {\n" +
                "    \"id\":\"@id\", //id\n" +
                "    \"uuid\":\"@uuid\", //uuid\n" +
                "    \"username\": \"@cname\", //姓名\n" +
                "    \"age\": \"@natural\", // 年龄\n" +
                "    \"money\": \"@float()\", //金额\n" +
                "    \"datetime\":\"@datetime\",\n" +
                "    \"date\":\"@date\",\n" +
                "    \"address\":\"@county(true)\", //地址\n" +
                "  }\n" +
                "}");
        System.out.println(stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(",")));

    }
}
