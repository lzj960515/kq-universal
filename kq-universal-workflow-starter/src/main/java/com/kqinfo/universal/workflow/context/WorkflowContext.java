package com.kqinfo.universal.workflow.context;

import org.springframework.core.NamedThreadLocal;

import java.util.Map;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
public class WorkflowContext {

	private WorkflowContext(){}

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


	private static final NamedThreadLocal<Map<String, Object>> VARIABLES_CONTEXT = new NamedThreadLocal<>("args context");

	public static Map<String, Object> getVariables() {
		return VARIABLES_CONTEXT.get();
	}

	public static void setVariables(Map<String, Object> variables) {
		VARIABLES_CONTEXT.set(variables);
	}

	public static void removeVariables() {
		VARIABLES_CONTEXT.remove();
	}
}
