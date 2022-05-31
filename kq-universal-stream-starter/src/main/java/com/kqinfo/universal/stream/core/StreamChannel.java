package com.kqinfo.universal.stream.core;

import com.kqinfo.universal.stream.util.MessageHandler;
import lombok.RequiredArgsConstructor;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class StreamChannel<T> {

    private final String group;

    private final T message;

    private final MessageHandler messageHandler;

    private final String queue;

    private final String recordId;

    public String getQueue(){
        return queue;
    }

    public T getMessage(){
       return message;
    }

    public String getRecordId(){
        return recordId;
    }


    public void acknowledge(){
        messageHandler.acknowledge(queue, group, recordId);
    }

}
