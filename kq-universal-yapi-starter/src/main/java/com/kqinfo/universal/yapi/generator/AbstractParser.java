package com.kqinfo.universal.yapi.generator;

import com.kqinfo.universal.yapi.domain.ApiInfo;
import com.kqinfo.universal.yapi.domain.CatInfo;
import com.kqinfo.universal.yapi.domain.ControllerInfo;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Zijian Liao
 * @since 2.13.0
 */
public abstract class AbstractParser {

    public final List<ControllerInfo> parse(List<Class<?>> classList){
        List<ControllerInfo> controllerInfoList = new ArrayList<>(classList.size());
        for (Class<?> cls : classList) {
            if(!isSupport(cls)){
                continue;
            }
            String catName = parseCatInfo(cls);
            List<ApiInfo> apiInfoList = parseApiInfo(cls);
            controllerInfoList.add(new ControllerInfo(catName, apiInfoList));
        }
        return controllerInfoList;
    }

    protected void parsePathAndMethod(Method method, ApiInfo apiInfo, String baseUri){
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        if(requestMapping != null){
            setUriAndMethod(apiInfo, baseUri, getUri(requestMapping.value()), requestMapping.method()[0].name());
        }
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        if(getMapping != null){
            setUriAndMethod(apiInfo, baseUri, getUri(getMapping.value()) , "GET");
        }
        PutMapping putMapping = method.getAnnotation(PutMapping.class);
        if(putMapping != null){
            setUriAndMethod(apiInfo, baseUri, getUri(putMapping.value()), "PUT");
        }
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        if(postMapping != null){
            setUriAndMethod(apiInfo, baseUri, getUri(postMapping.value()), "POST");
        }
        DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
        if(deleteMapping != null){
            setUriAndMethod(apiInfo, baseUri, getUri(deleteMapping.value()), "DELETE");
        }
    }

    private String getUri(String[] value){
        if (value.length > 0) {
            return value[0];
        }
        return "";
    }

    protected void setUriAndMethod(ApiInfo apiInfo, String baseUri, String uri, String reqMethod){
        if(StringUtils.hasText(uri) && !uri.startsWith("/")){
            uri = "/" + uri;
        }
        apiInfo.setPath(baseUri + uri);
        apiInfo.setMethod(reqMethod);
    }
    /**
     * 是否支持
     * @param cls clazz
     * @return true or false
     */
    abstract boolean isSupport(Class<?> cls);

    /**
     * 解析分类信息
     * @param cls clazz
     * @return 分类信息
     */
    abstract String parseCatInfo(Class<?> cls);

    /**
     * 解析接口信息 接口路径，请求方式，请求参数，响应参数
     * @param cls clazz
     * @return controller中的接口信息列表
     */
    abstract List<ApiInfo> parseApiInfo(Class<?> cls);
}
