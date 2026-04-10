# 支付系统演示指南

## 概述

本文档演示如何使用改造后的支付系统，包括创建订单、发起支付、支付回调和超时处理等完整流程。

## 前置条件

1. 所有服务已启动并正常运行
2. 数据库已初始化（执行了更新后的base.sql）
3. Redis服务正常运行（用于延迟队列）
4. 用户已登录并获取JWT token

## 演示流程

### 1. 用户登录获取Token

```bash
# 用户登录
curl -X POST http://localhost:8080/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'

# 响应示例
{
  "code": 1,
  "msg": "success",
  "data": {
    "userId": 1,
    "username": "testuser",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### 2. 添加商品到购物车

```bash
# 添加商品到购物车
curl -X POST http://localhost:8080/carts/add \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {your_token}" \
  -d '{
    "commodityId": 1,
    "quantity": 2
  }'
```

### 3. 查看购物车

```bash
# 获取购物车列表
curl -X GET http://localhost:8080/carts/list \
  -H "Authorization: Bearer {your_token}"

# 响应示例
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "commodityId": 1,
      "commodityName": "测试商品",
      "commodityPrice": 99.99,
      "commodityImage": "http://example.com/image.jpg",
      "quantity": 2,
      "totalPrice": 199.98
    }
  ]
}
```

### 4. 创建订单

```bash
# 创建订单
curl -X POST http://localhost:8080/orders/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {your_token}" \
  -d '{
    "details": [
      {
        "commodityId": 1,
        "num": 2
      }
    ]
  }'

# 响应示例
{
  "code": 1,
  "msg": "success",
  "data": "ORDER202412010001"
}
```

**重要**: 订单创建后，系统会自动：
- 扣减商品库存
- 生成唯一订单号
- 将订单添加到延迟队列（15分钟后超时）
- 清理购物车中的对应商品

### 5. 发起支付

```bash
# 创建支付请求
curl -X POST http://localhost:8080/orders/payment/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {your_token}" \
  -d '{
    "orderNo": "ORDER202412010001",
    "amount": 199.98,
    "payMethod": "SIMULATE",
    "description": "购买测试商品"
  }'

# 响应示例
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

### 6. 完成支付（模拟支付）

```bash
# 执行模拟支付
curl -X GET "http://localhost:8080/orders/payment/simulate/ORDER202412010001" \
  -H "Authorization: Bearer {your_token}"

# 响应示例
{
  "code": 1,
  "msg": "success",
  "data": "模拟支付成功"
}
```

### 7. 查询支付状态

```bash
# 查询支付状态
curl -X GET "http://localhost:8080/orders/payment/status/ORDER202412010001" \
  -H "Authorization: Bearer {your_token}"

# 支付成功响应示例
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

### 8. 查看订单列表

```bash
# 获取用户订单列表
curl -X GET http://localhost:8080/orders/get \
  -H "Authorization: Bearer {your_token}"

# 响应示例
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "orderNo": "ORDER202412010001",
      "commodityId": 1,
      "commodityName": "测试商品",
      "commodityUrl": "http://example.com/image.jpg",
      "commodityNum": 2,
      "money": 199.98,
      "payStatus": "PAID",
      "payTime": "2024-12-01T10:00:00Z",
      "tradeNo": "PAY1234567890",
      "createTime": "2024-12-01T09:50:00Z",
      "updateTime": "2024-12-01T10:00:00Z"
    }
  ]
}
```

## 超时处理演示

### 1. 创建订单但不支付

```bash
# 创建新订单
curl -X POST http://localhost:8080/orders/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {your_token}" \
  -d '{
    "details": [
      {
        "commodityId": 2,
        "num": 1
      }
    ]
  }'

# 响应示例
{
  "code": 1,
  "msg": "success",
  "data": "ORDER202412010002"
}
```

### 2. 等待15分钟后查询状态

```bash
# 查询订单状态（15分钟后）
curl -X GET "http://localhost:8080/orders/payment/status/ORDER202412010002" \
  -H "Authorization: Bearer {your_token}"

# 超时后响应示例
{
  "code": 1,
  "msg": "success",
  "data": {
    "orderNo": "ORDER202412010002",
    "status": "CANCELLED",
    "amount": 99.99,
    "tradeNo": null,
    "payTime": null
  }
}
```

## 支付回调接口演示

### 第三方支付平台回调

```bash
# 支付成功回调
curl -X POST http://localhost:8080/orders/payment/callback \
  -H "Content-Type: application/json" \
  -d '{
    "orderNo": "ORDER202412010001",
    "amount": 199.98,
    "payStatus": "SUCCESS",
    "tradeNo": "PAY1234567890",
    "payTime": 1640995200000,
    "sign": "generated_signature_here"
  }'

# 响应示例
{
  "code": 1,
  "msg": "success",
  "data": "回调处理成功"
}
```

## 错误情况处理

### 1. 库存不足

```bash
# 尝试购买超过库存数量的商品
curl -X POST http://localhost:8080/orders/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {your_token}" \
  -d '{
    "details": [
      {
        "commodityId": 1,
        "num": 999999
      }
    ]
  }'

# 响应示例
{
  "code": 0,
  "msg": "商品库存不足",
  "data": null
}
```

### 2. 订单不存在

```bash
# 查询不存在的订单
curl -X GET "http://localhost:8080/orders/payment/status/ORDER_NOT_EXIST" \
  -H "Authorization: Bearer {your_token}"

# 响应示例
{
  "code": 0,
  "msg": "订单不存在",
  "data": null
}
```

### 3. 支付状态不正确

```bash
# 对已支付的订单重复支付
curl -X POST http://localhost:8080/orders/payment/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {your_token}" \
  -d '{
    "orderNo": "ORDER202412010001",
    "amount": 199.98,
    "payMethod": "SIMULATE",
    "description": "重复支付测试"
  }'

# 响应示例
{
  "code": 0,
  "msg": "订单状态不正确，无法支付",
  "data": null
}
```

## 支付流程状态说明

### 订单状态流转

```
创建订单 -> PENDING (待支付)
    -> 支付成功 -> PAID (已支付)
    -> 支付失败 -> FAILED (支付失败)
    -> 15分钟超时 -> CANCELLED (已取消)
    -> 退款 -> REFUNDED (已退款)
```

### 库存流转

```
创建订单 -> 扣减库存 (预占库存)
    -> 支付成功 -> 确认库存扣减
    -> 支付失败 -> 释放库存
    -> 订单取消 -> 释放库存
```

## 开发测试建议

### 1. 使用PaymentTestUtil进行单元测试

```java
@Autowired
private PaymentTestUtil paymentTestUtil;

// 测试完整支付流程
paymentTestUtil.testCompletePaymentFlow("ORDER202412010001", new BigDecimal("199.99"));

// 测试支付失败流程
paymentTestUtil.testPaymentFailureFlow("ORDER202412010002", new BigDecimal("299.99"));
```

### 2. 监控延迟队列

延迟队列中的订单数量可以通过日志查看：
```
// 在控制台输出中查找
延迟队列中待处理的订单数量: 5
```

### 3. 测试并发支付

可以使用压测工具模拟多个用户同时支付，验证并发控制是否正确。

## 常见问题

### Q: 订单创建成功但未支付，库存什么时候释放？
A: 订单创建15分钟后，如果仍未支付，系统会自动取消订单并释放库存。

### Q: 支付回调失败怎么办？
A: 支付回调失败时，系统会返回错误信息。可以重新发送回调请求，系统会验证订单状态避免重复处理。

### Q: 如何测试支付超时功能？
A: 创建订单后不进行支付，等待15分钟后查询订单状态，应该显示为"已取消"。

### Q: 支付金额与订单金额不匹配会怎样？
A: 系统会验证支付金额，如果不匹配会拒绝处理支付回调。

## 性能监控

### 关键指标
- 订单创建速率
- 支付成功率
- 平均支付时间
- 超时订单比例
- 库存扣减响应时间

### 日志查看
- 订单服务日志：支付相关操作记录
- Redis日志：延迟队列操作记录
- 应用监控：系统性能指标

## 安全注意事项

1. **支付回调验证**: 确保回调接口的签名验证正确实现
2. **金额验证**: 支付金额必须与订单金额一致
3. **状态验证**: 避免重复支付和状态错误
4. **库存安全**: 使用悲观锁确保库存扣减的准确性

## 后续扩展

1. **接入真实支付**: 替换模拟支付为支付宝、微信支付等
2. **增加支付方式**: 支持银行卡、余额等多种支付方式
3. **支付流水**: 增加详细的支付记录和审计功能
4. **退款功能**: 实现完整的退款流程
5. **对账系统**: 增加支付对账和差错处理功能