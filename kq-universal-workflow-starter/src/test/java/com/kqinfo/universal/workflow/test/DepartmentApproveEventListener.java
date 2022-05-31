package com.kqinfo.universal.workflow.test;

import com.kqinfo.universal.workflow.core.WorkflowEvent;
import com.kqinfo.universal.workflow.core.WorkflowListener;
import org.springframework.stereotype.Component;

/**
 * @author Zijian Liao
 * @since 2.6.0
 */
@Component("departmentApproveEvent")
public class DepartmentApproveEventListener implements WorkflowListener  {

    @Override
    public void onWorkflowEvent(WorkflowEvent event) {
        System.out.println("收到departmentApproveEvent事件, businessId:" + event.getBusinessId());
    }
}
