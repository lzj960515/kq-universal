package com.kqinfo.universal.enums.core;

import com.kqinfo.universal.enums.annotation.KqCode;
import com.kqinfo.universal.enums.annotation.KqDesc;
import com.kqinfo.universal.enums.annotation.KqEnumScan;
import com.kqinfo.universal.enums.constant.DefaultEnum;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Zijian Liao
 * @since 2.5.0
 */
public class KqEnumScannerRegistrar implements ImportBeanDefinitionRegistrar {


    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes mapperScanAttrs = AnnotationAttributes
                .fromMap(importingClassMetadata.getAnnotationAttributes(KqEnumScan.class.getName()));
        if (mapperScanAttrs != null) {
            final List<String> basePackages = Arrays.stream(mapperScanAttrs.getStringArray("basePackages")).filter(StringUtils::hasText)
                    .collect(Collectors.toList());
            for (String basePackage : basePackages) {
                // 扫描包下的所有枚举
                final Map<String, Class<? extends Enum<?>>> enumMap = KqEnumScanner.scanKqEnum(basePackage);
                // 注册
                registerKqEnum(enumMap);
            }
        }
    }

    private void registerKqEnum(Map<String, Class<? extends Enum<?>>> enumMap) {
        enumMap.forEach((enumName, enumClass) -> {
            //  在枚举中找到code字段
            Field code = findCode(enumClass);
            //  在枚举中找到desc字段
            Field desc = findDesc(enumClass);
            //  将数据存入KqEnumStorage中
            Map<Object, Object> objectMap = new HashMap<>(16);
            for (Enum<?> e : enumClass.getEnumConstants()) {
                Object codeValue = ReflectionUtils.getField(code, e);
                Object descValue = ReflectionUtils.getField(desc, e);
                objectMap.put(codeValue, descValue);
            }
            KqEnumStorage.put(enumName, objectMap);
        });
    }

    private Field findCode(Class<? extends Enum<?>> enumClass) {
        final Field field = ReflectionUtils.findField(enumClass, DefaultEnum.CODE);
        if (field != null) {
            //开启能获取私有属性的权限
            field.setAccessible(true);
            return field;
        }
        for (Field f : enumClass.getDeclaredFields()) {
            if (f.isAnnotationPresent(KqCode.class)) {
                f.setAccessible(true);
                return f;
            }
        }
        throw new RuntimeException("未在枚举类[" + enumClass.getSimpleName() + "]找到" + DefaultEnum.CODE + "字段");
    }

    private Field findDesc(Class<? extends Enum<?>> enumClass) {
        final Field field = ReflectionUtils.findField(enumClass, DefaultEnum.DESC);
        if (field != null) {
            //开启能获取私有属性的权限
            field.setAccessible(true);
            return field;
        }
        for (Field f : enumClass.getDeclaredFields()) {
            if (f.isAnnotationPresent(KqDesc.class)) {
                f.setAccessible(true);
                return f;
            }
        }
        throw new RuntimeException("未在枚举类[" + enumClass.getSimpleName() + "]找到" + DefaultEnum.DESC + "字段");
    }

}
