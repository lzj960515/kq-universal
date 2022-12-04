package com.kqinfo.universal.yapi.util;

import com.alibaba.fastjson.JSONArray;
import com.kqinfo.universal.yapi.annotation.YapiParameter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * @author Zijian Liao
 * @since 2.13.0
 */
public final class YapiMock {

    private YapiMock() {
    }


    public static String generateMock(Type parameterizedType) {
        return generateMock(parameterizedType, 1);
    }

    public static String generateMock(Type parameterizedType, int deep) {
        if (parameterizedType instanceof ParameterizedTypeImpl) {
            ParameterizedTypeImpl paramCls = (ParameterizedTypeImpl) parameterizedType;
            Class<?> rawType = paramCls.getRawType();
            Type actualType = paramCls.getActualTypeArguments()[0];
            if (Collection.class.isAssignableFrom(rawType)) {
                String json5 = generateMock((Class<?>) actualType, deep);
                return "[" + json5 + "]";
            } else {
                Json5Object json5Object = new Json5Object(deep);
                Field[] declaredFields = rawType.getDeclaredFields();
                if(declaredFields.length != 0){
                    ReflectionUtils.doWithFields(rawType, declaredField -> {
                        if (Modifier.isStatic(declaredField.getModifiers())) {
                            return;
                        }
                        Type type = declaredField.getGenericType();
                        // 暂时写死判断泛型为T
                        if ("T".equals(type.getTypeName())) {
                            if (Void.class == actualType || void.class == actualType) {
                                return;
                            }
                            String json5 = generateMock(actualType, deep + 1);
                            json5Object.put(declaredField.getName(), json5, declaredField.getName());
                        }
                        else if (type.getTypeName().contains("<T>")) {
                            if (Void.class == actualType || void.class == actualType) {
                                return;
                            }
                            String json5 = generateMock(actualType, deep + 1);
                            if(Collection.class.isAssignableFrom(declaredField.getType())){
                                json5Object.put(declaredField.getName(), "[" + json5 + "]", declaredField.getName());
                            }else {
                                json5Object.put(declaredField.getName(), json5, declaredField.getName());
                            }
                        }
                        else {
                            json5Object.put(declaredField.getName(), generateMock(declaredField.getName(), declaredField.getType(), type, deep), declaredField.getName());
                        }
                    });
                }else {
                    ReflectionUtils.doWithMethods(rawType, declaredMethod -> {
                        if(Modifier.isStatic(declaredMethod.getModifiers())){
                            return;
                        }
                        String methodName = declaredMethod.getName();
                        if (methodName.startsWith("get")) {
                            String name = StringUtils.uncapitalize(methodName.substring(3));
                            Type type = declaredMethod.getGenericReturnType();
                            // 暂时写死判断泛型为T
                            if (type.getTypeName().contains("<T>")) {
                                if (Void.class == actualType || void.class == actualType) {
                                    return;
                                }
                                String json5 = generateMock(actualType, deep + 1);
                                if(Collection.class.isAssignableFrom(declaredMethod.getReturnType())){
                                    json5Object.put(name, "[" + json5 + "]", name);
                                }else {
                                    json5Object.put(name, json5, name);
                                }
                            } else {
                                json5Object.put(name, generateMock(name, declaredMethod.getReturnType(), type, deep), name);
                            }
                        }
                    });
                }
                return json5Object.getData();
            }
        } else {
            return generateMock((Class<?>) parameterizedType, deep);
        }
    }

    /**
     * 默认为自定义对象
     *
     * @param clazz cls
     * @return JSONObject
     */
    public static String generateMock(Class<?> clazz, int deep) {
        if(JSONArray.class.isAssignableFrom(clazz)){
            return "";
        }
        if(BeanUtils.isSimpleProperty(clazz)){
            return generateMock("a", clazz, clazz, deep);
        }
        Json5Object json5Object = new Json5Object(deep);
        ReflectionUtils.doWithFields(clazz, field -> {
            if (Modifier.isStatic(field.getModifiers())) {
                return;
            }
            YapiParameter yapiParameter = field.getAnnotation(YapiParameter.class);
            if (yapiParameter != null) {
                if (yapiParameter.hidden()) {
                    return;
                }
                // 字段名
                String desc = yapiParameter.value();
                String mock = yapiParameter.mock();
                if (StringUtils.hasText(mock)) {
                    json5Object.put(field.getName(), mock, desc);
                } else {
                    json5Object.put(field.getName(), YapiMock.generateMock(field.getName(), field.getType(), field.getGenericType(), deep), desc);
                }
            } else {
                json5Object.put(field.getName(), YapiMock.generateMock(field.getName(), field.getType(), field.getGenericType(), deep), field.getName());
            }
        });
        return json5Object.getData();
    }

    public static String generateMock(String name, Class<?> type, Type genericType, int deep) {
        if (type.isPrimitive()) {
            if(name.toLowerCase().contains("code")){
                return "0";
            }
            if(name.toLowerCase().contains("total")){
                return "10";
            }
            if(name.toLowerCase().contains("size")){
                return "10";
            }
            if(name.toLowerCase().contains("current")){
                return "1";
            }
            if(name.toLowerCase().contains("page")){
                return "1";
            }
            return "0";
        }
        if (Number.class.isAssignableFrom(type)) {
            if (Long.class.isAssignableFrom(type) || Integer.class.isAssignableFrom(type)) {
                return "@natural";
            }
            return "@float";
        }
        if (CharSequence.class.isAssignableFrom(type)) {
            return strMock(name);
        }
        if (Iterable.class.isAssignableFrom(type)) {
            // 判断里面的泛型
            String json5 = generateMock((Class<?>) getType(genericType), deep + 1);
            return "[" + json5 + "]";
        }
        if (Map.class.isAssignableFrom(type)) {
            return "{}";
        }
        if (LocalDate.class.isAssignableFrom(type)) {
            return "@date";
        }
        if (LocalDateTime.class.isAssignableFrom(type) || Date.class.isAssignableFrom(type)) {
            return "@datetime";
        }
        if(Boolean.class.isAssignableFrom(type)){
            return "@boolean";
        }
        if(BeanUtils.isSimpleProperty(type)){
            // 兜底
            return "";
        }
        // 说明是自定义类，继续解析
        return generateMock(type, deep);
    }

    public static String strMock(String field) {
        if (field.toLowerCase().contains("id")) {
            return "@id";
        }
        if (field.toLowerCase().contains("message") || field.toLowerCase().contains("msg")){
            return "success";
        }
        if (field.toLowerCase().contains("name")) {
            return "@cname";
        }
        if (field.toLowerCase().contains("address") || field.toLowerCase().contains("addr")) {
            return "@county(true)";
        }
        return "@word(5)";
    }

    private static Type getType(Type type){
        if(type instanceof ParameterizedTypeImpl){
            ParameterizedTypeImpl paramCls = (ParameterizedTypeImpl) type;
            return paramCls.getActualTypeArguments()[0];
        }else {
            return type;
        }

    }

}
