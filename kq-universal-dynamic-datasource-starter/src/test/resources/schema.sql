CREATE TABLE `dynamic_data_source` (
                                       `id` int(11) unsigned NOT NULL primary key AUTO_INCREMENT,
                                       `name` varchar(100) NOT NULL COMMENT '数据源名称',
                                       `driver_class_name` varchar(100) DEFAULT NULL COMMENT '数据源驱动名称',
                                       `jdbc_url` varchar(500) NOT NULL,
                                       `username` varchar(50) NOT NULL,
                                       `password` varchar(100) NOT NULL,
                                       `minimum_idle` int(11) NOT NULL COMMENT '最小连接数，默认10',
                                       `maximum_pool_size` int(11) NOT NULL COMMENT '最大连接数，默认10',
                                       `idle_timeout` int(11) NOT NULL COMMENT '连接最大闲置时间，默认10分钟',
                                       `connection_timeout` int(11) NOT NULL COMMENT '连接超时时间，默认30秒'
);

