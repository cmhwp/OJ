# 数据库初始化
-- 创建库
create database if not exists my_oj;

-- 切换库
use my_oj;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
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
    index idx_unionId (unionId)
) comment '用户' collate = utf8mb4_unicode_ci;

create table if not exists user
(
    id           bigint auto_increment comment 'id'
        primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    unionId      varchar(256)                           null comment '微信开放平台id',
    mpOpenId     varchar(256)                           null comment '公众号openId',
    userName     varchar(256)                           null comment '用户昵称',
    gitHubName   varchar(256)                           null comment 'GigHub',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    websites     text                                   null comment '个人网站、博客或者作品集等',
    gender       tinyint                                null comment '性别 0为女性 1为男性',
    address      varchar(256)                           null comment '地址',
    tags         varchar(1024)                          null comment '标签 JSON',
    birthday     date                                   null comment '生日',
    school       varchar(256)                           null comment '就读学校',
    company      varchar(256)                           null comment '公司',
    position     varchar(256)                           null comment '职位',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除'
)
    comment '用户' collate = utf8mb4_unicode_ci;

ALTER TABLE user
    ADD Column  gitHubName   varchar(256)                           null comment 'GigHub',
    ADD Column  websites     text                                   null comment '个人网站、博客或者作品集等';
alter table post
    add column   readNum    int      default 0                 null comment '阅读数';
alter table post
    add column replyNum    int      default 0                 null comment '回复数';
alter table post
    add column   status      varchar(256) default 'ban'            not null comment '状态：allow - 允许 ban - 禁止';
alter table post_reply
    add column replyNum    int      default 0                 null comment '评论回复数';
alter table user
    add column concernNum    int      default 0                 null comment '关注数';
alter table user
    add column fansNum      int      default 0                 null comment '粉丝数';
-- 题目表
create table if not exists question
(
    id           bigint auto_increment comment 'id'
    primary key,
    title        varchar(512)                       null comment '标题',
    content      text                               null comment '内容',
    tags         varchar(1024)                      null comment '标签列表（json 数组）',
    frontendCode text                               null comment '前端代码',
    logicCode    text                               null comment '逻辑代码',
    backendCode  text                               null comment '后端代码',
    answer       text                               null comment '题目答案',
    difficulty   tinyint                            null comment '难度 1-简单 2-中等 3-困难',
    submitNum    int      default 0                 not null comment '题目提交数',
    acceptedNum  int      default 0                 not null comment '题目通过数',
    passRate     decimal(5, 2) as (if((`submitNum` = 0), 0, ((`acceptedNum` / `submitNum`) * 100))) stored comment '通过率',
    judgeCase    text                               null comment '判题用例 (json 数组)',
    judgeConfig  text                               null comment '判题配置 (json 对象)',
    thumbNum     int      default 0                 not null comment '点赞数',
    favourNum    int      default 0                 not null comment '收藏数',
    userId       bigint                             not null comment '创建用户 id',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除'
    )
    comment '题目' collate = utf8mb4_unicode_ci;

create index idx_userId
    on question (userId);

-- 题目收藏
create table if not exists question_favour
(
    id         bigint auto_increment comment 'id'
        primary key,
    questionId bigint                             not null comment '题目 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '题目收藏';

create index idx_questionId
    on question_favour (questionId);

-- 题目提交
create table if not exists question_submit
(
    id            bigint auto_increment comment 'id'
        primary key,
    language      varchar(128)                       null comment '编程语言',
    code          text                               null comment '用户代码',
    error_message text                               null comment '错误信息',
    remark        text                               null comment '备注',
    judgeInfo     text                               null comment '判题信息（json 数组）',
    outPut        text                               null comment '实际输出用例',
    status        int      default 0                 null comment '判题状态（0 - 待判题、1 - 判题中、2 - 成功、3 - 失败）',
    questionId    bigint                             not null comment '题目 id',
    userId        bigint                             not null comment '创建用户 id',
    createTime    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete      tinyint  default 0                 not null comment '是否删除'
)
    comment '题目提交' collate = utf8mb4_unicode_ci;

create index idx_questionId
    on question_submit (questionId);

create index idx_userId
    on question_submit (userId);

-- 题目点赞
create table if not exists question_thumb
(
    id         bigint auto_increment comment 'id'
        primary key,
    questionId bigint                             not null comment '题目 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '题目点赞';

create index idx_questionId
    on question_thumb (questionId);

create index idx_userId
    on question_thumb (userId);


-- 帖子表
create table if not exists post
(
    id         bigint auto_increment comment 'id' primary key,
    title      varchar(512)                       null comment '标题',
    content    text                               null comment '内容',
    tags       varchar(1024)                      null comment '标签列表（json 数组）',
    thumbNum   int      default 0                 not null comment '点赞数',
    favourNum  int      default 0                 not null comment '收藏数',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '帖子' collate = utf8mb4_unicode_ci;

-- 帖子点赞表（硬删除）
create table if not exists post_thumb
(
    id         bigint auto_increment comment 'id' primary key,
    postId     bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_postId (postId),
    index idx_userId (userId)
) comment '帖子点赞';

-- 帖子收藏表（硬删除）
create table if not exists post_favour
(
    id         bigint auto_increment comment 'id' primary key,
    postId     bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_postId (postId),
    index idx_userId (userId)
) comment '帖子收藏';

-- 关注表
create table if not exists follow
(
    id         bigint auto_increment comment 'id'
        primary key,
    userId     bigint                             not null comment '关注用户 id',
    followId   bigint                             not null comment '被关注用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '关注';

create index idx_userId
    on follow (userId);
