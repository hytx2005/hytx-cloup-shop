# 云商城系统接口文档（中文版）

## 文档说明

本文档详细描述了云商城系统改造后的所有RESTful API接口，包含完整的请求参数、响应格式、状态码说明和使用示例。

### 基础信息
- **系统名称**: 云商城系统
- **API版本**: v1.0.0
- **基础URL**: `http://localhost:8080`
- **认证方式**: JWT Token认证
- **数据格式**: JSON

### 通用响应格式

所有API接口都遵循统一的响应格式：

```json
{
  "code": 1,
  "msg": "success",
  "data": {}
}
```

#### 响应字段说明
- **code**: 状态码（1=成功，0=失败，其他=特定错误）
- **msg**: 响应消息
- **data**: 响应数据（成功时返回具体数据，失败时为null）

### 认证方式

所有需要认证的接口都需要在HTTP Header中添加：
```
Authorization: Bearer {your_jwt_token}
```

---

## 用户模块接口

### 1. 用户注册

注册新用户账户

**接口地址**: `POST /users/register`

**请求参数**:

```json
{
  "username": "string", // 用户名（必填，3-20字符）
  "password": "string", // 密码（必填，6-20字符）
  "email": "string",    // 邮箱（可选）
  "phone": "string"     // 手机号（可选）
}
```

**成功响应**:

```json
{
  "code": 1,
  "msg": "注册成功",
  "data": {
    "userId": 1,
    "username": "testuser",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

**错误响应**:

```json
{
  "code": 0,
  "msg": "用户名已存在",
  "data": null
}
```

### 2. 用户登录

用户登录获取访问令牌

**接口地址**: `POST /users/login`

**请求参数**:

```json
{
  "username": "string", // 用户名（必填）
  "password": "string"  // 密码（必填）
}
```

**成功响应**:

```json
{
  "code": 1,
  "msg": "登录成功",
  "data": {
    "userId": 1,
    "username": "testuser",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "level": 1,
    "points": 100
  }
}
```

### 3. 获取用户信息

获取当前登录用户的信息

**接口地址**: `GET /users/profile`

**请求头**:
```
Authorization: Bearer {your_token}
```

**成功响应**:

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
    "points": 100,
    "userStatus": "ACTIVE"
  }
}
```

---

## 商品模块接口

### 1. 获取商品列表

分页获取商品列表，支持搜索和分类筛选

**接口地址**: `GET /products/list`

**查询参数**:
- `page`: 页码（默认1）
- `size`: 每页数量（默认10，最大100）
- `keyword`: 搜索关键词（可选）
- `categoryId`: 分类ID（可选）

**成功响应**:

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 100,
    "pages": 10,
    "page": 1,
    "size": 10,
    "records": [
      {
        "id": 1,
        "name": "苹果手机",
        "price": 5999.00,
        "imageUrl": "http://example.com/phone.jpg",
        "stock": 100,
        "sold": 50,
        "spec": "128GB 黑色",
        "status": 1
      }
    ]
  }
}
```

### 2. 获取商品详情

根据商品ID获取详细信息

**接口地址**: `GET /products/{id}`

**成功响应**:

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1,
    "name": "苹果手机",
    "price": 5999.00,
    "imageUrl": "http://example.com/phone.jpg",
    "stock": 100,
    "sold": 50,
    "spec": "128GB 黑色",
    "description": "最新款苹果手机，性能强劲...",
    "status": 1,
    "createTime": "2024-12-01T10:00:00Z"
  }
}
```

---

## 购物车模块接口

### 1. 添加商品到购物车

将商品添加到用户购物车

**接口地址**: `POST /carts/add`

**请求参数**:

```json
{
  "commodityId": 1,    // 商品ID（必填）
  "quantity": 2        // 数量（必填，大于0）
}
```

**请求头**:
```
Authorization: Bearer {your_token}
```

**成功响应**:

```json
{
  "code": 1,
  "msg": "添加成功",
  "data": null
}
```

### 2. 获取购物车列表

获取当前用户的购物车商品列表

**接口地址**: `GET /carts/list`

**请求头**:
```
Authorization: Bearer {your_token}
```

**成功响应**:

```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "commodityId": 1,
      "commodityName": "苹果手机",
      "commodityPrice": 5999.00,
      "commodityImage": "http://example.com/phone.jpg",
      "quantity": 2,
      "totalPrice": 11998.00
    }
  ]
}
```

### 3. 更新购物车商品数量

修改购物车中商品的数量

**接口地址**: `PUT /carts/update`

**请求参数**:

```json
{
  "cartId": 1,         // 购物车项ID（必填）
  "quantity": 3        // 新数量（必填，大于0）
}
```

**请求头**:
```
Authorization: Bearer {your_token}
```

**成功响应**:

```json
{
  "code": 1,
  "msg": "更新成功",
  "data": null
}
```

### 4. 删除购物车商品

从购物车中移除指定商品

**接口地址**: `DELETE /carts/remove`

**请求参数**:

```json
{
  "cartIds": [1, 2, 3]  // 购物车项ID数组（必填）
}
```

**请求头**:
```
Authorization: Bearer {your_token}
```

**成功响应**:

```json
{
  "code": 1,
  "msg": "删除成功",
  "data": null
}
```

---

## 订单模块接口

### 1. 创建订单

根据购物车商品创建新订单

**接口地址**: `POST /orders/create`

**请求参数**:

```json
{
  "details": [
    {
      "commodityId": 1,  // 商品ID（必填）
      "num": 2            // 购买数量（必填）
    },
    {
      "commodityId": 2,
      "num": 1
    }
  ]
}
```

**请求头**:
```
Authorization: Bearer {your_token}
```

**成功响应**:

```json
{
  "code": 1,
  "msg": "订单创建成功",
  "data": "ORDER202412010001"
}
```

**业务说明**:
- 创建订单时会自动扣减商品库存
- 订单创建后状态为"PENDING"（待支付）
- 订单会自动加入延迟队列（15分钟超时）
- 购物车中对应商品会被自动移除

### 2. 获取用户订单列表

获取当前用户的所有订单

**接口地址**: `GET /orders/get`

**查询参数**:
- `status`: 订单状态筛选（可选：PENDING, PAID, CANCELLED, REFUNDED）
- `page`: 页码（默认1）
- `size`: 每页数量（默认10）

**请求头**:
```
Authorization: Bearer {your_token}
```

**成功响应**:

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
      "commodityName": "苹果手机",
      "commodityUrl": "http://example.com/phone.jpg",
      "commodityNum": 2,
      "money": 11998.00,
      "payStatus": "PAID",
      "payTime": "2024-12-01T10:30:00Z",
      "tradeNo": "PAY1234567890",
      "createTime": "2024-12-01T10:00:00Z",
      "updateTime": "2024-12-01T10:30:00Z"
    }
  ]
}
```

### 3. 删除订单

删除指定的订单（只能删除自己的订单）

**接口地址**: `DELETE /orders/delete`

**请求参数**:

```json
{
  "ids": [1, 2, 3]  // 订单ID数组（必填）
}
```

**请求头**:
```
Authorization: Bearer {your_token}
```

**成功响应**:

```json
{
  "code": 1,
  "msg": "删除成功",
  "data": null
}
```

---

## 支付模块接口

### 1. 创建支付请求

为订单创建支付请求

**接口地址**: `POST /orders/payment/create`

**请求参数**:

```json
{
  "orderNo": "ORDER202412010001",  // 订单号（必填）
  "amount": 11998.00,              // 支付金额（必填）
  "payMethod": "SIMULATE",        // 支付方式（必填：SIMULATE, ALIPAY, WECHAT）
  "description": "购买商品"        // 支付描述（可选）
}
```

**请求头**:
```
Authorization: Bearer {your_token}
```

**成功响应**:

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "orderNo": "ORDER202412010001",
    "status": "SUCCESS",
    "amount": 11998.00,
    "tradeNo": "PAY1234567890",
    "payTime": 1640995200000,
    "payUrl": "/api/orders/payment/simulate/ORDER202412010001"
  }
}
```

### 2. 查询支付状态

查询指定订单的支付状态

**接口地址**: `GET /orders/payment/status/{orderNo}`

**请求头**:
```
Authorization: Bearer {your_token}
```

**成功响应**:

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "orderNo": "ORDER202412010001",
    "status": "SUCCESS",
    "amount": 11998.00,
    "tradeNo": "PAY1234567890",
    "payTime": 1640995200000,
    "errorMsg": null
  }
}
```

### 3. 模拟支付（测试用）

执行模拟支付完成支付流程（仅用于测试环境）

**接口地址**: `GET /orders/payment/simulate/{orderNo}`

**请求头**:
```
Authorization: Bearer {your_token}
```

**成功响应**:

```json
{
  "code": 1,
  "msg": "success",
  "data": "模拟支付成功"
}
```

### 4. 支付回调接口

第三方支付平台回调接口

**接口地址**: `POST /orders/payment/callback`

**请求参数**:

```json
{
  "orderNo": "ORDER202412010001",  // 订单号（必填）
  "amount": 11998.00,              // 支付金额（必填）
  "payStatus": "SUCCESS",          // 支付状态（必填：SUCCESS, FAILED）
  "tradeNo": "PAY1234567890",      // 交易号（必填）
  "payTime": 1640995200000,        // 支付时间（必填，时间戳）
  "sign": "signature_here"         // 签名（必填）
}
```

**成功响应**:

```json
{
  "code": 1,
  "msg": "success",
  "data": "回调处理成功"
}
```

---

## 数据字典

### 订单状态 (payStatus)

| 状态码 | 状态名称 | 说明 |
|--------|----------|------|
| PENDING | 待支付 | 订单已创建，等待用户支付 |
| PAID | 已支付 | 支付成功，订单生效 |
| CANCELLED | 已取消 | 订单已取消（超时或手动取消） |
| REFUNDED | 已退款 | 订单已退款 |
| FAILED | 支付失败 | 支付过程中出现错误 |

### 支付方式 (payMethod)

| 支付方式 | 说明 |
|----------|------|
| SIMULATE | 模拟支付（测试用） |
| ALIPAY | 支付宝支付 |
| WECHAT | 微信支付 |

### 支付状态 (PaymentResultVo.status)

| 状态码 | 说明 |
|--------|------|
| SUCCESS | 支付成功 |
| FAILED | 支付失败 |
| PENDING | 处理中 |
| CANCELLED | 已取消 |
| UNKNOWN | 未知状态 |

### 用户状态 (userStatus)

| 状态码 | 说明 |
|--------|------|
| ACTIVE | 激活状态 |
| DISABLED | 禁用状态 |

---

## 错误码说明

| 错误码 | 说明 | 处理建议 |
|--------|------|----------|
| 1 | 成功 | - |
| 0 | 失败 | 查看msg字段获取具体错误信息 |
| 1001 | 用户未登录 | 重新登录获取token |
| 1002 | 商品不存在 | 检查商品ID是否正确 |
| 1003 | 库存不足 | 减少购买数量或选择其他商品 |
| 1004 | 订单不存在 | 检查订单号是否正确 |
| 1005 | 订单状态不正确 | 确认订单当前状态 |
| 1006 | 支付失败 | 检查支付信息或重试 |
| 1007 | 回调签名验证失败 | 检查签名算法和密钥 |
| 1008 | 参数错误 | 检查请求参数格式 |
| 1009 | 权限不足 | 确认用户权限 |
| 1010 | 系统繁忙 | 稍后重试 |

---

## 支付流程说明

### 正常支付流程

1. **用户下单**
   - 调用 `POST /orders/create` 创建订单
   - 系统扣减库存，生成订单号
   - 订单状态设为 PENDING
   - 订单加入延迟队列（15分钟超时）

2. **发起支付**
   - 调用 `POST /orders/payment/create` 创建支付请求
   - 系统生成支付交易号
   - 返回支付信息（包含模拟支付URL）

3. **完成支付**
   - 调用 `GET /orders/payment/simulate/{orderNo}` 完成模拟支付
   - 系统处理支付回调
   - 更新订单状态为 PAID
   - 从延迟队列中移除订单

4. **查询结果**
   - 调用 `GET /orders/payment/status/{orderNo}` 查询支付状态
   - 确认支付成功

### 超时处理流程

1. 延迟队列检测到订单15分钟未支付
2. 系统检查订单状态是否为 PENDING
3. 如果是，自动更新状态为 CANCELLED
4. 释放已扣减的库存
5. 订单可被重新创建

### 支付失败流程

1. 支付回调返回 FAILED 状态
2. 系统更新订单状态为 FAILED
3. 自动释放已扣减的库存
4. 用户可以重新发起支付或删除订单

---

## 使用示例

### 完整购物流程示例

```bash
# 1. 用户登录
curl -X POST http://localhost:8080/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'

# 2. 添加商品到购物车
curl -X POST http://localhost:8080/carts/add \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{"commodityId":1,"quantity":2}'

# 3. 创建订单
curl -X POST http://localhost:8080/orders/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{"details":[{"commodityId":1,"num":2}]}'

# 4. 发起支付
curl -X POST http://localhost:8080/orders/payment/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{"orderNo":"ORDER202412010001","amount":199.98,"payMethod":"SIMULATE"}'

# 5. 完成支付（模拟）
curl -X GET "http://localhost:8080/orders/payment/simulate/ORDER202412010001" \
  -H "Authorization: Bearer {token}"

# 6. 查询支付状态
curl -X GET "http://localhost:8080/orders/payment/status/ORDER202412010001" \
  -H "Authorization: Bearer {token}"
```

---

## 注意事项

### 安全相关

1. **Token安全**: 妥善保管JWT token，不要在客户端存储敏感信息
2. **参数验证**: 所有接口都会验证参数的有效性
3. **权限控制**: 用户只能操作自己的数据
4. **支付安全**: 支付回调需要签名验证

### 性能相关

1. **分页查询**: 列表接口支持分页，避免一次性查询大量数据
2. **缓存使用**: 商品信息有缓存，更新后可能需要时间同步
3. **并发控制**: 库存操作有并发保护，避免超卖

### 开发相关

1. **测试环境**: 使用SIMULATE支付方式进行测试
2. **错误处理**: 检查所有接口的返回code，处理异常情况
3. **日志记录**: 重要操作建议记录日志用于排查问题

---

## 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| v1.0.0 | 2024-12-01 | 初始版本，包含完整的支付系统改造 |
| v1.0.1 | 2024-12-02 | 完善接口文档和错误处理 |