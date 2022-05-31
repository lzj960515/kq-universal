package com.kqinfo.universal.enums.annotation;

import com.kqinfo.universal.enums.core.KqEnumScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Zijian Liao
 * @since 2.4.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(KqEnumScannerRegistrar.class)
public @interface KqEnumScan {

    String[] basePackages() default {};
}
