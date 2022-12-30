package com.kqinfo.universal.delay.task.test;

import com.kqinfo.universal.delay.task.annotation.DelayTask;
import com.kqinfo.universal.delay.task.helper.DelayTaskHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 用于测试的类
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Component
public class DemoJob {

    private static final Logger log = LoggerFactory.getLogger(DemoJob.class);

    @DelayTask(name = "${testTaskName}")
    public void job(String info) {
        log.info("延迟任务「job」被调用了, id:{} 当前时间：{}", info, LocalDateTime.now());
        DelayTaskHelper.handleSuccess();
    }

    @DelayTask(name = "demoJob0")
    public void job0(String info) {
        log.info("延迟任务「job0」被调用了, id:{} 当前时间：{}", info, LocalDateTime.now());
        DelayTaskHelper.handleSuccess();
    }

    @DelayTask(name = "demoJob1")
    public void job1(String info) {
        log.info("延迟任务「job1」被调用了, id:{} 当前时间：{}", info, LocalDateTime.now());
        DelayTaskHelper.handleSuccess();
    }

    private static Set<String> task = new HashSet<>(100);

    @DelayTask(name = "demoJob2")
    public void job2(String info) throws InterruptedException {
        if (!task.add(info)) {
            System.err.println("延时任务「job2」被重复调用了, id:"  + info);
        }
        log.info("延迟任务「job2」被调用了, id:{} 当前时间：{}", info, LocalDateTime.now());
        Thread.sleep(6000L);
        log.info("延迟任务「job2」执行完毕, id:{} 当前时间：{}", info, LocalDateTime.now());
        DelayTaskHelper.handleSuccess();
    }
}
