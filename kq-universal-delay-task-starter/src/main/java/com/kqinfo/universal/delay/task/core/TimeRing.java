package com.kqinfo.universal.delay.task.core;

import com.kqinfo.universal.delay.task.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 时间轮，用于存储任务id
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
public class TimeRing {

    private static final Logger log = LoggerFactory.getLogger(TimeRing.class);

    /**
     * 时间轮 秒:任务id列表
     */
    private static final Map<Integer, Set<Long>> RING = new ConcurrentHashMap<>(64, 1);


    public static void put(long time, Long taskId){
        int second = TimeUtil.getSecond(time);
        Set<Long> taskIds = RING.computeIfAbsent(second, k -> new HashSet<>());
        taskIds.add(taskId);
    }

    public static List<Long> pull(){
        long time = System.currentTimeMillis();
        int second = TimeUtil.getSecond(time);
        Set<Long> taskIds = remove(second);
        List<Long> moreTaskIds = new ArrayList<>(taskIds.size());
        moreTaskIds.addAll(taskIds);
        // 为防止任务执行时间过长，跳过了前面几秒任务，将前面2秒的任务也取出
        // 当然，这种情况一般不会发生，任务一但取出就放入了线程池中。
        // 一般来说前面2秒都不会有任务，因为已经被取走了。只有卡住的时候才会发生这种情况
        for(int i = 1; i < 2; i++){
            // 当此时为1秒时，前面的2秒为0和59 如果只是减，将得到一个负数
            int beforeSecond = (second + 60 - i) % 60;
            moreTaskIds.addAll(remove(beforeSecond));
        }
        return moreTaskIds;
    }

    private static Set<Long> remove(int second){
        Set<Long> taskIds = RING.remove(second);
        if(taskIds != null){
            return taskIds;
        }
        return Collections.emptySet();
    }
}
