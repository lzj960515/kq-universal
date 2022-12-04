package com.kqinfo.universal.mybatis.interceptor;

import com.kqinfo.universal.mybatis.annotation.FieldEncrypt;
import com.kqinfo.universal.mybatis.util.EncryptHandler;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 加密拦截器
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class FieldEncryptInterceptor implements Interceptor {

    @Resource
    private EncryptHandler encryptHandler;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        List<List<FieldInfo>> fieldInfoList = new ArrayList<>();

        final Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        if (isUpdate(mappedStatement.getSqlCommandType())) {
            final Object entity = args[1];
            if (entity instanceof Map) {
                Iterator iterator = ((Map) entity).entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    final String key = (String) entry.getKey();
                    if (key.startsWith("param")) {
                        // 加密
                        final Object value = entry.getValue();
                        if (value != null) {
                            fieldInfoList.add(setEncryptData(value));
                        }
                    }
                }
            } else if (entity != null) {
                fieldInfoList.add(setEncryptData(entity));
            }
        }
        final Object proceed = invocation.proceed();
        // 将数据还原
        for (List<FieldInfo> fieldInfos : fieldInfoList) {
            for (FieldInfo fieldInfo : fieldInfos) {
                final Field field = fieldInfo.getField();
                ReflectionUtils.makeAccessible(field);
                ReflectionUtils.setField(field, fieldInfo.getEntity(), fieldInfo.getData());
            }
        }
        return proceed;
    }

    public boolean isUpdate(SqlCommandType commandType) {
        return SqlCommandType.UPDATE == commandType || SqlCommandType.INSERT == commandType;
    }

    private List<FieldInfo> setEncryptData(Object entity) {
        List<FieldInfo> fieldInfoList = new ArrayList<>();
        Class<?> entityClass = entity.getClass();
        ReflectionUtils.doWithFields(entityClass, field -> {
            final FieldEncrypt fieldEncrypt = field.getAnnotation(FieldEncrypt.class);
            if (fieldEncrypt != null) {
                ReflectionUtils.makeAccessible(field);
                Object data = ReflectionUtils.getField(field, entity);
                if (data != null) {
                    if (!(data instanceof String)) {
                        throw new IllegalArgumentException("字段必须为String类型");
                    }
                    String dataStr = data.toString();
                    if (StringUtils.hasText(dataStr)) {
                        // 记录原数据
                        fieldInfoList.add(new FieldInfo(entity, field, data));
                        ReflectionUtils.setField(field, entity, encryptHandler.encrypt(dataStr));
                    }
                }
            }
        });
        return fieldInfoList;
    }

}
