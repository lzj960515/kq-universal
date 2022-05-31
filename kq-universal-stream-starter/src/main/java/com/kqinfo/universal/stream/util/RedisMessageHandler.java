package com.kqinfo.universal.stream.util;

import com.kqinfo.universal.stream.core.MessageRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.PendingMessages;
import org.springframework.data.redis.connection.stream.Record;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamInfo;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * redis 实现
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class RedisMessageHandler implements MessageHandler {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public String convertAndSend(String key, Object message) {
        RecordId recordId = stringRedisTemplate.opsForStream().add(Record.of(message).withStreamKey(key));
        stringRedisTemplate.opsForStream().trim(key, 10000);
        return Objects.requireNonNull(recordId).getValue();
    }

    private static final String CREATE_GROUP_OK = "OK";

    @Override
    public boolean subscribe(String key, String group, String consumer) {
        String status = stringRedisTemplate.opsForStream().createGroup(key, group);
        return CREATE_GROUP_OK.equals(status);
    }

    @Override
    public <T> List<MessageRecord<T>> pending(String key, String group, String consumer, Class<T> targetType) {
        List<MessageRecord<T>> messageRecordList = new ArrayList<>(2);
        PendingMessages pending = stringRedisTemplate.opsForStream().pending(key, Consumer.from(group, consumer), Range.closed("0", "+"), 10000);
        pending.forEach(message -> {
            String id = message.getIdAsString();
            List<ObjectRecord<String, T>> range = stringRedisTemplate.opsForStream().range(targetType, key, Range.closed(id, id));
            range.forEach(record -> {
                messageRecordList.add(new MessageRecord<>(record.getId().getValue(), record.getValue()));
            });
        });
        return messageRecordList;
    }

    @Override
    public List<Group> groups(String key) {
        StreamInfo.XInfoGroups groups = stringRedisTemplate.opsForStream().groups(key);
        return groups.stream().map(this::convert).collect(Collectors.toList());
    }

    private Group convert(StreamInfo.XInfoGroup xInfoGroup){
        Group group = new Group();
        group.setGroupName(xInfoGroup.groupName());
        group.setConsumerCount(xInfoGroup.consumerCount());
        group.setPendingCount(xInfoGroup.pendingCount());
        group.setLastDeliveredId(xInfoGroup.lastDeliveredId());
        return group;
    }

    @Override
    public boolean acknowledge(String key, String group, String messageId) {
        Long acknowledge = stringRedisTemplate.opsForStream().acknowledge(key, group, messageId);
        return acknowledge != null && acknowledge > 1;
    }
}
