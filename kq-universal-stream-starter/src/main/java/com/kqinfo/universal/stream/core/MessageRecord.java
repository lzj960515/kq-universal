package com.kqinfo.universal.stream.core;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Zijian Liao
 * @since 1.4.0
 */
@Data
@AllArgsConstructor
public class MessageRecord<T> {

    private String recordId;

    private T message;

}
