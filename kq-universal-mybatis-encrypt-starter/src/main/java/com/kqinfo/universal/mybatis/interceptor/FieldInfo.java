package com.kqinfo.universal.mybatis.interceptor;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * 字段信息
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
public class FieldInfo implements Serializable {

    private Object entity;

    private Field field;

    private Object data;

    public Object getEntity() {
        return entity;
    }

    public void setEntity(Object entity) {
        this.entity = entity;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public FieldInfo(Object entity, Field field, Object data) {
        this.entity = entity;
        this.field = field;
        this.data = data;
    }
}
