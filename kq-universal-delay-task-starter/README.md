# 延时任务Delay-Task

## 一、简介

### 1.1 概述

Delay-Task作为一个延时任务组件，主要用于解决简单系统需要延时任务问题。

> 暂只支持单实例

### 1.2 为什么使用它

在日常开发中，常常遇到需要延时执行的需求：执行A任务后，在15分钟后执行B任务，如订单超时取消。

解决方案有三种：

1、使用juc中的延时队列

优点：jdk自带工具，使用方便

缺点：任务未持久化，服务重启后任务丢失

2、数据库+定时任务

优点：任务持久化，保证任务可执行。

缺点：任务执行时间不精确，如果定时任务的周期为5s，那么任务执行时间的误差为(0,5]

3、消息队列MQ

优点：任务持久化，执行时间保证精确

缺点：引入中间件，增加系统复杂度。

由于三种方式各有优劣，于是我诞生了一个想法：将MQ中的延时队列功能做成一个组件，让服务自己生产消息，自己消费，相当于2、3两种方案的结合体，既能保证任务的可靠性，也能保证任务执行的精确性，岂不美哉！

### 1.3 特性

1、简单：在方法上加上注解即可达到延时任务效果

2、精准：延时任务的调度时间误差在1秒之间

3、轻量：使用时间轮数据结构，占用内存极低

4、吞吐量：调度模型使用多线程，实际并发量依赖于MySQL的性能

## 二、快速入门

1、引入依赖
```java
        <dependency>
            <groupId>com.github.lzj960515</groupId>
            <artifactId>kq-universal-delay-task-starter</artifactId>
            <version>${revision}</version>
        </dependency>
```

2、执行数据库脚本，脚本位置：

```sql
CREATE TABLE `delay_task` (
                              `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                              `name` varchar(50) NOT NULL DEFAULT '' COMMENT '任务名：任务的名称，与执行任务的方法对应，用于执行任务时寻找执行任务的方法',
                              `description` varchar(128) DEFAULT '' COMMENT '任务描述',
                              `info` varchar(512) NOT NULL DEFAULT '' COMMENT '任务信息：放置执行任务所需的参数信息',
                              `execute_time` bigint(20) NOT NULL COMMENT '执行时间 时间戳',
                              `execute_status` tinyint(4) NOT NULL COMMENT '执行状态：1.创建 2.执行中 3.执行成功 4.执行失败',
                              `execute_message` varchar(256) DEFAULT '' COMMENT '执行结果信息',
                              `real_execute_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '实际执行时间 时间戳',
                              `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              PRIMARY KEY (`id`),
                              KEY `idx_etime` (`execute_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

3、编写延时任务

```java
@Component
public class DemoJob {

    private static final Logger log = LoggerFactory.getLogger(DemoJob.class);

    @DelayTask(name = "demoJob")
    public void job(String info) {
        log.info("延迟任务「job」被调用了, 参数:{} 当前时间：{}", info, LocalDateTime.now());
        DelayTaskHelper.handleSuccess();
    }
}
```
> info参数为保存延时任务时的任务信息

4、在业务逻辑中保存延时任务

```java
public class SingleDelayTaskTest {

    @Resource
    private DelayTaskTemplate delayTaskTemplate;

    @Test
    public void testDelayTask() throws IOException {
        delayTaskTemplate.save("demoJob", i + "", LocalDateTime.now().plusSeconds(10+i), "测试任务");
    }
}
```

> demoJob为任务名称，保存时与注解中的name相对应

## 三、其他

### 3.1 关于保存方法

保存方法有两种

```java
    /**
     * 指定时间执行延迟任务
     * @param taskName 任务名称
     * @param info 任务信息
     * @param executeTime 执行时间
     * @param description 描述
     */
    public void save(@NonNull String taskName, String info, @NonNull LocalDateTime executeTime, String description)
```

```java
    /**
     * 指定时间执行延迟任务
     * @param taskName 任务名称
     * @param info 任务信息
     * @param time 延迟时间
     * @param unit 时间单位
     * @param description 描述
     */
    public void save(@NonNull String taskName, String info, @NonNull long time, TimeUnit unit, String description)
```

### 3.2 清理已执行成功的任务

当任务已经被执行成功时，可以自主选择是否从数据库清除。

配置：

```yaml
delay-task:
  task-retention-days: 30
  concurrency: 100
```

> task-retention-days 保留任务天数，30表示只保留30天内的任务记录，如果不配置或者配置为-1则永不清理
> concurrency 5秒内调用的任务数，用于控制实际业务中在5秒内可处理的任务数



## feature
- 分布式时任务加锁保证一致性

