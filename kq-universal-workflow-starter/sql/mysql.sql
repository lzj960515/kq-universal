CREATE TABLE `tbl_process` (
                               `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                               `name` varchar(50) NOT NULL DEFAULT '' COMMENT '流程定义名称',
                               `description` varchar(128) NOT NULL COMMENT '流程定义描述',
                               `context` varchar(1024) NOT NULL DEFAULT '' COMMENT '流程定义的内容',
                               `version` int(11) NOT NULL COMMENT '流程定义的版本，每次创建同名称的流程定义，版本+1',
                               `sign` varchar(50) NOT NULL DEFAULT '' COMMENT '签名',
                               `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                               PRIMARY KEY (`id`),
                               UNIQUE KEY `uk_name_version` (`name`,`version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程定义';

CREATE TABLE `tbl_process_instance` (
                                        `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                        `name` varchar(64) NOT NULL DEFAULT '' COMMENT '流程名称',
                                        `call_uri` varchar(128) NOT NULL DEFAULT '' COMMENT '跳转的页面路径',
                                        `process_id` bigint(20) NOT NULL COMMENT '流程定义id',
                                        `business_id` varchar(50) NOT NULL DEFAULT '' COMMENT '业务id',
                                        `creator` varchar(50) NOT NULL DEFAULT '' COMMENT '流程发起人',
                                        `variable` varchar(512) NOT NULL DEFAULT '' COMMENT '流程变量',
                                        `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                        PRIMARY KEY (`id`),
                                        UNIQUE KEY `uk_bid_pid` (`business_id`,`process_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程实例';

CREATE TABLE `tbl_task` (
                            `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                            `name` varchar(50) NOT NULL DEFAULT '' COMMENT '任务名称',
                            `parent_id` bigint(20) NOT NULL COMMENT '父级任务id, 第一个任务则为0',
                            `process_id` bigint(20) NOT NULL COMMENT '流程定义id',
                            `instance_id` bigint(20) NOT NULL COMMENT '流程实例id',
                            `tenant_id` varchar(50) NOT NULL DEFAULT '1' COMMENT '租户id',
                            `business_id` varchar(50) NOT NULL DEFAULT '' COMMENT '业务id',
                            `call_uri` varchar(256) DEFAULT NULL COMMENT '回调地址',
                            `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                            PRIMARY KEY (`id`),
                            KEY `idx_bid` (`business_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程任务';

CREATE TABLE `tbl_task_operator` (
                                     `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                     `task_id` bigint(20) NOT NULL COMMENT '任务id',
                                     `operator_id` varchar(50) NOT NULL DEFAULT '' COMMENT '任务处理人id',
                                     `operator_name` varchar(50) NOT NULL DEFAULT '' COMMENT '任务处理人姓名',
                                     `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                     PRIMARY KEY (`id`),
                                     KEY `idx_tid_oid` (`task_id`,`operator_id`),
                                     KEY `idx_oid_tid` (`operator_id`,`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务处理人关系';

CREATE TABLE `tbl_history_process_instance` (
                                                `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                                `name` varchar(64) NOT NULL DEFAULT '' COMMENT '流程名称',
                                                `call_uri` varchar(128) NOT NULL DEFAULT '' COMMENT '页面跳转地址',
                                                `process_id` bigint(20) unsigned NOT NULL COMMENT '流程定义id',
                                                `business_id` varchar(50) NOT NULL DEFAULT '' COMMENT '业务id',
                                                `status` int(10) NOT NULL COMMENT '流程状态 1.审核中 2.审核通过 3.驳回',
                                                `creator` varchar(50) NOT NULL DEFAULT '' COMMENT '流程发起人',
                                                `variable` varchar(512) NOT NULL DEFAULT '' COMMENT '流程变量',
                                                `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                                PRIMARY KEY (`id`),
                                                KEY `idx_bid` (`business_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='历史流程实例';

CREATE TABLE `tbl_history_task` (
                                    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                    `name` varchar(50) NOT NULL DEFAULT '' COMMENT '任务名称',
                                    `parent_id` bigint(20) unsigned NOT NULL COMMENT '父级任务id, 第一个任务则为start',
                                    `process_id` bigint(20) unsigned NOT NULL COMMENT '流程定义id',
                                    `instance_id` bigint(20) unsigned NOT NULL COMMENT '流程实例id',
                                    `tenant_id` varchar(50) NOT NULL DEFAULT '1' COMMENT '租户id',
                                    `business_id` varchar(50) NOT NULL DEFAULT '' COMMENT '业务id',
                                    `call_uri` varchar(256) DEFAULT NULL COMMENT '页面跳转地址',
                                    `operator_id` varchar(50) NOT NULL DEFAULT '' COMMENT '任务处理人id',
                                    `operator_name` varchar(50) NOT NULL DEFAULT '' COMMENT '任务处理人姓名',
                                    `status` tinyint(4) NOT NULL COMMENT '1.提交审核 2.审核通过 3.驳回',
                                    `reason` varchar(128) DEFAULT NULL COMMENT '原因',
                                    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                    PRIMARY KEY (`id`),
                                    KEY `idx_bid` (`business_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='历史流程任务';