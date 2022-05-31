package com.kqinfo.universal.workflow.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@MapperScan(basePackages = "com.kqinfo.universal.workflow.mapper")
@ComponentScan(basePackages = "com.kqinfo.universal.workflow")
@Configuration
public class WorkflowAutoConfiguration {
}
