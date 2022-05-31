package com.kqinfo.universal.enums.core;

import com.kqinfo.universal.enums.annotation.KqEnum;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.beans.Introspector;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 扫描注解下的包
 *
 * @author Zijian Liao
 * @since 2.5.0
 */
public final class KqEnumScanner {

    private static final ResourcePatternResolver PATTERN_RESOLVER = new PathMatchingResourcePatternResolver();
    private static final MetadataReaderFactory METADATA_READER_FACTORY = new CachingMetadataReaderFactory();

    public static Map<String, Class<? extends Enum<?>>> scanKqEnum(String basePackage) {
        Map<String, Class<? extends Enum<?>>> enumMap = new HashMap<>(16);
        try {
            basePackage = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    ClassUtils.convertClassNameToResourcePath(basePackage) + "/**/*.class";
            final Resource[] resources = PATTERN_RESOLVER.getResources(basePackage);
            for (Resource resource : resources) {
                final MetadataReader metadataReader = METADATA_READER_FACTORY.getMetadataReader(resource);
                final AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
                AnnotationAttributes kqEnumAttrs = AnnotationAttributes
                        .fromMap(annotationMetadata.getAnnotationAttributes(KqEnum.class.getName()));
                if (kqEnumAttrs != null) {
                    final Class<?> enumClass = ClassUtils.forName(metadataReader.getClassMetadata().getClassName(), ClassUtils.getDefaultClassLoader());
                    if (enumClass.isEnum()) {
                        // 字典名称
                        String enumName = kqEnumAttrs.getString("value");
                        if (StringUtils.isEmpty(enumName)) {
                            enumName = Introspector.decapitalize(enumClass.getSimpleName());
                        }
                        enumMap.put(enumName, (Class<? extends Enum<?>>) enumClass);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return enumMap;
    }
}
