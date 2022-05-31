package com.kqinfo.universal.stream.config;

import com.kqinfo.universal.stream.core.RedisStreamListenerContext;
import com.kqinfo.universal.stream.util.RedisMessageHandler;
import org.springframework.context.annotation.Import;

/**
 * 消息配置类
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Import({RedisStreamListenerContext.class, RedisMessageHandler.class})
public class StreamAutoConfiguration {

}
