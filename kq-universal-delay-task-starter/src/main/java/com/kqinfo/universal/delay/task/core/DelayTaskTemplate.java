package com.kqinfo.universal.delay.task.core;

import com.kqinfo.universal.delay.task.constant.ExecuteStatus;
import com.kqinfo.universal.delay.task.core.domain.DelayTaskInfo;
import com.kqinfo.universal.delay.task.dao.DelayTaskDao;
import com.kqinfo.universal.delay.task.util.TimeUtil;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 存储任务
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Component
public class DelayTaskTemplate {

    @Resource
    private DelayTaskDao delayTaskDao;

    /**
     * 存储任务
     * @param delayTaskInfo 任务信息
     */
    private void save(DelayTaskInfo delayTaskInfo) {
        Long executeTime = delayTaskInfo.getExecuteTime();
        boolean innerFiveSecond = TimeUtil.isInnerFiveSecond(executeTime);
        // 1.设置任务状态
        this.setStatus(delayTaskInfo, innerFiveSecond);
        // 2.将任务存入数据库
        delayTaskDao.save(delayTaskInfo);
        // 3.如果任务执行时间在5s内，放入时间轮中
        if(innerFiveSecond){
            TimeRing.put(executeTime, delayTaskInfo.getId());
        }
    }

    /**
     * 指定时间执行延迟任务
     * @param taskName 任务名称
     * @param info 任务信息
     * @param executeTime 执行时间
     * @param description 描述
     */
    public void save(@NonNull String taskName, String info, @NonNull LocalDateTime executeTime, String description){
        Assert.hasText(taskName, "taskName must not be null");
        Assert.notNull(executeTime, "executeTime must not be null");
        DelayTaskInfo delayTaskInfo = new DelayTaskInfo();
        delayTaskInfo.setName(taskName);
        delayTaskInfo.setInfo(info);
        delayTaskInfo.setExecuteTime(TimeUtil.localDateTime2Millis(executeTime));
        delayTaskInfo.setDescription(description);
        this.save(delayTaskInfo);
    }

    /**
     * 指定时间执行延迟任务
     * @param taskName 任务名称
     * @param info 任务信息
     * @param time 延迟时间
     * @param unit 时间单位
     * @param description 描述
     */
    public void save(@NonNull String taskName, String info, @NonNull long time, TimeUnit unit, String description){
        long executeTime = TimeUtil.convert2Millis(time, unit);
        DelayTaskInfo delayTaskInfo = new DelayTaskInfo();
        delayTaskInfo.setName(taskName);
        delayTaskInfo.setInfo(info);
        delayTaskInfo.setExecuteTime(executeTime);
        delayTaskInfo.setDescription(description);
        this.save(delayTaskInfo);
    }

    private void setStatus(DelayTaskInfo delayTaskInfo, boolean innerFiveSecond){
        // 判断任务执行时间
        if(innerFiveSecond){
            delayTaskInfo.setExecuteStatus(ExecuteStatus.EXECUTE.status());
        }
        else {
            delayTaskInfo.setExecuteStatus(ExecuteStatus.NEW.status());
        }
    }


}
