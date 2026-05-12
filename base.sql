create table cart
(
    id              bigint auto_increment comment '购物车id'
        primary key,
    user_id         bigint         null comment '用户id',
    commodity_id    bigint         null comment '商品id',
    commodity_name  varchar(255)   null comment '商品名称',
    commodity_price decimal(10, 2) null comment '商品单价',
    commodity_url   varchar(255)   null comment '商品的url',
    commodity_num   int            null comment '商品数量',
    spec            varchar(255)   null comment '商品规格'
)
    comment '购物车';

create table commodity
(
    id        bigint auto_increment
        primary key,
    name      varchar(255)   null comment '商品名称',
    price     decimal(10, 2) null comment '商品价格',
    image_url varchar(255)   null comment '商品图片链接',
    stock     int            null comment '库存数量',
    sold      int            null comment '已售数量',
    spec      varchar(255)   null comment '商品规格',
    status    int            null comment '状态（0/1）',
    user_id   bigint         null comment '用户id'
)
    comment '商品模块';

create table orders
(
    id             bigint auto_increment
        primary key,
    user_id        bigint         null comment '用户id',
    money          decimal(10, 2) null comment '金额',
    pay_status     varchar(50)    null comment '支付状态：PENDING-待支付，PAID-已支付，CANCELLED-已取消，REFUNDED-已退款',
    commodity_id   bigint         null comment '商品id',
    commodity_name varchar(255)   null comment '商品名称',
    commodity_url  varchar(255)   null comment '商品图片',
    commodity_num  int            null comment '商品数量',
    order_no       varchar(255)   null comment '订单号',
    pay_time       datetime       null comment '支付时间',
    trade_no       varchar(255)   null comment '支付交易号',
    create_time    datetime       null comment '创建时间',
    update_time    datetime       null comment '更新时间'
)
    comment '订单模块';

create table user
(
    id          bigint auto_increment comment '主键'
        primary key,
    user_name   varchar(255) null comment '用户名',
    password    varchar(255) null comment '加密后的密码',
    phone       varchar(11)  null comment '联系电话',
    email       varchar(255) null comment '邮箱',
    user_status varchar(50)  null comment '用户状态：ACTIVE-激活，DISABLED-禁用',
    level       int          null comment '用户等级',
    points      int          null comment '用户积分',
    create_time datetime     null comment '注册时间',
    update_time datetime     null comment '更新时间'
)
    comment '用户模块';


