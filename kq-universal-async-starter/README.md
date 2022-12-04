# 异步组件
功能
- Spring的Async的功能
- 线程池管理器
## Spring的Async的功能
1. 引入依赖

   ```xml
   <dependency>
     <groupId>com.github.lzj960515</groupId>
     <artifactId>kq-universal-async-starter</artifactId>
     <version>1.1.0</version>
   </dependency>
   ```

2. 编辑配置

   ```yaml
    kq:
      async:
        core-pool-size: 20
        max-pool-size: 100
        queue-capacity: 200
        allow-core-thread-time-out: false
        keep-alive-seconds: 60
        thread-name-prefix: kq-async-
        rejected-execution-handler: java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy
   ```
   > corePoolSize: 核心线程数， 默认20
   >
   > queue-capacity: 队列容量， 当核心线程数达到满额时，任务将放置到队列里，默认200
   >
   > max-pool-size: 最大线程数，当队列满额且线程池中线程数小于最大线程数时，继续创建线程处理，直到达到max-pool-size， 默认100
   >
   > allow-core-thread-time-out: 允许核心线程超时， 默认false
   >
   > keep-alive-seconds: 空闲线程超时时间，线程在一定时间内未接到任务处理（空闲），自动关闭。 默认60
   >
   > thread-name-prefix: 线程名前缀， 默认kq-async-
   >
   > rejected-execution-handler: 当max-pool-size和queue-capacity超出时的拒绝策略， 默认使用当前线程运行任务

3. 使用
    在需要异步的方法中加上@Async注解即可，例：
    ```java
        @Async
        public void async(){
            System.out.println("async start!");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("async end!");
        }
    ```
   > 注意：请不要再同类方法中调用异步方法，这样做异步方法并不会生效
## 线程池管理器
通过ThreadPoolManager获取的线程池，在应用进程结束时，将自动shutdown所有manager中的线程池

例子
```java
    @Test
    public void testHok(){
        // test为线程池名称
        final ExecutorService executor = ThreadPoolManager.getExecutor("test");
        executor.execute(() -> {
            System.out.println("开始执行任务");
            ThreadUtil.sleep(5000);
            System.out.println("执行任务结束");
        });
        System.out.println("测试完成");
    }
```
> 此时进程将等待线程池中任务执行完毕后结束