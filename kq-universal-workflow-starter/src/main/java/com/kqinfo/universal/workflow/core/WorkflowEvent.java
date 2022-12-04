package com.kqinfo.universal.workflow.core;

import lombok.Data;

/**
 * @author Zijian Liao
 * @since 2.6.0
 */
@Data
public class WorkflowEvent {

    private String businessId;

    private String operator;
}
