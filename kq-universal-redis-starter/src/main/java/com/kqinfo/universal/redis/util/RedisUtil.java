package com.kqinfo.universal.redis.util;

import com.kqinfo.universal.redis.properties.KqRedisProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * redis util
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Slf4j
public class RedisUtil {

    private static RedisTemplate<String, Object> redisTemplate;
    private static KqRedisProperties redisProperties;

    public RedisUtil(final RedisTemplate<String, Object> redisTemplate,
                     final KqRedisProperties redisProperties) {
        RedisUtil.redisTemplate = redisTemplate;
        RedisUtil.redisProperties = redisProperties;
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return Boolean
     */
    public static Boolean expire(String key, Long time) {
        return expire(key, time, TimeUnit.SECONDS);
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @param unit 时间单位
     * @return Boolean
     */
    public static Boolean expire(String key, Long time, TimeUnit unit) {
        try {
            if (time > 0) {
                redisTemplate.expire(wrapKey(key), time, unit);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 根据key获取过期时间
     *
     * @param key 键 不能为 null
     * @return 时间(秒) 返回 0代表为永久有效
     */
    public static Long getExpire(String key) {
        return redisTemplate.getExpire(wrapKey(key), TimeUnit.SECONDS);
    }

    /**
     * 判断 key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public static Boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(wrapKey(key));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    public static void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(wrapKey(key[0]));
            } else {
                redisTemplate
                        .delete(Arrays.stream(key).peek(i -> wrapKey(i)).collect(Collectors.toList()));
            }
        }
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String key) {
        return (T) redisTemplate.opsForValue().get(wrapKey(key));
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public static Boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(wrapKey(key), value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public static Boolean set(String key, Object value, Long time) {
        return set(key, value, time, TimeUnit.SECONDS);
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @param unit  时间单位
     * @return true成功 false 失败
     */
    public static Boolean set(String key, Object value, Long time, TimeUnit unit) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(wrapKey(key), value, time, unit);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * Set {@code key} to hold the string {@code value} and expiration {@code timeout} if {@code key} is absent.
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0
     * @param unit 时间单位
     * @return true成功 false 失败
     */
    public static Boolean setNx(String key, Object value, Long time, TimeUnit unit){
        try {
            return redisTemplate.opsForValue().setIfAbsent(wrapKey(key), value, time, unit);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return Long
     */
    public static Long incr(String key, Long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(wrapKey(key), delta);
    }

    /**
     * 递增并设置过期时间（原子操作）
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @param time  ttl(时间秒)
     * @return Long
     */
    public static Long incrAndExpire(String key, long delta, long time) {
        return incrAndExpire(key, delta, time, TimeUnit.SECONDS);
    }

    /**
     * 递增并设置过期时间（原子操作）
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @param time  ttl
     * @param unit  时间单位
     * @return Long
     */
    public static Long incrAndExpire(String key, long delta, long time, TimeUnit unit) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        String script = "local incrValue = redis.call('incrby', KEYS[1], ARGV[1]); " +
                "redis.call('expire', KEYS[1], ARGV[2]);" +
                "return incrValue ";
        return  (Long) redisTemplate.execute(RedisScript.of(script, Long.class), Collections.singletonList(wrapKey(key)),  delta, unit.toSeconds(time));
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几
     * @return Long
     */
    public static Long decr(String key, Long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(wrapKey(key), -delta);
    }

    /**
     * HashGet
     *
     * @param key  键 不能为 null
     * @param item 项 不能为 null
     * @return 值
     */
    public static Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(wrapKey(key), item);
    }

    /**
     * 获取 hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public static Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(wrapKey(key));
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public static Boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(wrapKey(key), map);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public static Boolean hmset(String key, Map<String, Object> map, Long time) {
        try {
            redisTemplate.opsForHash().putAll(wrapKey(key), map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public static Boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(wrapKey(key), item, value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public static Boolean hset(String key, String item, Object value, Long time) {
        try {
            redisTemplate.opsForHash().put(wrapKey(key), item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为 null
     * @param item 项 可以使多个不能为 null
     */
    public static void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(wrapKey(key), item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为 null
     * @param item 项 不能为 null
     * @return true 存在 false不存在
     */
    public static Boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(wrapKey(key), item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return Double
     */
    public static Double hincr(String key, String item, Double by) {
        return redisTemplate.opsForHash().increment(wrapKey(key), item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return Double
     */
    public static Double hdecr(String key, String item, Double by) {
        return redisTemplate.opsForHash().increment(wrapKey(key), item, -by);
    }

    /**
     * 根据 key获取 Set中的所有值
     *
     * @param key 键
     * @return Set
     */
    public static Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(wrapKey(key));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public static Boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(wrapKey(key), value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public static Long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(wrapKey(key), values);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public static Long sSetAndTime(String key, Long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(wrapKey(key), values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return Long
     */
    public static Long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(wrapKey(key));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public static Long setRemove(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().remove(wrapKey(key), values);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return List
     */
    public static List<Object> lGet(String key, Long start, Long end) {
        try {
            return redisTemplate.opsForList().range(wrapKey(key), start, end);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return Long
     */
    public static Long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(wrapKey(key));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推； index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return Object
     */
    public static Object lGetIndex(String key, Long index) {
        try {
            return redisTemplate.opsForList().index(wrapKey(key), index);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return Boolean
     */
    public static Boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(wrapKey(key), value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return Boolean
     */
    public static Boolean lSet(String key, Object value, Long time) {
        try {
            redisTemplate.opsForList().rightPush(wrapKey(key), value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return Boolean
     */
    public static Boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(wrapKey(key), value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return Boolean
     */
    public static Boolean lSet(String key, List<Object> value, Long time) {
        try {
            redisTemplate.opsForList().rightPushAll(wrapKey(key), value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return Boolean
     */
    public static Boolean lUpdateIndex(String key, Long index, Object value) {
        try {
            redisTemplate.opsForList().set(wrapKey(key), index, value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public static Long lRemove(String key, Long count, Object value) {
        try {
            return redisTemplate.opsForList().remove(wrapKey(key), count, value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0L;
        }
    }

    private static String wrapKey(String key) {
        if(StringUtils.hasText(redisProperties.getPrefix())){
            return redisProperties.getPrefix().concat(key);
        }
        return key;
    }
}
