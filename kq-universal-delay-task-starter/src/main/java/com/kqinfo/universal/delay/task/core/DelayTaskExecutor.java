package com.kqinfo.universal.delay.task.core;

import com.kqinfo.universal.delay.task.config.DelayTaskProperties;
import com.kqinfo.universal.delay.task.core.domain.DelayTaskInfo;
import com.kqinfo.universal.delay.task.dao.DelayTaskDao;
import com.kqinfo.universal.delay.task.thread.ThreadPool;
import com.kqinfo.universal.delay.task.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 任务执行器
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Component
public class DelayTaskExecutor {

    private static final Logger log = LoggerFactory.getLogger(DelayTaskExecutor.class);

    @Resource
    private DelayTaskDao delayTaskDao;
    @Resource
    private DelayTaskProperties delayTaskProperties;

    public void start() {
        long now = System.currentTimeMillis();
        try {
            // 时间归整
            Thread.sleep(1000 - now % 1000);
        } catch (InterruptedException ignore) {
        }
        this.queryTask();
        this.executeTask();
        this.clearSuccessTask();
        log.info("延迟任务组件启动成功");
    }

    public void queryTask() {
        // 查询任务放入时间轮
        ThreadPool.DELAY_TASK_QUERY.scheduleAtFixedRate(() -> {
            // 1.查询5s内的任务放入时间轮中
            List<DelayTaskInfo> taskInfoList = delayTaskDao.findByExecuteTime(TimeUtil.getAfterFiveSecond());
            log.info("查询出延时任务数：{}", taskInfoList.size());
            for (DelayTaskInfo delayTaskInfo : taskInfoList) {
                // 2.判断时间
                long now = System.currentTimeMillis();
                long executeTime = delayTaskInfo.getExecuteTime();
                if(now >= executeTime){
                    // 2.1如果时间在这之前，直接执行任务
                    ThreadPool.DELAY_TASK_INVOKER.execute(new DelayTaskInvoker(delayTaskDao, delayTaskInfo.getId()));
                }else {
                    // 2.2否则将任务放入时间轮
                    TimeRing.put(executeTime, delayTaskInfo.getId());
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
    }
    private void executeTask() {
        // 处理时间轮中的任务
        ThreadPool.DELAY_TASK_EXECUTOR.scheduleAtFixedRate(() -> {
            // 1.从时间轮中取出该秒所有任务id
            List<Long> taskIds = TimeRing.pull();
            log.info("从时间轮中取出任务，任务数：{}", taskIds.size());
            // 2.执行任务
            ThreadPool.DELAY_TASK_WORKER.execute(new DelayTaskRunner(delayTaskDao, taskIds));
        }, 100, 1000, TimeUnit.MILLISECONDS);
    }

    private void clearSuccessTask(){
        int taskRetentionDays = delayTaskProperties.getTaskRetentionDays();
        if(taskRetentionDays <= 0){
            return;
        }
        ThreadPool.DELAY_TASK_CLEANER.scheduleAtFixedRate(() -> {
            LocalDate clearDate = LocalDate.now().plusDays(-taskRetentionDays);
            delayTaskDao.deleteByExecuteTime(TimeUtil.localDate2Millis(clearDate));
        }, 0, 1, TimeUnit.DAYS);
    }

}
