package com.kqinfo.universal.workflow.context;

import org.springframework.core.NamedThreadLocal;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
public class WorkflowContext {

    private WorkflowContext() {
    }

    /**
     * 租户id上下文
     */
    private static final NamedThreadLocal<Long> TENANT_CONTEXT = new NamedThreadLocal<>("tenant context");

    public static Long getTenantId() {
        return TENANT_CONTEXT.get();
    }

    public static void setTenantId(Long tenantId) {
        TENANT_CONTEXT.set(tenantId);
    }

    public static void removeTenantId() {
        TENANT_CONTEXT.remove();
    }

    /**
     * 操作人线程上下文
     */
    private static final NamedThreadLocal<String> OPERATE_CONTEXT = new NamedThreadLocal<>("operator context");

    public static String getOperator() {
        return OPERATE_CONTEXT.get();
    }

    public static void setOperator(String operator) {
        OPERATE_CONTEXT.set(operator);
    }

    public static void removeOperator() {
        OPERATE_CONTEXT.remove();
    }
}
