CREATE TABLE `user`
(
    `id`               int          not null primary key auto_increment,
    `name`           varchar(50)  NOT NULL DEFAULT '' COMMENT '姓名',
    `age`           int(11) NOT NULL DEFAULT '0' COMMENT '年龄',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);