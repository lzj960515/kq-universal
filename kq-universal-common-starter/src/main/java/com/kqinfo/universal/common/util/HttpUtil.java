package com.kqinfo.universal.common.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.kqinfo.universal.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * http请求工具
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Slf4j
public final class HttpUtil {

    /**
     * 微信响应信息
     */
    private static final String ERR_CODE = "errcode";
    private static final String ERR_MSG = "errmsg";

    private static final String REQUEST_LOG_PATTERN_1 = "请求外部接口, 请求路径：{}";
    private static final String REQUEST_LOG_PATTERN_2 = "请求外部接口，请求路径：{}, 请求参数: {}";
    private static final String REQUEST_LOG_PATTERN_3 = "请求外部接口，请求路径：{}, {}, 请求参数: {}";

    private HttpUtil() {
        throw new IllegalStateException("Utility class");
    }


    public static String get(String url) {
        log.info(REQUEST_LOG_PATTERN_1, url);
        HttpResponse response = HttpRequest.get(url).execute();
        validResult(response);
        return response.body();
    }

    public static String get(String url, @Nonnull Map<String, String> params) {
        Assert.notEmpty(params, "params is empty, please use get(String url) method");
        log.info(REQUEST_LOG_PATTERN_2, url, JSONUtil.toJsonStr(params));
        HttpResponse response = HttpRequest.get(appendUrl(url, params)).execute();
        validResult(response);
        return response.body();
    }

    public static String post(String url) {
        log.info(REQUEST_LOG_PATTERN_1, url);
        HttpResponse response = HttpRequest.post(url).execute();
        validResult(response);
        return response.body();
    }

    public static String post(String url, Object body) {
        log.info(REQUEST_LOG_PATTERN_2, url, JSONUtil.toJsonStr(body));
        HttpResponse response = HttpRequest.post(url).body(JSONUtil.toJsonStr(body)).execute();
        validResult(response);
        return response.body();
    }

    public static String post(String url, @Nonnull Map<String, String> params, Object body) {
        Assert.notEmpty(params, "params is empty, please use post(String url, Object body) method");
        log.info(REQUEST_LOG_PATTERN_3, url, JSONUtil.toJsonStr(params), JSONUtil.toJsonStr(body));
        HttpResponse response = HttpRequest.post(appendUrl(url, params)).body(JSONUtil.toJsonStr(body)).execute();
        validResult(response);
        return response.body();
    }

    public static String rawRequest(String url, String method, @Nullable Map<String, String> headers, @Nullable String body){
        log.info(REQUEST_LOG_PATTERN_2, url, JSONUtil.toJsonStr(body));
        HttpRequest httpRequest = determineHttpRequest(url, method);
        if(!CollectionUtils.isEmpty(headers)){
            httpRequest.headerMap(headers, false);
        }
        if(StringUtils.hasText(body)){
            httpRequest.body(body);
        }
        HttpResponse response = httpRequest.execute();
        validResult(response);
        return response.body();
    }

    public static String postForm(String url, @Nonnull Map<String, Object> params){
        HttpResponse response = HttpRequest.post(url).form(params).execute();
        validResult(response);
        return response.body();
    }


    //---------------私有方法---------------

    private static HttpRequest determineHttpRequest(String url, String method){
        if (HttpMethod.GET.name().equals(method)) {
            return HttpRequest.get(url);
        }
        return HttpRequest.post(url);
    }

    private static String appendUrl(String url, Map<String, String> params) {
        String with = "&";
        String equal = "=";
        StringBuilder sb = new StringBuilder();
        params.forEach((key, value) -> sb.append(key).append(equal).append(value).append(with));
        return url.concat("?").concat(sb.substring(0, sb.length() - 1));
    }

    private static void validResult(HttpResponse response) {
        String result = response.body();
        log.info("请求外部接口，响应数据：{}", result);
        if (!JSONUtil.isJson(result)) {
            return;
        }
        JSONObject resultJson = JSONUtil.toBean(result, JSONObject.class);
        if (!response.isOk()) {
            String message = message(resultJson);
            throw new BusinessException(message);
        }

        // 微信响应
        if (resultJson.containsKey(ERR_CODE) && resultJson.getInt(ERR_CODE) != 0) {
            throw new BusinessException("调用微信接口发生错误：" + resultJson.getStr(ERR_MSG));
        }
    }

    private static String message(JSONObject resultJson) {
        log.error("调用外部接口口发生异常：{}", resultJson);
        return "调用外部接口口发生异常";
    }

}
