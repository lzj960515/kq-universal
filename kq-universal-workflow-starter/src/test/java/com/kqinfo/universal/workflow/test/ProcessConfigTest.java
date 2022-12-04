package com.kqinfo.universal.workflow.test;

import com.kqinfo.universal.workflow.constant.NodeEnum;
import com.kqinfo.universal.workflow.constant.StatusEnum;
import com.kqinfo.universal.workflow.core.WorkflowInvoker;
import com.kqinfo.universal.workflow.dto.ExecuteTaskDto;
import com.kqinfo.universal.workflow.dto.ProcessDefConfig;
import com.kqinfo.universal.workflow.dto.ProcessStartDto;
import com.kqinfo.universal.workflow.model.EndNode;
import com.kqinfo.universal.workflow.model.StartNode;
import com.kqinfo.universal.workflow.model.TaskNode;
import com.kqinfo.universal.workflow.model.TransitionNode;
import com.kqinfo.universal.workflow.model.WorkNode;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * 测试使用Java代码创建流程
 *
 * @author Zijian Liao
 * @since 2.0.0
 */
@SpringBootTest
public class ProcessConfigTest {

    private static final String TEST = "test";
    @Resource
    private WorkflowInvoker workflowInvoker;

    /**
     * 初始化流程
     */
    @Test
    public void testInitProcess() {
        ProcessDefConfig processDefConfig = new ProcessDefConfig();
        processDefConfig.setName(TEST);
        processDefConfig.setDesc("测试流程");
        processDefConfig.setCallUri("'/process/test/detail?id='+#businessId");

        List<WorkNode> workNodes = new ArrayList<>();

        // 开始节点
        {
            StartNode startNode = new StartNode();
            startNode.setName("start1");
            startNode.setType(NodeEnum.START.name().toLowerCase(Locale.ROOT));

            TransitionNode transitionNode = new TransitionNode();
            transitionNode.setTo("组长审核");
            startNode.setTransitions(Collections.singletonList(transitionNode));

            workNodes.add(startNode);
        }
        // 任务节点1
        {
            TaskNode taskNode = new TaskNode();
            taskNode.setRole("admin");
            taskNode.setSequence(1);
            taskNode.setCallUri("'/process/simple/task?id='+#businessId");
            taskNode.setName("组长审核");
            taskNode.setType(NodeEnum.TASK.name().toLowerCase(Locale.ROOT));

            TransitionNode transitionNode = new TransitionNode();
            transitionNode.setTo("部门审核");
            taskNode.setTransitions(Collections.singletonList(transitionNode));
            workNodes.add(taskNode);
        }
        // 任务节点2
        {
            TaskNode taskNode = new TaskNode();
            taskNode.setRole("admin");
            taskNode.setSequence(2);
            taskNode.setName("部门审核");
            taskNode.setType(NodeEnum.TASK.name().toLowerCase(Locale.ROOT));

            TransitionNode transitionNode = new TransitionNode();
            transitionNode.setTo("end1");
            taskNode.setTransitions(Collections.singletonList(transitionNode));
            workNodes.add(taskNode);
        }
        // 结束节点
        {
            EndNode endNode = new EndNode();
            endNode.setName("end1");
            endNode.setType(NodeEnum.END.name().toLowerCase(Locale.ROOT));

            workNodes.add(endNode);
        }

        processDefConfig.setNodes(workNodes);
        workflowInvoker.initProcess(processDefConfig);
    }

    @Test
    public void testStart() {
        ProcessStartDto processStartDto = new ProcessStartDto();
        processStartDto.setTenantId(2L);
        processStartDto.setProcessDefName(TEST);
        processStartDto.setProcessName("张三申请发布文章");
        processStartDto.setCreator("1");
        processStartDto.setBusinessId("1");
        final Integer approveStatus = workflowInvoker.startProcessByName(processStartDto);
        MatcherAssert.assertThat(approveStatus, CoreMatchers.is(StatusEnum.START.value()));
    }

    @Test
    public void testExecuteTask() {
        {
            ExecuteTaskDto executeTaskDto = new ExecuteTaskDto();
            executeTaskDto.setProcessDefName(TEST);
            executeTaskDto.setBusinessId("1");
            executeTaskDto.setOperator("1");
            executeTaskDto.setTenantId(2L);
            final Integer approveStatus = workflowInvoker.executeTask(executeTaskDto);
            MatcherAssert.assertThat(approveStatus, CoreMatchers.is(StatusEnum.START.value()));
        }
        {
            ExecuteTaskDto executeTaskDto = new ExecuteTaskDto();
            executeTaskDto.setProcessDefName(TEST);
            executeTaskDto.setBusinessId("1");
            executeTaskDto.setOperator("2");
            executeTaskDto.setTenantId(2L);
            final Integer approveStatus = workflowInvoker.executeTask(executeTaskDto);
            MatcherAssert.assertThat(approveStatus, CoreMatchers.is(StatusEnum.END.value()));
        }
    }
}
