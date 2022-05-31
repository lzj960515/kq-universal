# 操作日志记录组件

| 功能                 | 完成 |
| -------------------- | ---- |
| 通过注解实现记录日志 | ✔    |
| 实现默认的日志curd   | ✔    |

## 使用方式

引入依赖

   ```xml
   <dependency>
     <groupId>com.github.lzj960515</groupId>
     <artifactId>kq-universal-log-starter</artifactId>
     <version>?</version>
   </dependency>
   ```

### 记录操作日志
数据结构如下：
```java
    private Long id;
/**
 * 操作人id
 */
private String userId;
/**
 * 操作人名称
 */
private String username;
/**
 * 操作内容
 */
private String content;
/**
 * 日志种类
 */
private String category;
/**
 * 业务编号
 */
private String businessNo;
/**
 * 方法参数
 */
private String param;
/**
 * ip地址
 */
private String ipAddress;
/**
 * 地理位置
 */
private String geo;
/**
 * 浏览器
 */
private String browser;
/**
 * 操作时间
 */
private LocalDateTime operateTime;
```
> 后续可以在基础上进行扩展更多需要记录的信息
1. 实现`OperatorService`

   由于操作日志需要记录操作人，所以需要使用者自己实现获取操作人接口

   例子

   ```java
   @Service
   public class OperatorServiceImpl implements OperatorService {
       @Override
       public Operator getOperator(HttpServletRequest request) {
           // 这里可以从RequestHeader 或者 Redis获取到当前的登录用户
           final Operator operator = new Operator();
           operator.setUserId("1");
           operator.setUsername("zhangsan");
           return operator;
       }
   }
   ```
   
2. `LogRecordService`
    
   组件默认实现了`DefaultLogRecordServiceImpl`作为缺省实现(推荐)，如果使用者想要自定义日志操作，可自己实现`LogRecordService`接口，注入Spring容器中即可，组件将使用使用者自定义的实现类
   
   包含方法

   ```java
public interface LogRecordService {

    /**
     * 记录日志
     * @param operateLog 日志信息
     */
    void record(OperateLog operateLog);

    /**
     * 分页查询日志列表
     * @param current 当前页
     * @param size 每页显示条数
     * @param category 日志分类
     * @param businessNo 业务编号
     * @return 日志列表
     */
    PageResult<OperateLog> page(int current, int size, String category, String businessNo);

    /**
     * 查询日志信息
     * @param id id
     * @return 日志信息
     */
    OperateLog getById(Long id);
}
   ```
   
   
   
4. 使用

   在业务方法上加上`LogRecord`注解
   
   ```java
   @Service
   public class TestService {
   
       @LogRecord(template = "订单：{#order.getId()}被修改了", category="order", businessNo = "#order.getId()")
       public void test(Order order){
   
       }
   }
   ```
   
   >template: 模板，支持spel表达式，但是注意要用`{}`包裹
   >
   >category: 日志分类，非必填项，方便区分业务
   >
   >businessNo： 业务编号，定位唯一一条业务数据的所有操作日志，支持spel表达式
   
   执行业务代码块后将自动调用`LogRecordService.record`方法
   
   如果使用的是组件默认实现类，使用者需要查询日志时，需要自行引入LogRecordService到业务代码中，调用page方法
   
   如果使用者自定义了LogRecordService的实现类话，那想怎么玩就怎么玩了
   
5. sql

   如果使用者使用组件的缺省实现`DefaultLogRecordServiceImpl`，需要执行sql操作日志的ddl

   ```sql
   CREATE TABLE `tbl_operate_log` (
   `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
   `user_id` varchar(50) NOT NULL DEFAULT '' COMMENT '操作人id',
   `username` varchar(50) NOT NULL DEFAULT '' COMMENT '操作人',
   `content` varchar(256) NOT NULL DEFAULT '' COMMENT '操作内容',
   `category` varchar(20) NOT NULL DEFAULT '' COMMENT '日志种类',
   `business_no` varchar(50) NOT NULL DEFAULT '' COMMENT '业务编号',
   `param` text COMMENT '方法参数',
   `ip_address` varchar(50) DEFAULT NULL COMMENT 'ip地址',
   `geo` varchar(128) DEFAULT NULL COMMENT '地理位置',
   `browser` varchar(50) DEFAULT NULL COMMENT '浏览器',
   `operate_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
   PRIMARY KEY (`id`),
   KEY `idx_bno` (`business_no`)
   ) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='操作日志';
   ```

   