package com.kqinfo.universal.comdao.core;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.annotation.Transient;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zpj
 */
@Slf4j
public class SqlBuilder {

    static final String DOWN_COMMA = ", ";

    static final String UP_COMMA = "`";

    static final String AND = "AND ";

    static final String SQL_ITEM = "sqlItem.";

    static final String ID = "id";

    /**
     * 插入的通用SQL
     *
     * @param params domain
     * @param <T>    domain type
     * @return sql
     */
    public <T> String insert(T params) {
        Class<?> cls = params.getClass();
        StringBuilder col = new StringBuilder();
        StringBuilder val = new StringBuilder();
        ReflectionUtils.doWithFields(cls, field -> {
            field.setAccessible(true);
            try {
                if (field.get(params) != null) {
                    col.append(UP_COMMA).append(tableFiled(field)).append(UP_COMMA).append(DOWN_COMMA);
                    val.append("#{").append(field.getName()).append("} ").append(DOWN_COMMA);
                } else {
                    if (field.isAnnotationPresent(TableId.class)) {
                        col.append(UP_COMMA).append(tableFiled(field)).append(UP_COMMA).append(DOWN_COMMA);
                        val.append("LAST_INSERT_ID(" + UP_COMMA).append(tableFiled(field)).append(UP_COMMA).append(") ").append(DOWN_COMMA);
                    }
                }

            } catch (IllegalAccessException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(cls.getName() + " Class not allow accesss " + field.getName() + " Field ");
            }
        }, field -> !field.isAnnotationPresent(Transient.class));
        TableName tableName = cls.getAnnotation(TableName.class);
        StringBuilder sql = new StringBuilder(" INSERT INTO ").append(tableName.value());
        sql.append("(").append(col, 0, col.lastIndexOf(DOWN_COMMA)).append(") ");
        sql.append(" VALUES ");
        sql.append("(").append(val, 0, val.lastIndexOf(DOWN_COMMA)).append(") ");
        return sql.toString();
    }

    /**
     * 批量插入
     *
     * @param list list
     * @param <E>  domain type
     * @return sql
     */
    public <E> String insertBatch(@Param("list") List<E> list) {
        // 取第一个值，得到对应的field
        Object e = list.get(0);
        Class<?> cls = e.getClass();
        TableName t = cls.getAnnotation(TableName.class);


        StringBuilder val = new StringBuilder();
        StringBuilder col = new StringBuilder();
        ReflectionUtils.doWithFields(cls, field -> {
            ReflectionUtils.makeAccessible(field);
            col.append(UP_COMMA).append(tableFiled(field)).append(UP_COMMA).append(DOWN_COMMA);
            val.append("#{" + SQL_ITEM).append(field.getName()).append("} ").append(DOWN_COMMA);
        }, field -> !field.isAnnotationPresent(Transient.class));


        StringBuilder sql = new StringBuilder(" INSERT INTO ").append(t.value());
        sql.append("(").append(col.substring(0, col.lastIndexOf(DOWN_COMMA))).append(") ");
        sql.append(" VALUES \n");
        StringBuilder tmp = new StringBuilder();
        tmp.append("(").append(val.substring(0, val.lastIndexOf(DOWN_COMMA))).append(") ");

        for (int i = 0; i < list.size(); i++) {
            sql.append(tmp.toString().replace(SQL_ITEM, "list[" + i + "]."));
            if (i < list.size() - 1) {
                sql.append(",\n");
            }

        }
        return sql.toString();

    }


    /**
     * 删除的通用SQL
     *
     * @param id  id
     * @param cls domain type
     * @return sql
     */
    public <T> String delete(@Param("id") T id, Class<?> cls) {
        TableName t = cls.getAnnotation(TableName.class);
        return " DELETE FROM " + t.value() + whereKey(cls, true);

    }

    /**
     * 更新的通用SQL
     *
     * @param params domain
     * @param <T>    domain type
     * @return sql
     */
    public <T> String update(T params) {
        Class<?> cls = params.getClass();
        TableName t = cls.getAnnotation(TableName.class);
        String colVal = assembleVariableSql(params, DOWN_COMMA, true);

        return " UPDATE " + t.value() +
                " SET " + colVal.substring(0, colVal.lastIndexOf(DOWN_COMMA)) +
                whereKey(cls, false);
    }

    /**
     * @param cls        类
     * @param isParamsId 是否是显式的参数id值，
     * @return where sql
     */
    private String whereKey(Class<?> cls, boolean isParamsId) {
        Field field = this.getIdFiled(cls);
        if (field == null) {
            return " WHERE `id` = #{id} ";
        } else {
            if (isParamsId) {
                return " WHERE " + UP_COMMA + tableFiled(field) + UP_COMMA + " = #{id} ";
            } else {
                return " WHERE " + UP_COMMA + tableFiled(field) + UP_COMMA + " = #{" + field.getName() + "} ";
            }
        }
    }

    public Field getIdFiled(Class<?> cls){
        AtomicReference<Field> idField = new AtomicReference<>();
        ReflectionUtils.doWithFields(cls, idField::set, field -> field.isAnnotationPresent(TableId.class));
        return idField.get();
    }


    /**
     * 根据id查单个对象
     *
     * @param id id
     * @param cls domain class
     * @param <T> domain type
     * @return sql
     */
    public <T> String select(@Param("id") T id, Class<?> cls) {
        TableName t = cls.getAnnotation(TableName.class);
        return " SELECT * FROM " + t.value() + whereKey(cls, true);
    }


    public <T> String selectList(T params) {
        Class<?> cls = params.getClass();

        String colVal = assembleVariableSql(params, AND, false);

        TableName t = cls.getAnnotation(TableName.class);
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT * FROM ").append(t.value());

        if (colVal.length() > 0) {
            sql.append(" WHERE ");
            sql.append(colVal, 0, colVal.lastIndexOf(AND));
        }
        return sql.toString();
    }


    public <T> String selectListByFieldFilter(T params, String[] fieldNames, boolean select) {

        String str = selectList(params);
        if (fieldNames != null && fieldNames.length > 0) {
            Map<String, String> fieldMap = Stream.of(fieldNames).collect(Collectors.toMap(name -> name, name -> name));
            //在字段列表里的字段和没在字段列表里的字段，核心工作是验证代入字段的有效性
            List<String> inList = new ArrayList<>();
            List<String> outList = new ArrayList<>();

            ReflectionUtils.doWithFields(params.getClass(), field -> {
                // 找出filedNames中的field
                String fieldName = tableFiled(field);
                if (fieldMap.containsKey(fieldName)) {
                    inList.add(fieldName);
                }else {
                    outList.add(fieldName);
                }
            }, field -> !field.isAnnotationPresent(Transient.class));

            StringBuffer queryFieldNamesBuffer = new StringBuffer();
            if (select) {
                if (inList.size() > 0) {
                    generateFieldString(queryFieldNamesBuffer, inList);
                    str = str.replace("*", queryFieldNamesBuffer.toString());
                }
            } else {
                if (outList.size() > 0) {
                    generateFieldString(queryFieldNamesBuffer, outList);
                    str = str.replace("*", queryFieldNamesBuffer.toString());
                }
            }

        }
        return str;
    }


    private void generateFieldString(StringBuffer sbf, List<String> fields) {
        for (int i = 0; i < fields.size(); i++) {
            if (i != fields.size() - 1) {
                sbf.append(UP_COMMA).append(fields.get(i)).append(UP_COMMA).append(DOWN_COMMA);
            } else {
                sbf.append(UP_COMMA).append(fields.get(i)).append(UP_COMMA);
            }
        }
    }


    /**
     * 组合更新的字段和查询所需要的字段
     *
     * @param params   参数对象
     * @param symbol   分隔符号
     * @param isUpdate 是否为插入语句
     * @return sql
     */
    private <T> String assembleVariableSql(T params, String symbol, boolean isUpdate) {
        Class<?> cls = params.getClass();
        StringBuilder colVal = new StringBuilder();
        ReflectionUtils.doWithFields(cls, field -> {
            field.setAccessible(true);
            try {
                if (field.get(params) != null) {
                    if (!isUpdate && String.class == field.getType()) {
                        colVal.append(UP_COMMA).append(tableFiled(field)).append(UP_COMMA).append(" like #{").append(field.getName()).append("} ").append(symbol);
                    } else {
                        if (!(ID.equals(field.getName()) || field.getAnnotation(TableId.class) != null)) {
                            colVal.append(UP_COMMA).append(tableFiled(field)).append(UP_COMMA).append(" = #{").append(field.getName()).append("} ").append(symbol);
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(cls.getName() + " Class not allow accesss " + field.getName() + " Field ");
            }
        }, f -> !f.isAnnotationPresent(Transient.class));
        return colVal.toString();
    }


    /**
     * 产生批处理SQL
     *
     * @param ids id列表
     * @param cls domain class
     * @param <T> domain type
     * @return sql
     */
    public <T> String deleteBatch(T[] ids, Class<?> cls) {

        TableName t = cls.getAnnotation(TableName.class);

        String where = whereKey(cls, true);
        where = where.substring(0, where.indexOf("="));

        StringBuilder sql = new StringBuilder();
        sql.append(" DELETE FROM ").append(t.value()).append(where).append(" in ");
        sql.append(" ( ");

        if (ids != null && ids.length > 0) {
            StringBuilder colVal = new StringBuilder();
            for (T id : ids) {
                if (Integer.class == id.getClass() || Long.class == id.getClass()) {
                    colVal.append(id).append(DOWN_COMMA);
                } else if (String.class == id.getClass()) {
                    colVal.append("'").append(id).append("'").append(DOWN_COMMA);
                }
            }
            sql.append(colVal);
            sql.deleteCharAt(sql.lastIndexOf(DOWN_COMMA));
        } else {
            throw new RuntimeException("ids不能为空，且长度不能为0");
        }

        sql.append(" ) ");
        return sql.toString();
    }

    static final String ORDER_BY = "ORDER BY";
    static final String LIMIT = "LIMIT";


    /**
     * 产生查询数量的SQL
     *
     * @param params domain
     * @param <T> domain type
     * @return sql
     */
    public <T> String selectCount(T params) {
        String str = selectList(params).replace("SELECT *", "SELECT COUNT(*)");
        if (str.contains(ORDER_BY)) {
            str = str.substring(0, str.indexOf(ORDER_BY));
        }
        if (str.contains(LIMIT)) {
            str = str.substring(0, str.indexOf(LIMIT));
        }
        return str;
    }


    public <T> String replace(T params) {
        return insert(params).replace("INSERT ", "REPLACE ");
    }

    public <E> String replaceBatch(@Param("list") List<E> list) {
        return insertBatch(list).replace("INSERT ", "REPLACE ");
    }

    /**
     * 确定字段的数据库名称
     * @param field 字段
     * @return 字段的数据库名称
     */
    private String tableFiled(Field field){
        if (field.isAnnotationPresent(TableField.class)) {
            return field.getAnnotation(TableField.class).value();
        }
        if (field.isAnnotationPresent(TableId.class)){
            String value = field.getAnnotation(TableId.class).value();
            if(StringUtils.isNotBlank(value)){
                return value;
            }
        }
        if(StringUtils.isCamel(field.getName())){
            return StringUtils.camelToUnderline(field.getName());
        }
        return field.getName();
    }
}


//------------------------------------ 内部的SQLBuilder类 end --------------------------------
