# 工作流引擎

## 功能

- 支持单链路审核
- 支持条件审核
- 支持指定审核人审核

## 使用方式

### 引入依赖

```xml
<dependency>
    <groupId>com.github.lzj960515</groupId>
    <artifactId>kq-universal-workflow-starter</artifactId>
</dependency>
```

### 定义流程

在`resources`目录下新建目录`workflow`, 在`workflow`目录下新建流程定义文件，json格式，如：
```
resources
└── workflow
    └── simple.json
```
以下为一个简单的流程设计：开始 -> 组长审核 -> 部门审核 -> 结束

```json
{
  "name":"测试流程",
  "callUri": "'/process/test/detail?id='+#businessId",
  "nodes":[
    {
      "type":"start",
      "name":"start1",
      "transitions":[
        {
          "name":"transition1",
          "type":"transition",
          "to":"组长审核"
        }
      ]
    },
    {
      "type":"task",
      "name":"组长审核",
      "assignee":"",
      "role": "admin",
      "sequence": "1",
      "transitions":[
        {
          "name":"transition1",
          "type":"transition",
          "to":"部门审核"
        }
      ]
    },
    {
      "type":"task",
      "name":"部门审核",
      "assignee":"",
      "role": "admin",
      "sequence": "2",
      "transitions":[
        {
          "name":"transition1",
          "type":"transition",
          "to":"end1"
        }
      ]
    },
    {
      "type":"end",
      "name":"end1"
    }
  ]
}
```

#### 名称解释

name: 流程定义名称

callUri: 回调地址，一般用于在待办任务中跳转到业务详情页, #businessId为业务id字段，固定写为#businessId

nodes: 节点内容

#### 节点内容

name: 节点名称

type：节点类型，当前共有5种类型：开始节点，任务节点，结束节点，条件节点，变迁节点

> name和type是每个节点的必备属性

transitions: 变迁信息，内容为变迁节点列表，用于指定该节点的下一个节点, 案例如下：

```json
{
  "name":"transition1",
  "type":"transition",
  "to":"组长审核"
}
```

to: 下一个节点的名称

如下信息表示为该节点为开始节点，节点名称为`start1`，它的下一个节点为`组长审核`

```json
{
  "type":"start",
  "name":"start1",
  "transitions":[
    {
      "name":"transition1",
      "type":"transition",
      "to":"组长审核"
    }
  ]
}
```

---

#### 任务节点

任务节点有以下特定字段：

assignee：任务受理人(一般为用户id)，多个以`,`号分隔，如："1,2,3"

role: 任务受理角色，多个以`,`号分隔，如："1,2,3"

> 组件中存在一个接口类需要使用者进行实现，用于传入受理人和受理角色获取相应的用户信息

sequence：排序字段，用于给任务排序

callUri: 页面跳转路径，同最外层的callUri，如果任务节点中配置了callUri，则使用任务节点中的，否则使用最外层的callUri

案例如下：

```json
{
  "type":"task",
  "name":"组长审核",
  "assignee":"1,2",
  "role": "admin",
  "sequence": "1",
  "callUri": "'/process/task/id='+#businessId",
  "transitions":[
    {
      "name":"transition1",
      "type":"transition",
      "to":"部门审核"
    }
  ]
}
```

#### 条件节点

一个常见的场景——请假审批，当请假天数小于3天时，只需组长审批即可，当大于3天时，还需要部门经理审批。而这便是条件节点所实现的功能。

案例如下：

```json
{
  "name":"请假审批",
  "callUri": "'/process/test/detail?id='+#businessId",
  "nodes":[
    {
      "type":"start",
      "name":"start1",
      "transitions":[
        {
          "name":"transition1",
          "type":"transition",
          "to":"组长审批"
        }
      ]
    },
    {
      "type":"task",
      "name":"组长审批",
      "assignee":"",
      "role": "user",
      "sequence": "1",
      "transitions":[
        {
          "to":"decision1"
        }
      ]
    },
    {
      "type":"decision",
      "name":"decision1",
      "expression": "",
      "transitions":[
        {
          "name":"transition1",
          "type":"transition",
          "expression": "#day<2",
          "to":"end1"
        },
        {
          "name":"transition2",
          "type":"transition",
          "expression": "#day>=2",
          "to":"部门审批"
        }
      ]
    },
    {
      "type":"task",
      "name":"部门审批",
      "assignee":"",
      "role": "admin",
      "sequence": "2",
      "transitions":[
        {
          "to":"end1"
        }
      ]
    },
    {
      "type":"end",
      "name":"end1"
    }
  ]
}
```

让我们把目光只关注到条件节点的内容

```json
{
  "type":"decision",
  "name":"decision1",
  "expression": "",
  "transitions":[
    {
      "name":"transition1",
      "type":"transition",
      "expression": "#day<2",
      "to":"end1"
    },
    {
      "name":"transition2",
      "type":"transition",
      "expression": "#day>=2",
      "to":"部门审批"
    }
  ]
}
```

外部的expression：用于解析出下一个节点名称，优先级最高。

transitions的expression：当该表达式为true时，该变迁节点生效。如day为1时，下一节点为end1

> 当外部的expression为空或者找不到节点时，则使用transitions的expression进行解析，都找不到则抛出异常

以上案例同样可以写为：

```json
{
  "type":"decision",
  "name":"decision1",
  "expression": "#day<2 ? 'end1' : '部门审批'"
}
```

#### 事件

有时，我们想要在某个节点结束时调用对应的业务逻辑，为此，流程引擎支持事件机制：在节点完成时发布事件，使用者监听对应事件，从而完成对应的业务。

首先，需要在流程定义的节点中配置`event`字段，目前支持开始事件，任务事件，结束事件

案例如下：

```json
{
  "name":"测试事件",
  "callUri": "'/process/test/detail?id='+#businessId",
  "nodes":[
    {
      "type":"start",
      "name":"start1",
      "event": "startEvent",
      "transitions":[
        {
          "name":"transition1",
          "type":"transition",
          "to":"组长审核"
        }
      ]
    },
    {
      "type":"task",
      "name":"组长审核",
      "assignee":"",
      "role": "admin",
      "sequence": "1",
      "event": "leaderEvent",
      "transitions":[
        {
          "to":"部门审核"
        }
      ]
    },
    {
      "type":"task",
      "name":"部门审核",
      "assignee":"",
      "role": "admin",
      "sequence": "2",
      "event": "departmentApproveEvent",
      "transitions":[
        {
          "to":"end1"
        }
      ]
    },
    {
      "type":"end",
      "name":"end1",
      "event": "endEvent"
    }
  ]
}
```

> event配置的事件名是由使用者自己定义的，对应着实现`WorkflowListener`接口的beanName

如案例中的`leaderEvent`事件举例

```java
@Component("leaderEvent")
public class LeaderEventListener implements WorkflowListener  {

    @Override
    public void onWorkflowEvent(WorkflowEvent event) {
        System.out.println("收到leaderEvent事件, businessId:" + event.getBusinessId());
    }
}
```

> 为了可观性，我在`Component`注解中指定了beanName为leaderEvent，不指定的话则默认为类名：leaderEventListener

以上配置和实现类都具备后，流程引擎将在节点结束之后发布事件。

### 实现接口

由于流程在流转时需要有基本的用户信息：用户id, 用户名。所以需要使用者实现接口`WorkflowUserService`，用于流程引擎在某个时刻进行调用。

接口内容如下：

```java
public interface WorkflowUserService {

    /**
     * 获取用户信息
     * @param userId 用户id
     * @return 用户信息
     */
    UserDto loadByUserId(String userId);

    /**
     * 获取用户信息列表
     * @param userIds 用户id列表
     * @return 用户信息列表
     */
    default List<UserDto> loadByUserIds(List<String> userIds){
        return Collections.emptyList();
    }

    /**
     * 使用角色获取用户信息列表
     * @param roles 角色列表
     * @return 用户信息
     */
    default List<UserDto> loadByRole(List<String> roles){
        return Collections.emptyList();
    }
}
```

### API

流程引擎中的所有API都封装在了`WorkflowInvoker`接口中

```java
public interface WorkflowInvoker {

    /**
     * 通过流程定义名称启动流程
     * @param processStartDto 流程启动参数
     * @return 流程状态 1.审核中 2.审核通过 3.驳回
     */
    Integer startProcessByName(ProcessStartDto processStartDto);

    /**
     * 通过流程定义名称启动流程, 并执行第一个任务
     * @param processStartDto 流程启动参数
     * @return 流程状态 1.审核中 2.审核通过 3.驳回
     */
    Integer startProcessAndExecuteFirstTask(ProcessStartDto processStartDto);

    /**
     * 执行任务
     * @param executeTaskDto 执行任务参数
     * @return 流程状态 1.审核中 2.审核通过 3.驳回
     */
    Integer executeTask(ExecuteTaskDto executeTaskDto);

    /**
     * 驳回任务
     * @param processDefName 流程定义名称
     * @param businessId 业务id
     * @param operator 任务受理人
     * @param reason 原因
     * @return 流程状态 1.审核中 2.审核通过 3.驳回
     */
    Integer rejectTask(String processDefName, String businessId, String operator, String reason);

    /**
     * 查询待办任务列表
     * @param tenantId 租户id
     * @param operator 用户id
     * @param processDefName 流程名称
     * @return 办任务列表
     */
    List<TodoTaskDto> listTodoTask(String tenantId, String operator, String processDefName);

    /**
     * 分页查询待办任务列表
     * @param tenantId 租户id
     * @param operator 任务受理人
     * @param todoTaskPageParam 参数
     * @return 待办任务列表
     */
    IPage<TodoTaskPageDto> pageTodoTask(String tenantId, String operator, TodoTaskPageParam todoTaskPageParam);

    /**
     * 是否有任务
     * @param operator 任务受理人
     * @param processDefName 流程名称
     * @param businessId 业务id
     * @return 是否有任务 1.是 0.否
     */
    Integer hasTask(String operator, String processDefName, String businessId);

    /**
     * 查询审核日志
     * @param businessId 业务id
     * @param processDefName 流程定义名称
     * @return 审核日志
     */
    List<TaskLogDto> listTaskLog(String businessId, String processDefName);

    /**
     * 查询审核进度
     * @param businessId 业务id
     * @param processDefName 流程定义名称
     * @return 审核进度
     */
    List<ApproveProgressDto> approveProgress(String businessId, String processDefName);

}
```

[入门案例](src/test/java/com/kqinfo/universal/workflow/test/SimpleTest.java)

[请假案例](src/test/java/com/kqinfo/universal/workflow/test/LeaveTest.java)

[查询待办任务](src/test/java/com/kqinfo/universal/workflow/test/QueryTest.java)

[事件案例](src/test/java/com/kqinfo/universal/workflow/test/EventTest.java)