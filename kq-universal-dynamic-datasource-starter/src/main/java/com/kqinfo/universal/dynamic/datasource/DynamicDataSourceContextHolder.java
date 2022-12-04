package com.kqinfo.universal.dynamic.datasource;

import org.springframework.core.NamedThreadLocal;

/**
 *
 * @author Zijian Liao
 * @since 2.20.0
 */
public final class DynamicDataSourceContextHolder {

    private static final ThreadLocal<Integer> LOOKUP_KEY_HOLDER = new NamedThreadLocal<Integer>("dynamic-datasource");

    private DynamicDataSourceContextHolder() {
    }

    /**
     * 获得当前线程数据源
     *
     * @return 数据源名称
     */
    public static Integer get() {
        return LOOKUP_KEY_HOLDER.get();
    }

    /**
     * 设置当前线程数据源
     *
     * @param ds 数据源id
     */
    public static void set(Integer ds) {
        LOOKUP_KEY_HOLDER.set(ds);
    }

    public static void remove() {
        LOOKUP_KEY_HOLDER.remove();
    }

}
