package com.kqinfo.universal.stream.util;

import lombok.Data;

/**
 * 消费组信息
 *
 * @author Zijian Liao
 * @since 1.4.0
 */
@Data
public class Group {

    private String groupName;

    private Long consumerCount;

    private Long pendingCount;

    private String lastDeliveredId;
}
