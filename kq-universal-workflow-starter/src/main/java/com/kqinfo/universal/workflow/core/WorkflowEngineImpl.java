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
 * ???????????? ??????
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
			// ??????????????????????????????
			final List<JSONObject> processList = readProcessFile();
			if (processList.isEmpty()) {
				log.warn("??????[workflow]?????????????????????????????????");
				return;
			}
			// ?????????????????????
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
		// ???????????????????????????????????????????????????
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
				throw new WorkflowException("["+processDefName+"]??????????????????????????????????????????????????????????????????");
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
	 * ??????????????????????????????, ????????????????????????
	 * @param processDefName ??????????????????
	 * @param processName ????????????
	 * @param creator ?????????
	 * @param businessId ??????id
	 * @param variables ????????????
	 * @return ?????????????????????
	 */
	private Task doStartProcessAndExecuteFirstTask(String processDefName, String processName, String creator,
												   String businessId, Map<String, Object> variables, boolean cancel){
		// ???????????????????????????????????????????????????
		checkExistProcessAndCancel(processDefName, businessId, creator, cancel);
		// ?????????????????????????????????????????????????????????????????????????????????????????????????????????
		Map<String, Object> taskVariables = new HashMap<>(variables);
		variables.remove(WorkflowConstant.NEXT_TASK_ASSIGNEE);
		startProcess(processDefName, processName, creator, businessId, variables);
		// ????????????
		final Task task = taskService.complete(processDefName, businessId, creator, TaskStatusEnum.SUBMIT.value(), "???????????????????????????");
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
		// ????????????????????????
		final String context = process.getContext();
		final List<WorkNode> workNodes = workflowHandler.parse(context);
		// issue-4 ????????????????????????????????????????????????
		Map<String, Object> taskVariables = new HashMap<>(variables);
		variables.remove(WorkflowConstant.NEXT_TASK_ASSIGNEE);
		// ??????????????????
		final ProcessInstance processInstance = processInstanceService.create(processName,
				workflowHandler.getCallUri(context, businessId), process.getId(), businessId, creator,
				JSONUtil.toJsonStr(variables));
		// ????????????????????????????????????
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
		String reason = "????????????";
		if (StringUtils.hasText(executeTaskDto.getReason())) {
			reason += "????????????" + executeTaskDto.getReason();
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
	 * ????????????
	 * @param processName ????????????
	 * @param businessId ??????id
	 * @param operator ???????????????
	 * @param reason ??????
	 * @param variables ????????????
	 * @return ?????????????????????
	 */
	private Task doExecuteTask(String processName, String businessId, String operator, String reason, Map<String, Object> variables){
		// ????????????
		final Task task = taskService.complete(processName, businessId, operator, TaskStatusEnum.APPROVED.value(), reason);
		final Execution execution = getExecution(task, variables);
		// ????????????????????????????????????
		// ???????????????????????????
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
			throw new WorkflowException("?????????????????????");
		}
		// ?????????????????????
		final Task task = taskService.complete(processName, businessId, operator, TaskStatusEnum.REJECT.value(), reason);
		// ???????????????????????????????????????
		processInstanceService.complete(task.getInstanceId(), StatusEnum.REJECT.value());
		return getApproveStatus(task.getInstanceId());
	}

	private Integer getApproveStatus(Long processInstanceId){
		// ??????????????????
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
		final Task task = taskService.complete(processName, businessId, operator, TaskStatusEnum.CANCEL.value(), "???????????????????????????????????????");
		// ????????????????????????????????????
		processInstanceService.complete(task.getInstanceId(), StatusEnum.CANCEL.value());
	}


	final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

	private List<JSONObject> readProcessFile() throws IOException {
		// ?????????workflow??????????????????
		final Resource[] resources = resourcePatternResolver.getResources("classpath*:/workflow/**.json");
		List<JSONObject> processList = new ArrayList<>(resources.length);

		for (Resource resource : resources) {
			try (InputStream inputStream = resource.getInputStream()) {
				final String json = StreamUtils.copyToString(inputStream, Charset.defaultCharset());
				log.info("???????????????{}", json);
				JSONObject processObject = JSONUtil.parseObj(json);
				processList.add(processObject);
			}
			catch (IOException e) {
				log.error("??????workflow???????????????", e);
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
