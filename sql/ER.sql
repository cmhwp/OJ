create table if not exists user
(
    id           bigint auto_increment comment 'id'
    primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    unionId      varchar(256)                           null comment '微信开放平台id',
    mpOpenId     varchar(256)                           null comment '公众号openId',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    gender       tinyint                                null comment '性别 0为女性 1为男性',
    address      varchar(256)                           null comment '地址',
    tags         varchar(1024)                          null comment '标签 JSON',
    birthday     date                                   null comment '生日',
    school       varchar(256)                           null comment '就读学校',
    company      varchar(256)                           null comment '公司',
    position     varchar(256)                           null comment '职位',
    gitHubName   varchar(256)                           null comment 'GigHub',
    websites     text                                   null comment '个人网站、博客或者作品集等',
    concernNum   int          default 0                 null comment '关注数',
    fansNum      int          default 0                 null comment '粉丝数',
    email        varchar(256)                           null comment '邮箱'
    )
    comment '用户' collate = utf8mb4_unicode_ci;

create index idx_unionId
    on user (unionId);