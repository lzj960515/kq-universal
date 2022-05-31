package com.kqinfo.universal.mybatisplus.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.kqinfo.universal.mybatisplus.repository.MyMetaObjectHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Configuration
public class MybatisPlusConfiguration {

	/**
	 * MyBatis Plus 分页插件
	 * @return {@link MybatisPlusInterceptor}
	 */
	@Bean
	public MybatisPlusInterceptor mybatisPlusInterceptor() {
		MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
		interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
		return interceptor;
	}

	@Bean
	public MyMetaObjectHandler metaObjectHandler(){
		return new MyMetaObjectHandler();
	}

}
