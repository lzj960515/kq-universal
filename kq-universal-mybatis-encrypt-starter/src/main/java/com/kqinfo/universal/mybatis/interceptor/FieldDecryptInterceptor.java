package com.kqinfo.universal.mybatis.interceptor;

import com.kqinfo.universal.mybatis.annotation.FieldEncrypt;
import com.kqinfo.universal.mybatis.util.EncryptHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.sql.Statement;
import java.util.List;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Intercepts({@Signature(
        type = ResultSetHandler.class,
        method = "handleResultSets",
        args = {Statement.class}
)})
public class FieldDecryptInterceptor implements Interceptor {

    @Resource
    private EncryptHandler encryptHandler;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        List list = (List)invocation.proceed();
        for (Object entity : list) {
            final Class<?> entityClass = entity.getClass();
            ReflectionUtils.doWithFields(entityClass, field -> {
                final FieldEncrypt fieldEncrypt = field.getAnnotation(FieldEncrypt.class);
                if(fieldEncrypt != null){
                    ReflectionUtils.makeAccessible(field);
                    Object data = ReflectionUtils.getField(field, entity);
                    if(data != null) {
                        if (!(data instanceof String)) {
                            throw new IllegalArgumentException("字段必须为String类型");
                        }
                        String dataStr = data.toString();
                        if (StringUtils.hasText(dataStr)) {
                            ReflectionUtils.setField(field, entity, encryptHandler.decrypt(dataStr));
                        }
                    }
                }
            });
        }
        return list;
    }
}
