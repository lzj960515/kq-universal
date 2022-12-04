package com.kqinfo.universal.workflow.test;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kqinfo.universal.workflow.core.WorkflowInvoker;
import com.kqinfo.universal.workflow.dto.ApproveProgressDto;
import com.kqinfo.universal.workflow.dto.TaskLogDto;
import com.kqinfo.universal.workflow.dto.TodoTaskDto;
import com.kqinfo.universal.workflow.dto.TodoTaskPageDto;
import com.kqinfo.universal.workflow.dto.TodoTaskPageParam;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * 测试查询数据
 *
 * @author Zijian Liao
 * @since 2.4.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class QueryTest {

    private static final String TEST = "测试流程";

    @Resource
    private WorkflowInvoker workflowInvoker;

    @Test
    public void testTodoTask() {
        final Integer hasTask = workflowInvoker.hasTask("1", TEST, "1");
        MatcherAssert.assertThat(hasTask, CoreMatchers.is(1));
    }

    @Test
    public void testPageTodoTask() {
        TodoTaskPageParam todoTaskPageParam = new TodoTaskPageParam();
        todoTaskPageParam.setCurrent(1);
        todoTaskPageParam.setSize(10);
        final IPage<TodoTaskPageDto> result = workflowInvoker.pageTodoTask("2", "1", todoTaskPageParam);
        MatcherAssert.assertThat(result.getRecords().size(), CoreMatchers.is(1));
    }

    @Test
    public void testListLog() {
        final List<TaskLogDto> taskLogDtos = workflowInvoker.listTaskLog("1", TEST);
        MatcherAssert.assertThat(taskLogDtos.size(), CoreMatchers.not(0));
    }

    @Test
    public void testProgress() {
        final List<ApproveProgressDto> approveProgressDtos = workflowInvoker.approveProgress("1", TEST);
        MatcherAssert.assertThat(approveProgressDtos.size(), CoreMatchers.not(0));
    }

    @Test
    public void testListTodoTaskByDesc() {
        List<TodoTaskDto> todoTaskDtos = workflowInvoker.listTodoTaskByDesc("2", "1", TEST);
        MatcherAssert.assertThat(todoTaskDtos.size(), CoreMatchers.not(0));
    }
}
