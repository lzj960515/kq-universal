package com.kqinfo.universal.dynamic.datasource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zijian Liao
 * @since 2.20.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DynamicDataSourceInfo implements Cloneable {

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

    private String password;
    /**
     * 此属性控制HikariCP试图在池中维护的空闲连接的最小数量。
     * 如果空闲连接低于此值，HikariCP将尽最大努力快速有效地添加额外的连接。
     * 但是，为了获得最大的性能和对峰值需求的响应能力，建议不要设置这个值，而是允许HikariCP充当固定大小的连接池。
     * 默认值:与maximumPoolSize相同
     */
    private int minimumIdle;
    /**
     * 此属性控制池允许达到的最大大小，包括空闲连接和正在使用的连接。
     * 基本上，这个值将决定到数据库后端的实际连接的最大数量。
     * 当池达到这个大小，并且没有空闲连接可用时，对getConnection()的调用将阻塞高达connectionTimeout毫秒，然后超时。
     * 默认值:10
     */
    private int maximumPoolSize;
    /**
     * 此属性控制连接池中允许闲置的最长时间。
     * 此设置仅适用于minimumIdle定义为小于maximumPoolSize的情况。
     * 当空闲连接池达到最小空闲连接(minimumIdle)时，空闲连接将不会被取消。
     * 默认值:600000(10m)
     */
    private int idleTimeout;
    /**
     * 此属性控制请求等待来自池的连接的最大毫秒数。
     * 如果超过这个时间而没有连接可用，则会抛出SQLException。
     * 最低可接受的连接超时时间是250毫秒。默认值:30000(30s)
     */
    private int connectionTimeout;

    /**
     * 连接测试SQL，默认为空
     * 例如：SELECT 1 FROM DUAL, SELECT 1
     * 大部分数据库不需要配置，部分数据库需要按要求配置
     */
    private String connectionTestQuery;

    @Override
    public DynamicDataSourceInfo clone() {
        try {
            return (DynamicDataSourceInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
