# Redis组件
| 功能                                        | 完成 |
|-------------------------------------------| ---- |
| 简便的RedisUtil                              | ✔    |
| 注解实现的Redis锁-尝试获取锁，不成功抛出异常                 | ✔ |
| 注解实现的Redis锁-尝试获取锁，不成功直接返回null             | ✔ |
| 注解实现的Redis锁-尝试获取锁，不成功阻塞等待                 | ✔ |
| 注解实现的Redis锁-尝试获取锁，不成功阻塞等待一段时间，依旧未获取到锁则抛出异常 | ✔ |
| 注解实现的Redis读写锁-尝试获取锁，不成功抛出异常               | ✔ |
| 注解实现的Redis读写锁-尝试获取锁，不成功直接返回null               | ✔ |
| 注解实现的Redis读写锁-尝试获取锁，不成功阻塞等待               | ✔ |
| 注解实现的Redis读写锁-尝试获取锁，不成功阻塞等待一段时间，依旧未获取到锁则抛出异常 | ✔ |

## 使用方式

### 配置

1. 引入依赖

   ```xml
   <dependency>
     <groupId>com.github.lzj960515</groupId>
     <artifactId>kq-universal-redis-starter</artifactId>
     <version>1.0.1</version>
   </dependency>
   ```

2. 配置Redis（使用Spring的配置）

   ```yaml
   spring:
     redis:
       host: 127.0.0.1
       port: 6379
       password: 123456
   ```

3. 配置Redis key前缀，用于区分不同的业务

   ```yaml
   kq:
     redis:
       prefix: order
   ```

### 使用RedisUtil

   ```java
   RedisUtil.set("a", "xx");
   ```

   > 更多方法查看RedisUtil，如果不满足欢迎提issue或者PR


### 使用注解版的Redis锁

#### 快速开始

在方法上添加@RedisLock注解

```java
@RedisLock(name = "hello", keys = {"#name"})
public String hello(String name){
  return "ok";
}
```

> name：key的名称前缀，与keys字段共同拼接出一个redis key
>
> keys: 能够确定出系统中唯一性资源的key,  必须为spel表达式 如 #id #user.id #user.name

#### 加锁策略

1. 默认加锁策略为快速失败-加锁失败则抛出异常


2. 加锁失败直接返回null
   ```java
       @RedisLock(name = "job", keys = {"'xx'"}, lockStrategy = LockStrategy.SKIP_AND_RETURN_NULL)
       public void job(){
           System.out.println(LocalDateTime.now());
       }
   ```

3. 等待获取锁，直到获取锁成功

   ```java
   @RedisLock(name = "hello", keys = "#hello", lockStrategy = LockStrategy.KEEP_ACQUIRE)
   public String keepAcquire(String hello){
     try {
       Thread.sleep(2000);
     } catch (InterruptedException e) {
       e.printStackTrace();
     }
     return "ok";
   }
   ```

4. 等待获取锁，超过一定时间内仍未获取到锁抛出异常

   ```java
   @RedisLock(name = "hello", keys = "#hello", lockStrategy = LockStrategy.KEEP_ACQUIRE_TIMEOUT, waitTime = 10)
   public String keepAcquireTimeout(String hello){
     try {
       Thread.sleep(2000);
     } catch (InterruptedException e) {
       e.printStackTrace();
     }
     return "ok";
   }
   ```

   > waitTime: 等待超时时间，默认为30秒

#### 锁的类型

1. 默认为可重入锁

2. 读锁

   ```java
   @RedisLock(name = "readwrite", keys = {"#hello"}, lockType = LockType.ReadLock, lockStrategy = LockStrategy.KEEP_ACQUIRE)
   public String readLock(String hello){
     try {
       Thread.sleep(2000);
     } catch (InterruptedException e) {
       e.printStackTrace();
     }
     return "ok";
   }
   ```

3. 写锁

   ```java
   @RedisLock(name = "readwrite", keys = {"#hello"}, lockType = LockType.WriteLock, lockStrategy = LockStrategy.KEEP_ACQUIRE)
   public String writeLock(String hello){
     try {
       Thread.sleep(2000);
     } catch (InterruptedException e) {
       e.printStackTrace();
     }
     return "ok";
   }
   ```

   > 读锁与写锁同时使用才有意义，表现为：读读不阻塞，读写阻塞，写写阻塞

#### 自动解锁

想要加锁后自动解锁可使用属性：leaseTime（过期时间）

当leaseTime=-1时，表示永不过期（逻辑上的永不过期，实际上过期为30s, 在过期时间剩余20s时将自动续期到30s）

#### 异常

当加锁策略为`快速失败`或`尝试获取锁超时`时，将抛出异常`RedisLockFailFastException`或`RedisLockAcquireTimeoutException`, 需要使用者自己捕获异常进行处理

如使用Spring的全局异常捕获切面

```java
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(RedisLockException.class)
  public ResponseEntity<?> handlerException(HttpServletRequest request, RedisLockException ex) {
    if(ex instanceof RedisLockFailFastException){
      System.out.println("获取锁失败-快速失败策略");
    }
    if(ex instanceof RedisLockAcquireTimeoutException){
      System.out.println("获取锁失败-超时策略");
    }
    Map<String, Object> error = new HashMap<>(2, 1);
    error.put("code", -1);
    error.put("msg", ex.getMessage());
    return new ResponseEntity<>(error, HttpStatus.OK);
  }
}
```

> `RedisLockFailFastException`与`RedisLockAcquireTimeoutException`有着共同的父类`RedisLockException`

#### 所有配置

所有配置都将在`@RedisLock`注解类中写明

```java
package com.kqinfo.universal.redis.annotation;

import com.kqinfo.universal.redis.enums.LockStrategy;
import com.kqinfo.universal.redis.enums.LockType;
import com.kqinfo.universal.redis.exception.RedisLockFailFastException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * redis lock
 * 对加上该注解的方法上锁
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisLock {
    /**
     * key的名称前缀，与keys字段共同拼接出一个redis key
     * 如 name = user keys = 123（userId）
     * 最终使用的key为 user123
     */
    String name();

    /**
     * 能够确定出系统中唯一性资源的key
     * 如 用户， 使用用户id为key
     * 或者使用 用户名+手机号
     * 必须为spel表达式 如 #id #user.id #user.name
     */
    String[] keys();

    /**
     * 锁的类型，默认为可重入锁
     *
     * @see LockType
     */
    LockType lockType() default LockType.Lock;

    /**
     * 加锁策略，默认加锁不成功就抛出异常 {@link RedisLockFailFastException}
     *
     * @see LockStrategy
     */
    LockStrategy lockStrategy() default LockStrategy.FAIL_FAST;

    /**
     * 尝试获取锁的超时时间(秒)，默认30s
     * 当加锁策略为 {@link LockStrategy.KEEP_ACQUIRE_TIMEOUT} 时有效
     * -1表示一直等待，等同于{@link LockStrategy.KEEP_ACQUIRE} 策略
     */
    long waitTime() default 30;

    /**
     * 锁的过期时间(秒)，默认30s过期
     * -1表示永不过期（逻辑上的永不过期，实际上过期为30s, 在过期时间剩余20s时将自动续期到30s）
     */
    long leaseTime() default 30;
    /**
     * 自定义异常消息
     */
    String exceptionMessage() default "";
}
```
