package com.kqinfo.universal.stream.util;

import com.kqinfo.universal.stream.core.MessageRecord;

import java.util.List;

/**
 * @author Zijian Liao
 * @since 1.4.0
 */
public interface MessageHandler {

    String convertAndSend(String key, Object message);

    boolean subscribe(String key, String group, String consumer);

    <T> List<MessageRecord<T>> pending(String key, String group, String consumer, Class<T> targetType);

    List<Group> groups(String key);

    boolean acknowledge(String key, String group, String messageId);
}
