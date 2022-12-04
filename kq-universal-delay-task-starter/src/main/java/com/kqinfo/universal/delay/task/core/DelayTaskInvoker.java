package com.kqinfo.universal.delay.task.core;

import com.kqinfo.universal.delay.task.constant.ExecuteStatus;
import com.kqinfo.universal.delay.task.core.domain.DelayTaskInfo;
import com.kqinfo.universal.delay.task.dao.DelayTaskDao;
import com.kqinfo.universal.delay.task.helper.DelayTaskExecuteInfo;
import com.kqinfo.universal.delay.task.helper.DelayTaskHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * 延迟任务调度器
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
public final class DelayTaskInvoker implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(DelayTaskInvoker.class);

    private final DelayTaskDao delayTaskDao;
    private final Long taskId;

    public DelayTaskInvoker(DelayTaskDao delayTaskDao, Long taskId) {
        this.delayTaskDao = delayTaskDao;
        this.taskId = taskId;
    }

    @Override
    public void run() {
        this.invoke();
    }

    private void invoke() {
        DelayTaskInfo delayTaskInfo = delayTaskDao.findById(taskId);
        // 判断任务状态
        if(!ExecuteStatus.NEW.status().equals(delayTaskInfo.getExecuteStatus())){
            return;
        }
        // 1.修改任务状态为执行中
        delayTaskDao.updateStatus(taskId, ExecuteStatus.EXECUTE.status(), "");
        // 2.调用延迟任务
        this.doInvoke(delayTaskInfo);
        // 3.更新任务状态
        DelayTaskExecuteInfo executeInfo = DelayTaskHelper.getExecuteInfo();
        if(executeInfo != null){
            delayTaskDao.updateStatus(taskId, executeInfo.getExecuteStatus(), executeInfo.getExecuteMessage());
        }else {
            delayTaskDao.updateStatus(taskId, ExecuteStatus.SUCCESS.status(), "");
        }
    }

    private void doInvoke(DelayTaskInfo delayTaskInfo) {
        // 1.从上下文中取出任务对应的方法
        DelayTaskMethod delayTaskMethod = DelayTaskContext.find(delayTaskInfo.getName());
        if(delayTaskMethod == null){
            log.error("not found delay task method for name: {}", delayTaskInfo.getName());
            DelayTaskHelper.handleFail("not found delay task method for name: " + delayTaskInfo.getName());
            return;
        }
        try {
            // 2.调用
            delayTaskMethod.getMethod().invoke(delayTaskMethod.getBean(), delayTaskInfo.getInfo());
        } catch (InvocationTargetException e){
            log.error(e.getTargetException().getMessage(), e.getTargetException());
            DelayTaskHelper.handleFail(e.getTargetException().getMessage());
        }
        catch (Throwable e) {
            log.error(e.getMessage(), e);
            DelayTaskHelper.handleFail(e.getMessage());
        }
    }
}
