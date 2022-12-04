package com.kqinfo.universal.yapi.generator;

import com.kqinfo.universal.yapi.annotation.YapiApi;
import com.kqinfo.universal.yapi.annotation.YapiOperation;
import com.kqinfo.universal.yapi.annotation.YapiParameter;
import com.kqinfo.universal.yapi.annotation.YapiParameterObject;
import com.kqinfo.universal.yapi.domain.ApiInfo;
import com.kqinfo.universal.yapi.domain.ReqHeader;
import com.kqinfo.universal.yapi.domain.ReqParam;
import com.kqinfo.universal.yapi.domain.ReqQuery;
import com.kqinfo.universal.yapi.util.YapiMock;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Zijian Liao
 * @since 2.13.0
 */
public class YapiParser extends AbstractParser {
    @Override
    boolean isSupport(Class<?> cls) {
        return cls.isAnnotationPresent(YapiApi.class) && !cls.getAnnotation(YapiApi.class).hidden();
    }

    @Override
    String parseCatInfo(Class<?> cls) {
        YapiApi yapiApi = cls.getAnnotation(YapiApi.class);
        return yapiApi.value();
    }

    @Override
    List<ApiInfo> parseApiInfo(Class<?> cls) {
        // 1.解析类头上是否有RequestMapping注解
        RequestMapping requestMapping = cls.getAnnotation(RequestMapping.class);
        String baseUri = "";
        if (requestMapping != null) {
            baseUri = requestMapping.value()[0];
            if (baseUri.endsWith("/")) {
                baseUri = baseUri.substring(0, baseUri.length() - 1);
            }
        }
        // 2.遍历所有方法
        Method[] methods = cls.getDeclaredMethods();
        List<ApiInfo> apiInfoList = new ArrayList<>(methods.length);
        for (Method method : methods) {
            ApiInfo apiInfo = new ApiInfo();
            apiInfo.setStatus("undone");
            apiInfo.setRes_body_type("json");
            YapiOperation yapiOperation = method.getAnnotation(YapiOperation.class);
            if (yapiOperation == null || yapiOperation.hidden()) {
                continue;
            }
            // 接口名称
            String apiName = yapiOperation.value();
            apiInfo.setTitle(apiName);
            apiInfo.setDesc(apiName);
            // 接口路径、请求方法
            super.parsePathAndMethod(method, apiInfo, baseUri);
            // 请求参数
            this.parseReqInfo(method, apiInfo);
            // 响应参数
            Type genericReturnType = method.getGenericReturnType();
            apiInfo.setRes_body(YapiMock.generateMock(genericReturnType));
            apiInfoList.add(apiInfo);
        }
        return apiInfoList;
    }

    private void parseReqInfo(Method method, ApiInfo apiInfo) {
        // 请求参数 1.req_body_other @RequestBody注解 2.req_query 没有注解或者是@RequestParam注解，3.req_params @PathVariable注解
        List<ReqParam> reqParams = new ArrayList<>(1);
        List<ReqQuery> reqQueryList = new ArrayList<>(5);
        List<ReqQuery> reqFormList = new ArrayList<>(1);
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            boolean isRequestBody = false;
            boolean isPathVariable = false;
            boolean isYapiParameterObject = false;
            YapiParameter yapiParameter = null;
            for (Annotation annotation : parameter.getAnnotations()) {
                if (annotation instanceof RequestBody) {
                    isRequestBody = true;
                } else if (annotation instanceof PathVariable) {
                    isPathVariable = true;
                } else {
                    // get请求
                    if (annotation instanceof YapiParameterObject) {
                        isYapiParameterObject = true;
                    } else if (annotation instanceof YapiParameter) {
                        yapiParameter = (YapiParameter) annotation;
                    }
                }
            }
            if (isRequestBody) {
                this.setJsonReq(apiInfo, parameter);
            } else if (isPathVariable) {
                ReqParam reqParam = new ReqParam();
                reqParam.setName(parameter.getName());
                reqParam.setDesc(parameter.getName());
                reqParams.add(reqParam);
            } else if (isYapiParameterObject) {
                // body 解析类中的所有字段
                reqQueryList.addAll(buildReqQuery(parameter));
            } else {
                if(yapiParameter != null && yapiParameter.hidden()){
                    continue;
                }
                this.setRawReq(yapiParameter, parameter, apiInfo, reqQueryList, reqFormList);
            }
        }
        apiInfo.setReq_params(reqParams);
        apiInfo.setReq_query(reqQueryList);
        apiInfo.setReq_body_form(reqFormList);
    }

    private void setRawReq(YapiParameter yapiParameter, Parameter parameter, ApiInfo apiInfo, List<ReqQuery> reqQueryList, List<ReqQuery> reqFormList) {
        ReqQuery reqQuery = new ReqQuery();
        reqQuery.setName(parameter.getName());
        if (yapiParameter != null) {
            if (!yapiParameter.hidden()) {
                reqQuery.setDesc(yapiParameter.value());
                reqQuery.setRequired(yapiParameter.required() ? 1 : 0);
            }
        } else {
            reqQuery.setDesc(parameter.getName());
            reqQuery.setRequired(0);
        }
        if (MultipartFile.class.isAssignableFrom(parameter.getType())) {
            // 表单请求
            apiInfo.setReq_body_type("form");
            apiInfo.setReq_headers(Collections.singletonList(ReqHeader.form()));
            reqFormList.add(reqQuery);
        } else {
            reqQueryList.add(reqQuery);
        }
    }

    private void setJsonReq(ApiInfo apiInfo, Parameter parameter) {
        String json5 = YapiMock.generateMock(parameter.getParameterizedType());
        apiInfo.setReq_body_other(json5);
        apiInfo.setReq_headers(Collections.singletonList(ReqHeader.applicationJson()));
        apiInfo.setReq_body_type("json");
    }

    private List<ReqQuery> buildReqQuery(Parameter parameter) {
        Type parameterizedType = parameter.getParameterizedType();
        if (parameterizedType instanceof ParameterizedTypeImpl) {
            ParameterizedTypeImpl paramCls = (ParameterizedTypeImpl) parameterizedType;
            Class<?> rawType = paramCls.getRawType();
            if (Collection.class.isAssignableFrom(rawType)) {
                Type actualType = paramCls.getActualTypeArguments()[0];
                return buildReqQuery((Class<?>) actualType);
            } else {
                // 不是集合的话，那我也不知道是啥，碰到了再看
                return Collections.emptyList();
            }
        } else {
            return buildReqQuery((Class<?>) parameterizedType);
        }
    }

    private List<ReqQuery> buildReqQuery(Class<?> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        List<ReqQuery> queryList = new ArrayList<>(declaredFields.length);
        ReflectionUtils.doWithFields(clazz, field -> {
            if (Modifier.isStatic(field.getModifiers())) {
                return;
            }
            ReqQuery reqQuery = new ReqQuery();
            reqQuery.setName(field.getName());
            reqQuery.setRequired(0);
            YapiParameter yapiParameter = field.getAnnotation(YapiParameter.class);
            if (yapiParameter != null) {
                if (yapiParameter.hidden()) {
                    return;
                }
                reqQuery.setDesc(yapiParameter.value());
                reqQuery.setRequired(yapiParameter.required() ? 1 : 0);
            }
            queryList.add(reqQuery);
        });
        return queryList;
    }

}
