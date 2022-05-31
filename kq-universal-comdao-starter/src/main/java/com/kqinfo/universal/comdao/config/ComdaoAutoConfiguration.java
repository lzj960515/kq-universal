package com.kqinfo.universal.comdao.config;

import com.kqinfo.universal.comdao.core.CommonDaoWrap;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Zijian Liao
 * @since 2.8.0
 */
@MapperScan(basePackages = "com.kqinfo.universal.comdao.core")
@Import(CommonDaoWrap.class)
@Configuration
public class ComdaoAutoConfiguration {
}
