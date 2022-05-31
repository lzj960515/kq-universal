package com.kqinfo.universal.workflow.core;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.kqinfo.universal.workflow.constant.StatusEnum;
import com.kqinfo.universal.workflow.constant.TaskStatusEnum;
import com.kqinfo.universal.workflow.constant.WorkflowConstant;
import com.kqinfo.universal.workflow.context.WorkflowContext;
import com.kqinfo.universal.workflow.domain.HistoryProcessInstance;
import com.kqinfo.universal.workflow.domain.Process;
import com.kqinfo.universal.workflow.domain.ProcessInstance;
import com.kqinfo.universal.workflow.domain.Task;
import com.kqinfo.universal.workflow.dto.Assignee;
import com.kqinfo.universal.workflow.dto.ExecuteTaskDto;
import com.kqinfo.universal.workflow.dto.ProcessStartDto;
import com.kqinfo.universal.workflow.exception.WorkflowException;
import com.kqinfo.universal.workflow.model.WorkNode;
import com.kqinfo.universal.workflow.service.HistoryProcessInstanceService;
import com.kqinfo.universal.workflow.service.ProcessInstanceService;
import com.kqinfo.universal.workflow.service.ProcessService;
import com.kqinfo.universal.workflow.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程引擎 实现
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowEngineImpl implements WorkflowEngine, ApplicationListener<ApplicationReadyEvent> {

	private final ProcessService processService;

	private final ProcessInstanceService processInstanceService;

	private final TaskService taskService;

	private final HistoryProcessInstanceService historyProcessInstanceService;
	private final WorkflowListenerExecutor workflowListenerExecutor;

	@Autowired
	private WorkflowHandler workflowHandler;


	@Override
	public void initProcess() {
		try {
			// 读取出所有的流程定义
			final List<JSONObject> processList = readProcessFile();
			if (processList.isEmpty()) {
				log.warn("未在[workflow]目录下找到任何流程定义");
				return;
			}
			// 遍历进行初始化
			processList.forEach(this::doInitProcess);
		}
		catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public Integer startProcessByName(ProcessStartDto processStartDto, boolean cancel) {
		WorkflowContext.setTenantId(processStartDto.getTenantId());
		Map<String, Object> variables = populateAssignee(processStartDto.getNextTaskAssigneeList(), processStartDto.getVariables());
		variables = variables == null ? new HashMap<>(2) : variables;
		WorkflowContext.setVariables(variables);
		// 检查是否存在原流程，存在则自动取消
		checkExistProcessAndCancel(processStartDto.getProcessDefName(), processStartDto.getBusinessId(), processStartDto.getCreator(), cancel);
		ProcessInstance processInstance = startProcess(processStartDto.getProcessDefName(), processStartDto.getProcessName(), processStartDto.getCreator(), processStartDto.getBusinessId(), variables);
		WorkflowContext.removeTenantId();
		WorkflowContext.removeVariables();
		return getApproveStatus(processInstance.getId());
	}

	private void checkExistProcessAndCancel(String processDefName, String businessId, String creator, boolean cancel){
		final Task task = taskService.getTask(businessId, processDefName);
		if(task != null){
			if(cancel){
				cancelTask(processDefName, businessId, creator);
			}else {
				throw new WorkflowException("["+processDefName+"]流程正在进行中，不可对正在审批的数据进行修改");
			}
		}
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public Integer startProcessAndExecuteFirstTask(ProcessStartDto processStartDto, boolean cancel) {
		WorkflowContext.setTenantId(processStartDto.getTenantId());
		Map<String, Object> variables = populateAssignee(processStartDto.getNextTaskAssigneeList(), processStartDto.getVariables());
		variables = variables == null ? new HashMap<>(2) : variables;
		WorkflowContext.setVariables(variables);
		final Task task = this.doStartProcessAndExecuteFirstTask(processStartDto.getProcessDefName(),
				processStartDto.getProcessName(), processStartDto.getCreator(), processStartDto.getBusinessId(), variables, cancel);
		WorkflowContext.removeTenantId();
		WorkflowContext.removeVariables();
		return getApproveStatus(task.getInstanceId());
	}

	/**
	 * 通过流程名称启动流程, 并执行第一个任务
	 * @param processDefName 流程定义名称
	 * @param processName 流程名称
	 * @param creator 发起人
	 * @param businessId 业务id
	 * @param variables 流程变量
	 * @return 当前执行的任务
	 */
	private Task doStartProcessAndExecuteFirstTask(String processDefName, String processName, String creator,
												   String businessId, Map<String, Object> variables, boolean cancel){
		// 检查是否存在原流程，存在则自动取消
		checkExistProcessAndCancel(processDefName, businessId, creator, cancel);
		// 由于这里需要自动提交审核，将指定下一个任务的受理人移到提交审核的任务中
		Map<String, Object> taskVariables = new HashMap<>(variables);
		variables.remove(WorkflowConstant.NEXT_TASK_ASSIGNEE);
		startProcess(processDefName, processName, creator, businessId, variables);
		// 完成任务
		final Task task = taskService.complete(processDefName, businessId, creator, TaskStatusEnum.SUBMIT.value(), "提交审核，等待审批");
		final Execution execution = getExecution(task, taskVariables);
		workflowHandler.execute(task.getName(), execution);
		return task;
	}

	private ProcessInstance startProcess(String processDefName, String processName, String creator, String businessId,
			Map<String, Object> variables) {
		final Process process = processService.getProcessByName(processDefName);
		if (process == null) {
			throw new WorkflowException(processDefName + " not found!");
		}
		// 解析出所有的节点
		final String context = process.getContext();
		final List<WorkNode> workNodes = workflowHandler.parse(context);
		// issue-4 下一任务受理人不存储于流程实例中
		Map<String, Object> taskVariables = new HashMap<>(variables);
		variables.remove(WorkflowConstant.NEXT_TASK_ASSIGNEE);
		// 创建流程实例
		final ProcessInstance processInstance = processInstanceService.create(processName,
				workflowHandler.getCallUri(context, businessId), process.getId(), businessId, creator,
				JSONUtil.toJsonStr(variables));
		// 启动流程，保存第一个任务
		Execution execution = new Execution();
		execution.setWorkflowEngine(this);
		execution.setProcess(process);
		execution.setProcessInstance(processInstance);
		execution.setWorkNodes(workNodes);
		execution.setVariables(taskVariables);
		workflowHandler.start(execution);
		return processInstance;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public Integer executeTask(ExecuteTaskDto executeTaskDto) {
		WorkflowContext.setTenantId(executeTaskDto.getTenantId());
		String reason = "审核通过";
		if (StringUtils.hasText(executeTaskDto.getReason())) {
			reason += "，原因：" + executeTaskDto.getReason();
		}
		Map<String, Object> variables = populateAssignee(executeTaskDto.getNextTaskAssigneeList(), executeTaskDto.getVariables());
		variables = variables == null ? new HashMap<>(2) : variables;
		WorkflowContext.setVariables(variables);
		Task task = this.doExecuteTask(executeTaskDto.getProcessDefName(), executeTaskDto.getBusinessId(), executeTaskDto.getOperator(), reason, variables);
		WorkflowContext.removeVariables();
		WorkflowContext.removeTenantId();
		return getApproveStatus(task.getInstanceId());
	}

	private Map<String, Object> populateAssignee(List<Assignee> nextTaskAssigneeList, Map<String, Object> variables){
		if(nextTaskAssigneeList != null && !nextTaskAssigneeList.isEmpty()){
			if(variables == null){
				variables = new HashMap<>(2);
			}
			final Map<String, String> assigneeMap = nextTaskAssigneeList.stream().collect(Collectors.toMap(Assignee::getUserId, Assignee::getUsername));
			variables.put(WorkflowConstant.NEXT_TASK_ASSIGNEE, assigneeMap);
		}
		return variables;
	}

	/**
	 * 执行任务
	 * @param processName 流程定义
	 * @param businessId 业务id
	 * @param operator 任务受理人
	 * @param reason 原因
	 * @param variables 流程变量
	 * @return 当前执行的任务
	 */
	private Task doExecuteTask(String processName, String businessId, String operator, String reason, Map<String, Object> variables){
		// 完成任务
		final Task task = taskService.complete(processName, businessId, operator, TaskStatusEnum.APPROVED.value(), reason);
		final Execution execution = getExecution(task, variables);
		// 任务完成后执行对应的事件
		// 取到当前任务的节点
		final WorkNode node = workflowHandler.getNode(task.getName(), execution.getWorkNodes());
		workflowListenerExecutor.execute(node.getEvent(), businessId);

		workflowHandler.execute(task.getName(), execution);
		return task;
	}

	private Execution getExecution(Task task, Map<String, Object> variables) {
		final ProcessInstance processInstance = processInstanceService.getById(task.getInstanceId());
		final Process process = processService.getById(processInstance.getProcessId());
		Execution execution = new Execution();
		execution.setWorkflowEngine(this);
		execution.setProcess(process);
		execution.setProcessInstance(processInstance);
		execution.setWorkNodes(workflowHandler.parse(process.getContext()));
		execution.setTask(task);
		JSONObject args = JSONUtil.parseObj(processInstance.getVariable());
		if(variables != null && !variables.isEmpty()){
			args.putAll(variables);
		}
		execution.setVariables(args);
		return execution;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public Integer rejectTask(String processName, String businessId, String operator, String reason) {
		if(StringUtils.isEmpty(reason)){
			throw new WorkflowException("请填写驳回原因");
		}
		// 将当前任务完成
		final Task task = taskService.complete(processName, businessId, operator, TaskStatusEnum.REJECT.value(), reason);
		// 结束流程，结束状态为已驳回
		processInstanceService.complete(task.getInstanceId(), StatusEnum.REJECT.value());
		return getApproveStatus(task.getInstanceId());
	}

	private Integer getApproveStatus(Long processInstanceId){
		// 查询流程状态
		final HistoryProcessInstance historyProcessInstance = historyProcessInstanceService.getById(processInstanceId);
		return historyProcessInstance.getStatus();
	}

	@Override
	public Integer cancelProcess(String processDefName, String businessId, String operator) {
		final Task task = taskService.getTask(businessId, processDefName);
		if(task == null){
			return StatusEnum.CANCEL.value();
		}
		cancelTask(processDefName, businessId, operator);

		return getApproveStatus(task.getInstanceId());
	}

	private void cancelTask(String processName, String businessId, String operator) {
		final Task task = taskService.complete(processName, businessId, operator, TaskStatusEnum.CANCEL.value(), "重新发起流程，自动取消审核");
		// 结束流程，结束状态为取消
		processInstanceService.complete(task.getInstanceId(), StatusEnum.CANCEL.value());
	}


	final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

	private List<JSONObject> readProcessFile() throws IOException {
		// 读取出workflow下的所有文件
		final Resource[] resources = resourcePatternResolver.getResources("classpath*:/workflow/**.json");
		List<JSONObject> processList = new ArrayList<>(resources.length);

		for (Resource resource : resources) {
			try (InputStream inputStream = resource.getInputStream()) {
				final String json = StreamUtils.copyToString(inputStream, Charset.defaultCharset());
				log.info("加载流程：{}", json);
				JSONObject processObject = JSONUtil.parseObj(json);
				processList.add(processObject);
			}
			catch (IOException e) {
				log.error("读取workflow文件失败：", e);
				throw e;
			}
		}
		return processList;
	}

	private void doInitProcess(JSONObject processObject) {
		final String name = processObject.getStr("name");

		final Process process = processService.getProcessByName(name);
		String jsonStr = processObject.toString();
		String sourceSign = DigestUtil.md5Hex(jsonStr);
		int version = 1;
		if (process != null) {
			String sign = process.getSign();
			if (sourceSign.equals(sign)) {
				return;
			}
			version = process.getVersion() + 1;
		}
		Process insertProcess = new Process();
		insertProcess.setName(name);
		insertProcess.setDescription(processObject.getStr("desc"));
		insertProcess.setContext(jsonStr);
		insertProcess.setVersion(version);
		insertProcess.setSign(sourceSign);
		insertProcess.setCreateTime(LocalDateTime.now());
		insertProcess.setUpdateTime(LocalDateTime.now());
		processService.save(insertProcess);
	}

	@Override
	public ProcessInstanceService processInstanceService() {
		return processInstanceService;
	}

	@Override
	public void onApplicationEvent(@Nullable ApplicationReadyEvent event) {
		initProcess();
	}

}
