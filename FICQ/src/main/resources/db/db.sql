create database IF NOT EXISTS ficq charset utf8mb4;
use ficq;
create table IF NOT EXISTS `user`(
    `id` bigint not null auto_increment primary key  comment 'id',
    `user_name` varchar(255) not null comment '用户名',
    `nick_name` varchar(255) not null comment '用户昵称',
    `head_image` varchar(255) default '' comment '用户头像',
    `head_image_thumb` varchar(255) default '' comment '用户头像缩略图',
    `password` varchar(255) not null comment '密码',
    `sex`  tinyint(1) default 0 comment '性别 0:男 1:女',
    `is_banned` tinyint(1) default 0 comment '是否被封禁 0:否 1:是',
    `reason` varchar(255) default ''  comment '被封禁原因',
    `type`  smallint default 1 comment '用户类型 1:普通用户 2:审核账户',
    `signature` varchar(1024) default '' comment '个性签名',
    `last_login_time`  datetime DEFAULT null comment '最后登录时间',
    `created_time` datetime DEFAULT CURRENT_TIMESTAMP comment '创建时间',
    unique key `idx_user_name`(user_name),
    key `idx_nick_name`(nick_name)
) ENGINE=InnoDB CHARSET=utf8mb4 comment '用户';

create table IF NOT EXISTS `friend`(
    `id` bigint not null auto_increment primary key  comment 'id',
    `user_id` bigint not null  comment '用户id',
    `friend_id` bigint not null  comment '好友id',
    `friend_nick_name` varchar(255) not null comment '好友昵称',
    `friend_head_image` varchar(255) default '' comment '好友头像',
    `deleted` tinyint comment '删除标识  0：正常   1：已删除',
    `created_time` datetime DEFAULT CURRENT_TIMESTAMP comment '创建时间',
    key `idx_user_id` (`user_id`),
    key `idx_friend_id` (`friend_id`)
) ENGINE=InnoDB CHARSET=utf8mb4 comment '好友';

create table IF NOT EXISTS `private_message`(
    `id` bigint not null auto_increment primary key comment 'id',
    `send_id` bigint not null  comment '发送用户id',
    `recv_id` bigint not null  comment '接收用户id',
    `content` text   comment '发送内容',
    `type`  tinyint(1) NOT NULL  comment '消息类型 0:文字 1:图片 2:文件 3:语音 4:视频 21:提示',
    `status` tinyint(1) NOT NULL   comment '状态 0:未读 1:已读 2:撤回 3:已读',
    `send_time` datetime DEFAULT CURRENT_TIMESTAMP comment '发送时间',
    key `idx_send_id` (`send_id`),
    key `idx_recv_id` (`recv_id`)
)ENGINE=InnoDB CHARSET=utf8mb4 comment '私聊消息';


create table IF NOT EXISTS `group`(
    `id` bigint not null auto_increment primary key comment 'id',
    `name` varchar(255) not null comment '群名字',
    `owner_id` bigint not null  comment '群主id',
    `head_image` varchar(255) default '' comment '群头像',
    `head_image_thumb` varchar(255) default '' comment '群头像缩略图',
    `notice` varchar(1024)  default '' comment '群公告',
    `is_banned` tinyint(1) default 0 comment '是否被封禁 0:否 1:是',
    `reason` varchar(255) default '' comment '被封禁原因',
    `dissolve` tinyint(1) default 0   comment '是否已解散',
    `created_time` datetime default CURRENT_TIMESTAMP comment '创建时间'
)ENGINE=InnoDB CHARSET=utf8mb4 comment '群';

create table IF NOT EXISTS `group_member`(
    `id` bigint not null auto_increment primary key comment 'id',
    `group_id` bigint not null  comment '群id',
    `user_id` bigint not null  comment '用户id',
    `user_nick_name`  varchar(255) DEFAULT '' comment '用户昵称',
    `remark_nick_name` varchar(255) DEFAULT '' comment '显示昵称备注',
    `head_image` varchar(255) DEFAULT '' comment '用户头像',
    `remark_group_name` varchar(255) DEFAULT '' comment '显示群名备注',
    `quit` tinyint(1) DEFAULT 0  comment '是否已退出',
    `quit_time` datetime DEFAULT NULL comment '退出时间',
    `created_time` datetime DEFAULT CURRENT_TIMESTAMP comment '创建时间',
    key `idx_group_id`(`group_id`),
    key `idx_user_id`(`user_id`)
)ENGINE=InnoDB CHARSET=utf8mb4 comment '群成员';

create table IF NOT EXISTS `group_message`(
    `id` bigint not null auto_increment primary key comment 'id',
    `group_id` bigint not null  comment '群id',
    `send_id` bigint not null  comment '发送用户id',
    `send_nick_name` varchar(255) DEFAULT ''  comment '发送用户昵称',
    `recv_ids` varchar(1024) DEFAULT ''  comment '接收用户id,逗号分隔，为空表示发给所有成员',
    `content` text   comment '发送内容',
    `at_user_ids` varchar(1024) comment '被@的用户id列表，逗号分隔',
    `receipt` tinyint DEFAULT 0  comment '是否回执消息',
    `receipt_ok` tinyint DEFAULT 0  comment '回执消息是否完成',
    `type`  tinyint(1) NOT NULL  comment '消息类型 0:文字 1:图片 2:文件 3:语音 4:视频 21:提示' ,
    `status` tinyint(1) DEFAULT 0 comment '状态 0:未发出  2:撤回 ',
    `send_time` datetime DEFAULT CURRENT_TIMESTAMP comment '发送时间',
    key `idx_group_id` (group_id)
)ENGINE=InnoDB CHARSET=utf8mb4 comment '群消息';

create table IF NOT EXISTS `sensitive_word`(
    `id` bigint not null auto_increment primary key comment 'id',
    `content` varchar(64) not null  comment '敏感词内容',
    `enabled` tinyint DEFAULT 0 COMMENT '是否启用 0:未启用 1:启用',
    `creator` bigint DEFAULT NULL COMMENT '创建者',
    `create_time` datetime  DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
)ENGINE=InnoDB CHARSET=utf8mb4 comment '敏感词';