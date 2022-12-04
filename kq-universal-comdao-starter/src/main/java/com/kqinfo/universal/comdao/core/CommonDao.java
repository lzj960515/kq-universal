package com.kqinfo.universal.comdao.core;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;
import java.util.Map;

/**
 *
 * @author zpj
 * @date 2016/10/17
 * 根据动态SQL的原理，组装SQL
 */
@Mapper
public interface CommonDao {

    /**
     * 插入一个对象
     *
     * @param params 实体对象
     * @return 大于0为成功
     */
    @InsertProvider(type = SqlBuilder.class, method = "insert")
    <T> int insert(T params);

    /**
     * 删除一个对象
     *
     * @param id  主键
     * @param cls 指定一个实体类型
     * @return delete line count
     */
    @DeleteProvider(type = SqlBuilder.class, method = "delete")
    <T> int delete(@Param("id") T id, Class<?> cls);

    /**
     * 修改对象，必须有主键
     *
     * @param params param
     * @param <T> type
     * @return update line count
     */
    @UpdateProvider(type = SqlBuilder.class, method = "update")
    <T> int update(T params);

    /**
     * 根据主键选择一个对象
     *
     * @param id  关键字
     * @param cls 指定一个实体
     * @return result
     */
    @SelectProvider(type = SqlBuilder.class, method = "select")
    <T> Map<?,?> select(@Param("id") T id, Class<?> cls);

    /**
     * 查询列表条数跟 selectList 配对使用
     *
     * @param params param
     * @param <T> param type
     * @return count
     */
    @SelectProvider(type = SqlBuilder.class, method = "selectCount")
    <T> long selectCount(T params);

    /**
     * 查询列表
     *
     * @param params param
     * @return result
     */
    @SelectProvider(type = SqlBuilder.class, method = "selectList")
    <T> List<Map<?,?>> selectList(T params);


    /**
     * 自定义查询列表结果
     *
     * @param params param
     * @return result
     */
    @Select(" ${selfSQL} ")
    List<Map<?,?>> selectListBySql(Map<String, Object> params);

    /**
     * 自定义查询数量结果
     *
     * @param params 参数
     * @return result
     */
    @Select(" ${selfSQL} ")
    long selectCountBySql(Map<String, Object> params);


    /**
     * insert
     * @param params 参数
     * @return result
     */
    @Insert(" ${selfSQL} ")
    long insertSql(Map<String, Object> params);

    /**
     * update
     * @param params 参数
     * @return result
     */
    @Delete(" ${selfSQL} ")
    long updateSql(Map<String, Object> params);

    /**
     * delete
     * @param params 参数
     * @return result
     */
    @Update(" ${selfSQL} ")
    long deleteSql(Map<String, Object> params);

    /**
     * 批量删除多个实体
     *
     * @param ids id列表
     * @param cls class
     * @return delete line count
     */
    @DeleteProvider(type = SqlBuilder.class, method = "deleteBatch")
    <T> int deleteBatch(T[] ids, Class<?> cls);


    /**
     * 批量插入
     * @param list list
     * @param <E> type
     * @return insert line count
     */
    @InsertProvider(type = SqlBuilder.class, method = "insertBatch")
    @Options(keyColumn = "id", useGeneratedKeys = true)
    <E> int insertBatch(@Param("list") List<E> list);


    /**
     * 替换某个主键的信息
     *
     * @param params param
     * @param <T> type
     * @return replace line count
     */
    @InsertProvider(type = SqlBuilder.class, method = "replace")
    @Options(keyColumn = "id", useGeneratedKeys = true)
    <T> int replace(T params);


    /**
     * 批量替换某个主键的信息
     * @param list list
     * @param <E> type
     * @return replace line count
     */
    @InsertProvider(type = SqlBuilder.class, method = "replaceBatch")
    @Options(keyColumn = "id", useGeneratedKeys = true)
    <E> int replaceBatch(@Param("list") List<E> list);

}
