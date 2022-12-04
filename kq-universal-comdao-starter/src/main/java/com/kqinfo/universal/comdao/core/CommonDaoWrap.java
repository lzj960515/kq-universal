package com.kqinfo.universal.comdao.core;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.NonTransientDataAccessResourceException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.kqinfo.universal.comdao.core.SqlBuilder.UP_COMMA;


/**
 *
 * @author zpj
 * @date 2016/10/19
 */
@Slf4j
public class CommonDaoWrap {


    private final static String KEY_SELF_SQL = "selfSQL";
    @Resource
    private CommonDao dao;

    /**
     * 插入一个实体，主键必须为空,其它属性为空时，插入时忽略该属性
     *
     * @param params 实体对象
     */
    public <T> int insert(T params) {
        int insert = dao.insert(params);
        if(insert > 0){
            ReflectionUtils.doWithFields(params.getClass(), field -> {
                field.setAccessible(true);
                try {
                    if (field.get(params) == null) {
                        if (field.getType() == Long.class) {
                            field.set(params, selectCountBySQL("select LAST_INSERT_ID()", null));
                        }
                        if (field.getType() == Integer.class) {
                            field.set(params, (int) selectCountBySQL("select LAST_INSERT_ID()", null));
                        }
                    }
                } catch (IllegalAccessException e) {
                    log.error(e.getMessage(), e);
                }
            }, field -> field.isAnnotationPresent(TableId.class));
        }
        return insert;
    }


    /**
     * 插入一组数据
     * 注意这是全字段的插入，如果对应属性为空，在数据库中也会置空。
     *
     * @param list 需要保存进数据库的实体对象列表
     * @return 实际保存的数量
     */
    public <E> int insertBatch(List<E> list) {

        if (CollectionUtils.isEmpty(list)) {
            throw new NonTransientDataAccessResourceException("The list cannot be empty !!!");
        }
        return dao.insertBatch(list);
    }


    /**
     * 删除一个实体
     *
     * @param id  主键
     * @param cls 实体类型
     */
    public <T> int delete(@Param("id") T id, Class<?> cls) {
        return dao.delete(id, cls);
    }

    /**
     * 传入一组实体主键，然后一次性删除
     *
     * @param ids 主键数组，可以用steam操作完成比如map后得到数组，当然还是new T[]{x1,x2,x3}来得快
     * @param cls 实体类型
     * @return 实际删除数
     */
    public <T> int deleteBatch(T[] ids, Class<?> cls) {
        return dao.deleteBatch(ids, cls);
    }

    /**
     * 修改一个实体，必须有主键，为空的属性将不做更改
     *
     * @param params 实体对象
     * @return 实际更新数
     */
    public <T> int update(T params) {
        return dao.update(params);
    }


    /**
     * 修改一个实体，必须有主键，实体需要置空的属性可以通过参数nullFieldNams 传入。
     * 比如传入 {"name", "age"}，那么实体对象中的name和age属性就可以强制为null
     *
     * @param params        对象
     * @param nullFieldNames 空属性名
     * @return 实际更新数
     */
    public <T> long update(T params, String[] nullFieldNames) {

        // 检查是否有错误的字段名
        SqlBuilder sbr = new SqlBuilder();
        StringBuilder sql = new StringBuilder(sbr.update(params));
        int idx = sql.indexOf("SET");
        if (nullFieldNames != null) {
            final StringBuilder nullSql = new StringBuilder();
            Stream.of(nullFieldNames).forEach(n -> nullSql.append(UP_COMMA).append(n).append(UP_COMMA).append(" = null, "));
            sql.insert(idx + 4, nullSql);
        }
        return updateSql(sql.toString(), BeanUtil.beanToMap(params));
    }


    /**
     * 根据实体的主键，找到实体
     *
     * @param id  主键
     * @param cls 实体类型
     * @return 满足主键的实体对象
     */
    public <T, R> R select(@Param("id") T id, Class<R> cls) {
        Map<?,?> rs = dao.select(id, cls);
        return BeanUtil.mapToBean(rs, cls, false, CopyOptions.create());
    }

    /**
     * 根据实体条件进行查询,
     *
     * @param params 实体参数，只能等于
     * @return 不会返回空，请放心使用
     */
    public <T> List<T> selectList(T params) {
        List<Map<?,?>> list = dao.selectList(params);
        Class<T> clz = (Class<T>) params.getClass();
        return compositeList(list, clz);
    }

    /**
     * 根据实体条件以及忽略字段的查询
     *
     * @param params     实体参数
     * @param fieldNames 字段名列表,大小写敏感，如果拼写不对，就忽略此字段的作用
     * @param select     决定列表里字段是否是查询字段，true含有，false不含有。
     * @param addSql     在自动生成的SQL上追加SQL，一般填空就行了
     * @return 不会返回空，请放心使用
     */
    public <T> List<T> selectList(T params, String[] fieldNames, boolean select, String addSql) {
        try {
            Class<T> clz = (Class<T>) params.getClass();
            Map<String, Object> map = BeanUtil.beanToMap(params);
            String sql = new SqlBuilder().selectListByFieldFilter(params, fieldNames, select);
            if (addSql != null) {
                sql = sql + " " + addSql;
            }

            map.put(KEY_SELF_SQL, sql);
            List<Map<?,?>> list = dao.selectListBySql(map);
            return compositeList(list, clz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 完全自定义的查询列表
     *
     * @param sql    SQL语句，不可为空
     * @param params 键值查询参数
     * @param c      将查询的结果组装成该java类型，不可为空
     * @return 不会返回空，请放心使用
     */
    public <T> List<T> selectListBySql(String sql, Map<String, Object> params, Class<T> c) {
        List<Map<?,?>> list = dao.selectListBySql(setSQL(sql, params));
        return compositeList(list, c);
    }

    /**
     * 完全自定义的查询数目
     *
     * @param sql    不可为空
     * @param params 键值查询参数
     * @return 不会返回空，请放心使用
     */
    public long selectCountBySQL(String sql, Map<String, Object> params) {

        return dao.selectCountBySql(setSQL(sql, params));
    }

    /**
     * 追加自定义SQL
     *
     * @param sql    SQL语句，不能为null
     * @param params 键值查询参数
     * @return 键值查询MAP
     */
    private Map<String, Object> setSQL(String sql, Map<String, Object> params) {

        if (params == null) {
            params = new HashMap<>(2);
        }
        params.put(KEY_SELF_SQL, sql);
        return params;
    }

    /**
     * 利用泛型转换成相关对象
     *
     * @param list 查询出来的记录列表
     * @param c    需要转换的java类型
     * @return 必然返回一个list
     */
    private <T> List<T> compositeList(List<Map<?, ?>> list, Class<T> c) {
        if (list != null) {
            List<T> resultList = new ArrayList<>();
            list.forEach(m -> {
                T t = BeanUtil.mapToBean(m, c, false, CopyOptions.create());
                resultList.add(t);
            });
            return resultList;
        } else {
            return new ArrayList<>();
        }
    }


    /**
     * 根据实体条件查询条数
     *
     * @param params 实体参数
     * @return 满足条件数
     */
    public <T> long selectCount(T params) {
        return dao.selectCount(params);
    }


    /**
     * 通过实体唯一的字段属性或者一组字段属性，得到一个对象
     *
     * @param obj 实体对象，UNIQUE 相关属性必填
     * @return 没有返回空，如果有多个值会报异常，建议使用唯一索引
     */
    public <T> T selectByUnique(T obj) throws DataAccessException {
        List<Map<?,?>> list = dao.selectList(obj);
        if (list == null || list.size() < 1) {
            return null;
        } else if (list.size() == 1) {
            return BeanUtil.mapToBean(list.get(0), (Class<T>) obj.getClass(), false, CopyOptions.create());
        } else {
            throw new DataIntegrityViolationException("PARAMETER NOT UNIQUE, RESULT NOT ONLY ONE !");
        }
    }


    /**
     * 执行自定义SQL，纯手工打造，仅对增删改操作，返回受语句影响的行数
     *
     * @param sql    SQL语句
     * @param params 参数 跟写xml一样，比如#{age}参数，对应map里的key就是age
     * @return 实际影响数量
     */
    public long updateSql(String sql, Map<String, Object> params) {
        return dao.updateSql(setSQL(sql, params));
    }

    public long deleteSql(String sql, Map<String, Object> params) {
        return dao.deleteSql(setSQL(sql, params));
    }

    /**
     * 插入一个对象，前提是必须有主键，
     * 如果主键值已存在数据库中，则删除原有记录，再插入一条行的记录，慎用！
     *
     * @param params 实体
     * @return 实际影响数量
     */
    public <T> int replace(T params) {
        return dao.replace(params);
    }


    /**
     * 更新或者插入一批对象，这些对象必须都有主键
     * 如果主键值已存在数据库中，则删除原有记录，再插入一条行的记录，慎用！
     * 注意这是全字段的替换，如果对应属性为空，在数据库中也会置空。
     *
     * @param list 实体列表
     * @return 实际影响数量
     */
    public <E> int replaceBatch(List<E> list) {
        if (CollectionUtils.isEmpty(list)) {
            throw new NonTransientDataAccessResourceException("The list cannot be empty !!!");
        }
        return dao.replaceBatch(list);
    }

    /**
     * 半自动分页组件，返回一个标准pageInfo
     *
     * @param select    查询的select子句,
     * @param fromWhere 查询用的from 和 where 子句 这里面请不要再写limit或者orderBy了
     * @param params param
     * @param c         要包装成的类型
     * @param pageNum   当前页号, 起始页码为1
     * @param pageSize  分页大小， 如果pageSize小于1时，不启用分页，改为查询全部内容
     * @param orderBy   排序规则，如果没有排序，请为空
     * @param <T> type
     * @return result
     */
    public <T> PageResult<T> selectPageList(String select, String fromWhere, Map<String, Object> params, Class<T> c, int pageNum, int pageSize, String orderBy) {
        long count = selectCountBySQL(String.format("select count(*) %s", fromWhere), params);
        StringBuilder sbf = new StringBuilder(String.format("%s %s", select, fromWhere));

        if (orderBy != null) {
            sbf.append(String.format(" %s ", orderBy));
        }

        if (pageNum < 1) {
            pageNum = 1;
        }

        if (pageSize >= 1) {
            sbf.append(String.format(" limit %d, %d ", (pageNum - 1) * pageSize, pageSize));
        }

        List<T> list = selectListBySql(sbf.toString(), params, c);
        return new PageResult<>(list, count, pageNum, pageSize);

    }


    /**
     * 只取列表里第一个对象，如果查不到则返回空
     *
     * @param sql    查询语句
     * @param params 键值参数
     * @param c      转换类型
     * @return 返回类型T的对象
     */
    public <T> T selectOne(String sql, Map<String, Object> params, Class<T> c) {
        List<Map<?,?>> list = dao.selectListBySql(setSQL(sql, params));
        if (CollectionUtils.isEmpty(list)) {
            return null;
        } else {
            return BeanUtil.mapToBean(list.get(0), c, false, CopyOptions.create());
        }
    }

}
