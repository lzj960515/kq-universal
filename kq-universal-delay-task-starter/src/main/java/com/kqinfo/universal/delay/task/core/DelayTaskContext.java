package com.kqinfo.universal.delay.task.core;

import com.kqinfo.universal.delay.task.annotation.DelayTask;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 延迟任务上下文，存放标识了DelayTask注解的方法
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Component
public class DelayTaskContext implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * 执行器map, 任务名称:执行器
     */
    private static final Map<String, DelayTaskMethod> INVOKER_REPOSITORY = new ConcurrentHashMap<>(4);

    @Resource
    private DelayTaskExecutor delayTaskExecutor;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        this.scan(event.getApplicationContext());
        delayTaskExecutor.start();
    }

    private void scan(ApplicationContext applicationContext){
        String[] beanNames = applicationContext.getBeanNamesForType(Object.class);
        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            Map<Method, DelayTask> annotatedMethods = MethodIntrospector.selectMethods(bean.getClass(),
                    (MethodIntrospector.MetadataLookup<DelayTask>) method -> AnnotatedElementUtils.findMergedAnnotation(method, DelayTask.class));
            annotatedMethods.forEach((method, delayTask) -> {
                if(delayTask == null){
                    return;
                }
                INVOKER_REPOSITORY.put(delayTask.name(), new DelayTaskMethod(bean, method));
            });
        }
    }

    public static DelayTaskMethod find(String taskName){
        return INVOKER_REPOSITORY.get(taskName);
    }
}
