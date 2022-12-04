package com.kqinfo.universal.enums.util;

import com.kqinfo.universal.enums.constant.DefaultEnum;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * @author Zijian Liao
 * @since 2.5.0
 */
public final class KqEnumUtil {

    private KqEnumUtil(){}

    /**
     * 使用枚举类中filed1字段获取field2字段
     * 如有枚举类
     * public enum TestEnum {
     *     TEST("test1","desc1"),
     *     TEST2("test2","desc2");
     *
     *     private final String code;
     *     private final String desc;
     * }
     * 通过test1获取对应的desc1， 则为getField1ByField2(TestEnum.class, "desc", "code", "test1")
     * @return filed2Value
     */
    public static Object getField1ByField2(Class<? extends Enum<?>> enumClass, String field1, String field2, Object field1Value){
        if (!enumClass.isEnum()) {
            throw new RuntimeException(enumClass.getName() + " is not enum class");
        }
        Field field1Field = ReflectionUtils.findField(enumClass, field1);
        if (field1Field == null){
            throw new RuntimeException("no such field ["+ field1 + "] in " + enumClass.getName());
        }
        ReflectionUtils.makeAccessible(field1Field);
        Field field2Field = ReflectionUtils.findField(enumClass, field2);
        if(field2Field == null){
            throw new RuntimeException("no such field ["+ field2 + "] in " + enumClass.getName());
        }
        ReflectionUtils.makeAccessible(field2Field);
        Enum<?>[] enumConstants = enumClass.getEnumConstants();
        for (Enum<?> enumConstant : enumConstants) {
            Object value = ReflectionUtils.getField(field2Field, enumConstant);
            if(field1Value.equals(value)){
                return ReflectionUtils.getField(field1Field, enumConstant);
            }
        }
        return null;
    }


    public static Object getDescByCode(Class<? extends Enum<?>> enumClass, Object code){
        return getField1ByField2(enumClass, DefaultEnum.DESC, DefaultEnum.CODE, code);
    }

    public static Object getCodeByDesc(Class<? extends Enum<?>> enumClass, Object desc){
        return getField1ByField2(enumClass, DefaultEnum.CODE, DefaultEnum.DESC, desc);
    }

}
