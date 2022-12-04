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

INSERT INTO `dynamic_data_source` (`id`, `name`, `driver_class_name`, `jdbc_url`, `username`, `password`, `minimum_idle`, `maximum_pool_size`, `idle_timeout`, `connection_timeout`)
VALUES
    (1, '测试数据源', 'com.mysql.cj.jdbc.Driver', 'jdbc:mysql://192.168.65.206:3306/test?serverTimezone=Asia/Shanghai&useLegacyDatetimeCode=false&nullNamePatternMatchesAll=true&zeroDateTimeBehavior=CONVERT_TO_NULL&tinyInt1isBit=false&autoReconnect=true&useSSL=false&pinGlobalTxToPhysicalConnection=true&characterEncoding=utf8', 'root', 'root', 10, 10, 600000, 30000);
    (2, '测试数据源2', 'com.mysql.cj.jdbc.Driver', 'jdbc:mysql://192.168.65.206:3306/test2?serverTimezone=Asia/Shanghai&useLegacyDatetimeCode=false&nullNamePatternMatchesAll=true&zeroDateTimeBehavior=CONVERT_TO_NULL&tinyInt1isBit=false&autoReconnect=true&useSSL=false&pinGlobalTxToPhysicalConnection=true&characterEncoding=utf8', 'root', 'root', 10, 10, 600000, 30000);


-- 其他数据源
create database test;
use test;
CREATE TABLE `user` (
                        `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                        `name` varchar(50) DEFAULT NULL,
                        `age` int(11) DEFAULT NULL,
                        `email` varchar(50) DEFAULT NULL,
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

create database test2;
use test2;
CREATE TABLE `user` (
                        `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                        `name` varchar(50) DEFAULT NULL,
                        `age` int(11) DEFAULT NULL,
                        `email` varchar(50) DEFAULT NULL,
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;