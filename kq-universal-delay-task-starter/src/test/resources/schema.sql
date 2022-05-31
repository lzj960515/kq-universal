CREATE TABLE `delay_task`
(
    `id`               int          not null primary key auto_increment,
    `name`           varchar(50)  NOT NULL DEFAULT '' COMMENT '任务名：任务的名称，与执行任务的方法对应，用于执行任务时寻找执行任务的方法',
    `info`           varchar(512) NOT NULL DEFAULT '' COMMENT '任务信息：放置执行任务所需的参数信息',
    `description`    varchar(128)          DEFAULT '' COMMENT '任务描述',
    `execute_time`    bigint(20)   NOT NULL COMMENT '执行时间 时间戳',
    `execute_status`  tinyint(4)   NOT NULL COMMENT '执行状态：1.创建 2.执行中 3.执行成功 4.执行失败',
    `execute_message` varchar(256)          DEFAULT '' COMMENT '执行结果信息',
    `real_execute_time` bigint(20) NOT NULL COMMENT '实际执行时间 时间戳',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);