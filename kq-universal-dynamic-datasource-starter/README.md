# 动态数据源组件

## 功能

1、多数据源管理

数据源信息存储在数据库中，使得能够在不停服时增加新的数据源，或者删除数据源。

2、多数据源切换

可根据数据源id进行数据源切换

> 注：当前版本在同一事务中只可使用一个数据源，这是由于Spring的事务管理机制：在同一事务中，数据库连接保持不变。
>
> 当然，我想也不会有同一事务使用不同数据源的需求吧。这已经是分布式事务解决的问题了，我认为与本组件无关。

## 背景

SQLBuilder(公司某个项目)作为可由使用者通过编写sql的方式自由构造接口的产品，面对如此自由的场景，使用者从不同的库(数据源)中查询数据也将会成为一种需求，达成这种需求就有一个需要解决的问题：如何动态的增减数据源？

## 使用

1、引入组件依赖

```xml
<dependency>
  <groupId>com.github.lzj960515</groupId>
  <artifactId>kq-universal-dynamic-datasource-starter</artifactId>
  <version>version</version>
</dependency>
```

2、初始化数据源表

```sql
CREATE TABLE `dynamic_data_source` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '数据源名称',
  `driver_class_name` varchar(100) DEFAULT NULL COMMENT '数据源驱动名称',
  `jdbc_url` varchar(500) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `minimum_idle` int(11) NOT NULL COMMENT '最小连接数，默认10',
  `maximum_pool_size` int(11) NOT NULL COMMENT '最大连接数，默认10',
  `idle_timeout` int(11) NOT NULL COMMENT '连接最大闲置时间，默认10分钟',
  `connection_timeout` int(11) NOT NULL COMMENT '连接超时时间，默认30秒',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4;
```

3、配置密码加密密钥

```yaml
kq:
  dynamic-datasource:
    secret: 1234qwer4567asdf
```

4、新增数据源

数据源可在表中新增，也可以调用组件中的方法。注意：自己在表中插入的话，记得将密码使用秘钥进行AES加密。

> 组件将在服务启动时获取数据源信息加载到内存中
>
> 调用组件中的方法时，组件会同时往数据库和内存中保存新的数据源信息
>
> 注意：如果服务已经启动，此时手动在数据库中插入数据源，那么该数据源将不会生效。

组件新增数据源方法：

```java
@Resource
private DataSourceRegister dataSourceRegister;

@Test
public void testSave(){
  DynamicDataSourceInfo dynamicDataSourceInfo = new DynamicDataSourceInfo();
  dynamicDataSourceInfo.setName("测试数据源xx");
  dynamicDataSourceInfo.setDriverClassName("");
  dynamicDataSourceInfo.setJdbcUrl("jdbc:mysql://192.168.65.206:3306/test3?serverTimezone=Asia/Shanghai&useLegacyDatetimeCode=false&nullNamePatternMatchesAll=true&zeroDateTimeBehavior=CONVERT_TO_NULL&tinyInt1isBit=false&autoReconnect=true&useSSL=false&pinGlobalTxToPhysicalConnection=true&characterEncoding=utf8");
  dynamicDataSourceInfo.setUsername("root");
  dynamicDataSourceInfo.setPassword("root");
  dataSourceRegister.putDataSource(dynamicDataSourceInfo);
}
```

5、切换数据源

在调用业务代码前，调用方法设置当前数据源信息：

```java
DynamicDataSourceContextHolder.set(数据源id);
```

在调用业务代码后，调用方法清除线程中的数据源信息：

```java
DynamicDataSourceContextHolder.remove();
```

## 例子

假设数据源信息如下：

![image-20221101115632086](https://notes.zijiancode.cn/2022/11/01/image-20221101115632086.png)

SQLBuilder存储的SQL信息：

![image-20221101115842721](https://notes.zijiancode.cn/2022/11/01/image-20221101115842721.png)

那么在执行sql语句1时

```java
// 设置当前数据源为1
DynamicDataSourceContextHolder.set(1);
// 业务代码
// 清除数据源信息
DynamicDataSourceContextHolder.remove();
```
## 数据源配置信息
```java
public class DynamicDataSourceInfo {

    private Integer id;

    /**
     * 数据源名称
     */
    private String name;
    /**
     * 数据库驱动，如：com.mysql.cj.jdbc.Driver
     * HikariCP将尝试通过仅基于jdbcUrl的DriverManager来解析驱动程序，但对于一些较老的驱动程序，还必须指定driverClassName。
     * 支持的驱动：https://github.com/brettwooldridge/HikariCP#popular-datasource-class-names
     */
    private String driverClassName;

    private String jdbcUrl;

    private String username;
    /**
     * 密码使用AES加密
     */
    private String password;
    /**
     * 此属性控制HikariCP试图在池中维护的空闲连接的最小数量。
     * 如果空闲连接低于此值，HikariCP将尽最大努力快速有效地添加额外的连接。
     * 但是，为了获得最大的性能和对峰值需求的响应能力，建议不要设置这个值，而是允许HikariCP充当固定大小的连接池。
     * 默认值:与maximumPoolSize相同
     */
    private Integer minimumIdle;
    /**
     * 此属性控制池允许达到的最大大小，包括空闲连接和正在使用的连接。
     * 基本上，这个值将决定到数据库后端的实际连接的最大数量。
     * 当池达到这个大小，并且没有空闲连接可用时，对getConnection()的调用将阻塞高达connectionTimeout毫秒，然后超时。
     * 默认值:10
     */
    private Integer maximumPoolSize;
    /**
     * 此属性控制连接池中允许闲置的最长时间。
     * 此设置仅适用于minimumIdle定义为小于maximumPoolSize的情况。
     * 当空闲连接池达到最小空闲连接(minimumIdle)时，空闲连接将不会被取消。
     * 默认值:600000(10m)
     */
    private Integer idleTimeout;
    /**
     * 此属性控制请求等待来自池的连接的最大毫秒数。
     * 如果超过这个时间而没有连接可用，则会抛出SQLException。
     * 最低可接受的连接超时时间是250毫秒。默认值:30000(30s)
     */
    private Integer connectionTimeout;
}
```
## 管理数据源API

```java
// 新增数据源
dataSourceRegister.putDataSource(dynamicDataSourceInfo);
// 修改数据源
dataSourceRegister.updateById(dynamicDataSourceInfo);
// 修改密码
dataSourceRegister.updatePassword("123456", 1);
// 删除数据源
dataSourceRegister.deleteById(1);
// 列表查询
dataSourceRegister.list();
// 详情
dataSourceRegister.get(1);
// 重连
dataSourceRegister.reconnect(1);
```

> 在新增修改操作时，都会先校验数据源是否有效

## FAQ

### 1.支持的数据源有哪些

组件使用的连接池为[HikariCP](https://github.com/brettwooldridge/HikariCP), 支持的数据源和该连接池有关，

该连接池列出了以下无须设置driverClassName，可直接通过jdbcUrl确定的[数据源](https://github.com/brettwooldridge/HikariCP#popular-datasource-class-names)

其他数据源须使用者自己尝试。

### 2.引入新的数据源是否需要组件更改驱动依赖

项目引入组件，需要使用其他数据源驱动时，在自己的项目引入相关驱动就可以了。
