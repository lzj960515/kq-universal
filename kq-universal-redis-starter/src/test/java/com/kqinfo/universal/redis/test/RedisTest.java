package com.kqinfo.universal.redis.test;
import java.time.LocalDateTime;

import com.kqinfo.universal.redis.util.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * redis util test
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedisApplication.class)
public class RedisTest {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Test
    public void redisTest(){
        RedisUtil.set("a", "xx");
    }

    @Test
    public void localDateTimeTest(){
        Person person = new Person();
        person.setBirthday(LocalDateTime.now());
        RedisUtil.set("a", person, 1L, TimeUnit.MINUTES);

        Person p = RedisUtil.get("a");
        System.out.println(p);
    }

    @Test
    public void incrAndExpireTest(){
        System.out.println(RedisUtil.incrAndExpire("incr", 10L, 60L));
        System.out.println(RedisUtil.incrAndExpire("incr", 10L, 1, TimeUnit.MINUTES));

    }

    @Test
    public void readTest(){
        String key = "user";
        String group = "orderGroup";
        String consumerStr = "orderConsumer";
        Consumer consumer = Consumer.from(group, consumerStr);
        StreamReadOptions streamReadOptions = StreamReadOptions.empty()
                .block(Duration.ZERO)
                .count(100);
        List<MapRecord<String, Object, Object>> recordList = redisTemplate.opsForStream().read(consumer, streamReadOptions, StreamOffset.create(key, ReadOffset.lastConsumed()));
        System.out.println(recordList);
        if(recordList != null){
            recordList.forEach(record -> {
                Map<Object, Object> map = record.getValue();
                map.forEach((key1,value) -> {
                    System.out.println("key:" + key1 + ", value: " + value);
                });
                redisTemplate.opsForStream().acknowledge(key, group, record.getId());
            });
        }
    }

    @Test
    public void createGroup(){
        String key = "user";
        String group = "orderGroup";
        String orderGroup = redisTemplate.opsForStream().createGroup(key, ReadOffset.latest(), group);
    }

    @Test
    public void destroyGroup(){
        String key = "user";
        String group = "orderGroup";
        Boolean aBoolean = redisTemplate.opsForStream().destroyGroup(key, group);
    }

    @Test
    public void publishTest(){
        String key = "user";
        Map<Object, Object> map = new HashMap<>();
        map.put("name", "王五");
        map.put("age", 12);
        RecordId recordId = redisTemplate.opsForStream().add(MapRecord.create(key, map));
        System.out.println(recordId);
    }
}
