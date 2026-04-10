# 云商城系统 API 文档

## 概述

本文档描述了云商城系统的RESTful API接口，包含用户管理、商品管理、购物车、订单管理和支付等核心功能。

### 基础信息
- **基础URL**: `/api`
- **API版本**: v1.0
- **认证方式**: JWT Token (通过HTTP Header `Authorization: Bearer {token}`)

## 用户模块 API

### 用户注册

```
POST /users/register
```

**请求参数**:
```json
{
  "username": "string",
  "password": "string",
  "email": "string",
  "phone": "string"
}
```

**响应**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "userId": 1,
    "username": "testuser",
    "token": "jwt_token_here"
  }
}
```

### 用户登录

```
POST /users/login
```

**请求参数**:
```json
{
  "username": "string",
  "password": "string"
}
```

**响应**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "userId": 1,
    "username": "testuser",
    "token": "jwt_token_here"
  }
}
```

### 获取用户信息

```
GET /users/profile
```

**响应**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "userId": 1,
    "username": "testuser",
    "email": "test@example.com",
    "phone": "13800138000",
    "level": 1,
    "points": 100
  }
}
```

## 商品模块 API

### 获取商品列表

```
GET /products/list
```

**查询参数**:
- `page`: 页码 (默认1)
- `size`: 每页数量 (默认10)
- `keyword`: 搜索关键词
- `categoryId`: 分类ID

**响应**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 100,
    "pages": 10,
    "records": [
      {
        "id": 1,
        "name": "商品名称",
        "price": 99.99,
        "imageUrl": "http://example.com/image.jpg",
        "stock": 100,
        "sold": 50,
        "spec": "规格信息"
      }
    ]
  }
}
```

### 获取商品详情

```
GET /products/{id}
```

**响应**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1,
    "name": "商品名称",
    "price": 99.99,
    "imageUrl": "http://example.com/image.jpg",
    "stock": 100,
    "sold": 50,
    "spec": "规格信息",
    "description": "商品详细描述",
    "status": 1
  }
}
```

## 购物车模块 API

### 添加到购物车

```
POST /carts/add
```

**请求参数**:
```json
{
  "commodityId": 1,
  "quantity": 2
}
```

**响应**:
```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

### 获取购物车列表

```
GET /carts/list
```

**响应**:
```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "commodityId": 1,
      "commodityName": "商品名称",
      "commodityPrice": 99.99,
      "commodityImage": "http://example.com/image.jpg",
      "quantity": 2,
      "totalPrice": 199.98
    }
  ]
}
```

### 更新购物车商品数量

```
PUT /carts/update
```

**请求参数**:
```json
{
  "cartId": 1,
  "quantity": 3
}
```

**响应**:
```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

### 删除购物车商品

```
DELETE /carts/remove
```

**请求参数**:
```json
{
  "cartIds": [1, 2, 3]
}
```

**响应**:
```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

## 订单模块 API

### 创建订单

```
POST /orders/create
```

**请求参数**:
```json
{
  "details": [
    {
      "commodityId": 1,
      "num": 2
    },
    {
      "commodityId": 2,
      "num": 1
    }
  ]
}
```

**响应**:
```json
{
  "code": 1,
  "msg": "success",
  "data": "ORDER202412010001"
}
```

### 获取用户订单列表

```
GET /orders/get
```

**查询参数**:
- `status`: 订单状态 (可选)
- `page`: 页码 (默认1)
- `size`: 每页数量 (默认10)

**响应**:
```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "orderNo": "ORDER202412010001",
      "commodityId": 1,
      "commodityName": "商品名称",
      "commodityUrl": "http://example.com/image.jpg",
      "commodityNum": 2,
      "money": 199.98,
      "payStatus": "PENDING",
      "payTime": "2024-12-01T10:00:00Z",
      "tradeNo": "PAY1234567890",
      "createTime": "2024-12-01T09:50:00Z",
      "updateTime": "2024-12-01T10:00:00Z"
    }
  ]
}
```

### 删除订单

```
DELETE /orders/delete
```

**请求参数**:
```json
{
  "ids": [1, 2, 3]
}
```

**响应**:
```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

## 支付模块 API

### 创建支付请求

```
POST /orders/payment/create
```

**请求参数**:
```json
{
  "orderNo": "ORDER202412010001",
  "amount": 199.98,
  "payMethod": "SIMULATE",
  "description": "购买商品"
}
```

**响应**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "orderNo": "ORDER202412010001",
    "status": "SUCCESS",
    "amount": 199.98,
    "tradeNo": "PAY1234567890",
    "payTime": 1640995200000,
    "payUrl": "/api/orders/payment/simulate/ORDER202412010001"
  }
}
```

### 查询支付状态

```
GET /orders/payment/status/{orderNo}
```

**响应**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "orderNo": "ORDER202412010001",
    "status": "SUCCESS",
    "amount": 199.98,
    "tradeNo": "PAY1234567890",
    "payTime": 1640995200000
  }
}
```

### 模拟支付（测试用）

```
GET /orders/payment/simulate/{orderNo}
```

**响应**:
```json
{
  "code": 1,
  "msg": "success",
  "data": "模拟支付成功"
}
```

### 支付回调（第三方支付平台调用）

```
POST /orders/payment/callback
```

**请求参数**:
```json
{
  "orderNo": "ORDER202412010001",
  "amount": 199.98,
  "payStatus": "SUCCESS",
  "tradeNo": "PAY1234567890",
  "payTime": 1640995200000,
  "sign": "signature_here"
}
```

**响应**:
```json
{
  "code": 1,
  "msg": "success",
  "data": "回调处理成功"
}
```

## 数据字典

### 订单状态 (payStatus)
- `PENDING`: 待支付
- `PAID`: 已支付
- `CANCELLED`: 已取消
- `REFUNDED`: 已退款
- `FAILED`: 支付失败

### 支付方式 (payMethod)
- `ALIPAY`: 支付宝
- `WECHAT`: 微信支付
- `SIMULATE`: 模拟支付（测试用）

### 支付状态 (payStatus in PaymentResultVo)
- `SUCCESS`: 支付成功
- `FAILED`: 支付失败
- `PENDING`: 处理中
- `CANCELLED`: 已取消

## 错误码说明

- `1`: 成功
- `0`: 失败
- `1001`: 用户未登录
- `1002`: 商品不存在
- `1003`: 库存不足
- `1004`: 订单不存在
- `1005`: 订单状态不正确
- `1006`: 支付失败
- `1007`: 回调签名验证失败

## 支付流程说明

### 正常支付流程
1. 用户创建订单：`POST /orders/create`
2. 系统生成订单号并扣减库存
3. 订单进入延迟队列（15分钟后超时）
4. 用户发起支付：`POST /orders/payment/create`
5. 系统生成支付交易号
6. 用户完成支付（模拟：`GET /orders/payment/simulate/{orderNo}`）
7. 系统处理支付回调，更新订单状态
8. 从延迟队列中移除订单

### 超时处理流程
1. 延迟队列检测到订单超时（15分钟）
2. 系统检查订单状态是否为"待支付"
3. 如果是，自动取消订单并释放库存
4. 更新订单状态为"已取消"

### 支付失败流程
1. 支付回调返回失败状态
2. 系统更新订单状态为"支付失败"
3. 自动释放已扣减的库存
4. 订单可被重新支付或删除

## 开发说明

### 环境配置
- **开发环境**: 端口8081-8084
- **数据库**: MySQL 8.0+
- **缓存**: Redis
- **消息队列**: Kafka
- **服务注册**: Nacos

### 测试支付
使用模拟支付方式进行测试：
1. 创建订单获取订单号
2. 调用模拟支付接口完成支付
3. 查询支付状态验证结果

### 注意事项
- 所有金额字段使用BigDecimal类型，避免浮点数精度问题
- 库存操作使用悲观锁确保并发安全
- 支付超时时间为15分钟，可根据需求调整
- 延迟队列使用Redis实现，确保可靠性