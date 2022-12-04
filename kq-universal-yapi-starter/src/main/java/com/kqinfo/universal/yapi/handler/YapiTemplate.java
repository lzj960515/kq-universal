package com.kqinfo.universal.yapi.handler;

import cn.hutool.core.lang.Assert;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONValidator;

import java.util.Map;

/**
 * http请求工具
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
public class YapiTemplate {
    private final String yapiAddress;

    /**
     * yapi响应信息
     */
    private static final String ERR_CODE = "errcode";
    private static final String ERR_MSG = "errmsg";

    protected YapiTemplate(String yapiAddress) {
        this.yapiAddress = yapiAddress;
    }

    public String get(String url, Map<String, Object> params) {
        Assert.notEmpty(params, "params is empty, please use get(String url) method");
        HttpResponse response = HttpRequest.get(appendUrl(concatUri(url), params)).execute();
        validResult(response);
        return response.body();
    }

    public String post(String url, Object body) {
        HttpResponse response = HttpRequest.post(concatUri(url))
                .body(JSON.toJSONString(body))
                .execute();
        validResult(response);
        return response.body();
    }

    public String post(String url, String token, Object body) {
        HttpResponse response = HttpRequest.post(concatUri(url))
                .header("token", token)
                .body(JSON.toJSONString(body))
                .execute();
        validResult(response);
        return response.body();
    }

    public String post(String url, String token, Map<String, Object> params, Object body) {
        Assert.notEmpty(params, "params is empty, please use post(String url, Object body) method");
        HttpResponse response = HttpRequest.post(appendUrl(concatUri(url), params))
                .header("token", token)
                .body(JSON.toJSONString(body)).execute();
        validResult(response);
        return response.body();
    }

    public String postForm(String url, Map<String, Object> params){
        HttpResponse response = HttpRequest.post(concatUri(url)).form(params).execute();
        validResult(response);
        return response.body();
    }

    //---------------私有方法---------------
    private String appendUrl(String url, Map<String, Object> params) {
        String with = "&";
        String equal = "=";
        StringBuilder sb = new StringBuilder();
        params.forEach((key, value) -> sb.append(key).append(equal).append(value).append(with));
        return url.concat("?").concat(sb.substring(0, sb.length() - 1));
    }

    private void validResult(HttpResponse response) {
        String result = response.body();
        if (!JSONValidator.from(result).validate()) {
            return;
        }
        if (!response.isOk()) {
            throw new RuntimeException("调用外部接口口发生异常:" + response);
        }
        JSONObject resultJson = JSON.parseObject(result, JSONObject.class);
        // yapi响应
        if (resultJson.containsKey(ERR_CODE) && resultJson.getInteger(ERR_CODE) != 0) {
            throw new RuntimeException("调用yapi接口发生错误：" + resultJson.getString(ERR_MSG));
        }
    }

    private String concatUri(String uri){
        return yapiAddress + uri;
    }
}
