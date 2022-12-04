package com.kqinfo.universal.yapi.util;

/**
 * @author Zijian Liao
 * @since 2.13.0
 */
public class Json5Object {

    /**
     * 深度
     */
    private final int deep;
    private final StringBuilder data;

    public Json5Object() {
        this(0);
    }

    public Json5Object(int deep) {
        this.deep = deep;
        this.data = new StringBuilder();
        this.data.append("{\n");
    }

    public String getData() {
        String data = this.data.toString();
        if (!data.endsWith("}")) {
            // 去掉最后一个逗号
            int i = this.data.lastIndexOf(",");
            if (i > 0) {
                this.data.deleteCharAt(i);
            }
            this.data.append(blankStr(deep - 1)).append('}');
        }
        return this.data.toString();
    }

    /**
     * {
     * "code": 0,
     * "message": "success",
     * "data":
     * {
     * "id":"@id", //id
     * "uuid":"@uuid", //uuid
     * "username": "@cname", //姓名
     * "age": "@natural", // 年龄
     * "money": "@float()", //金额
     * "datetime":"@datetime",
     * "date":"@date",
     * "address":"@county(true)"
     * }
     * }
     */
    public void put(String key, String value, String desc) {
        // 1.判断深度
        this.data.append(blankStr()).append(wrapQuotes(key)).append(':');
        // 如果是个对象
        if (value.startsWith("{") || value.startsWith("[")) {
            this.data.append(" //").append(desc).append('\n').append(blankStr()).append(value).append(",\n");
        } else {
            this.data.append(wrapQuotes(value)).append(", ").append("//").append(desc).append('\n');
        }
    }

    private String wrapQuotes(String key) {
        return "\"" + key + "\"";
    }

    private String blankStr() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < deep; i++) {
            sb.append("  ");
        }
        return sb.toString();
    }

    private String blankStr(int deep) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < deep; i++) {
            sb.append("  ");
        }
        return sb.toString();
    }
}
