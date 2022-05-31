package com.kqinfo.universal.test.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.StringUtils;

/**
 * @author Zijian Liao
 * @since 2.0.0
 */
public final class ApiUtil {

    /**
     * 校验get请求url返回数据与传入数据结构是否一致
     * @param url url
     * @param source 需要比对的数据
     */
    public static void checkGetApi(String url, Object source){
        String resultStr = HttpUtil.get(url);
        doCheck(resultStr, source);
    }

    /**
     * 校验post请求url返回数据与传入数据结构是否一致
     * @param url url
     * @param param 请求参数
     * @param source 需要比对的数据
     */
    public static void checkPostApi(String url, Object param, Object source){
        String resultStr = HttpUtil.post(url, JSON.toJSONString(param));
        doCheck(resultStr, source);
    }

    /**
     * 校验put请求url返回数据与传入数据结构是否一致
     * @param url url
     * @param param 请求参数
     * @param source 需要比对的数据
     */
    public static void checkPutApi(String url, Object param, Object source){
        String resultStr = HttpRequest.put(url).body(JSON.toJSONString(param)).execute().body();
        doCheck(resultStr, source);
    }

    /**
     * 校验delete请求url返回数据与传入数据结构是否一致
     * @param url url
     * @param source 需要比对的数据
     */
    public static void checkDeleteApi(String url, Object source){
        String resultStr = HttpRequest.delete(url).execute().body();
        doCheck(resultStr, source);
    }

    private static void doCheck(String resultStr, Object source){
        if(StringUtils.isEmpty(resultStr)){
            throw new RuntimeException("请求url返回数据为空");
        }
        JSONObject resultJson = JSON.parseObject(resultStr);
        JSONObject sourceJson = (JSONObject) JSON.toJSON(source);
        checkJson(sourceJson, resultJson);
    }

    /**
     * 检查源json结构是否与目标json结构一致
     * @param source 源json
     * @param target 目标json
     */
    private static void checkJson(JSONObject source, JSONObject target){
        for (String key : target.keySet()) {
            if(!source.containsKey(key)){
                throw new RuntimeException("缺失字段：" + key);
            }
            final Object sourceObj = source.get(key);

            // 判断是否需要递归处理
            if(isJson(sourceObj)){
                // 如果是个json，直接进行递归处理
                checkJson((JSONObject) sourceObj, target.getJSONObject(key));
            }else if(sourceObj instanceof JSONArray){
                // 如果是jsonArray，并且里面是jsonObject 取第一个json
                final Object o = ((JSONArray)sourceObj).get(0);
                if(isJson(o)){
                    checkJson((JSONObject)o, target.getJSONArray(key).getJSONObject(0));
                }
            }
        }
    }

    private static boolean isJson(Object o){
        return o instanceof JSONObject;
    }

}
