package com.kqinfo.universal.delay.task.core;

import com.kqinfo.universal.delay.task.dao.DelayTaskDao;
import com.kqinfo.universal.delay.task.thread.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
public class DelayTaskRunner implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(DelayTaskRunner.class);

    private final DelayTaskDao delayTaskDao;
    private final List<Long> taskIds;

    public DelayTaskRunner(DelayTaskDao delayTaskDao, List<Long> taskIds){
        this.delayTaskDao = delayTaskDao;
        this.taskIds = taskIds;
    }

    @Override
    public void run() {
        // 1.查询任务
        for (Long taskId : taskIds) {
            ThreadPool.DELAY_TASK_INVOKER.execute(new DelayTaskInvoker(delayTaskDao, taskId));
        }
    }
}
