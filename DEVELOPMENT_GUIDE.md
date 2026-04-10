# 云商城系统开发指南

## 项目概述

云商城系统是一个基于Spring Cloud微服务架构的电商平台，包含用户管理、商品管理、购物车、订单管理和支付等核心功能。

## 技术栈

- **后端框架**: Spring Boot 2.7.12, Spring Cloud 2021.0.8
- **服务发现**: Alibaba Nacos
- **RPC框架**: Apache Dubbo 3.3.1
- **数据库**: MySQL 8.0.23
- **ORM**: MyBatis-Plus 3.4.3
- **缓存**: Redis + Redisson
- **消息队列**: Apache Kafka
- **搜索**: Elasticsearch + Easy-ES
- **API网关**: Spring Cloud Gateway
- **文档**: Swagger 2.9.2
- **构建工具**: Maven
- **Java版本**: 17

## 项目结构

```
cloud-shop/
├── gateway/                 # API网关服务 (端口: 8080)
├── users/                   # 用户服务 (端口: 8082)
├── products/               # 商品服务 (端口: 8083)
├── carts/                  # 购物车服务 (端口: 8084)
├── orders/                 # 订单服务 (端口: 8081)
├── shop-common/            # 公共组件模块
├── shop-api/               # API定义模块
├── base.sql                # 数据库初始化脚本
├── pom.xml                 # 父级Maven配置
└── CLAUDE.md              # Claude AI助手指南
```

## 环境配置

### 开发环境要求

1. **JDK 17**
2. **Maven 3.6+**
3. **MySQL 8.0+**
4. **Redis 6.0+**
5. **Nacos 2.0+** (服务注册与配置中心)
6. **Kafka 2.8+** (消息队列)

### 配置文件说明

#### Nacos配置
- **地址**: 121.43.197.241:8848
- **命名空间**: 根据环境配置
- **用户名/密码**: nacos/nacos

#### Kafka配置
- **地址**: 122.51.149.223:9092
- **消费者组**: default-group

#### 数据库配置
通过Nacos配置中心管理，包含：
- 数据库连接信息
- Redis连接信息
- 其他服务配置

## 快速开始

### 1. 环境准备

```bash
# 克隆项目
git clone <repository-url>
cd cloud-shop

# 安装依赖
mvn clean install -DskipTests
```

### 2. 数据库初始化

执行 `base.sql` 脚本创建数据库表结构：

```bash
mysql -u username -p < base.sql
```

### 3. 启动服务

按照以下顺序启动服务：

```bash
# 1. 启动用户服务
mvn spring-boot:run -pl users

# 2. 启动商品服务
mvn spring-boot:run -pl products

# 3. 启动购物车服务
mvn spring-boot:run -pl carts

# 4. 启动订单服务
mvn spring-boot:run -pl orders

# 5. 启动网关服务
mvn spring-boot:run -pl gateway
```

### 4. 访问API文档

启动完成后，访问以下地址查看API文档：
- Swagger UI: http://localhost:8081/swagger-ui.html
- API文档: http://localhost:8080/api-docs

## 支付系统使用指南

### 支付流程

1. **创建订单**
   ```bash
   POST /orders/create
   ```
   创建订单后，系统会自动扣减库存并生成订单号。

2. **发起支付**
   ```bash
   POST /orders/payment/create
   ```
   创建支付请求，获取支付信息。

3. **完成支付**
   ```bash
   GET /orders/payment/simulate/{orderNo}
   ```
   使用模拟支付完成支付流程（测试环境）。

4. **查询支付状态**
   ```bash
   GET /orders/payment/status/{orderNo}
   ```
   查询订单的支付状态。

### 支付超时处理

- 订单创建后15分钟内未支付，系统会自动取消订单
- 取消订单时会自动释放已扣减的库存
- 延迟队列使用Redis实现，确保可靠性

### 测试支付功能

使用 `PaymentTestUtil` 类进行支付功能测试：

```java
@Autowired
private PaymentTestUtil paymentTestUtil;

// 测试完整支付流程
paymentTestUtil.testCompletePaymentFlow("ORDER202412010001", new BigDecimal("199.99"));

// 测试支付失败流程
paymentTestUtil.testPaymentFailureFlow("ORDER202412010002", new BigDecimal("299.99"));
```

## API开发规范

### 控制器层规范

1. **URL命名**
   - 使用RESTful风格
   - 复数形式表示资源
   - 小写字母，单词间用连字符分隔

2. **HTTP方法**
   - GET: 查询
   - POST: 创建
   - PUT: 更新
   - DELETE: 删除

3. **响应格式**
   ```java
   @RestController
   @RequestMapping("/orders")
   public class OrdersController {

       @PostMapping("/create")
       public Result<String> createOrder(@RequestBody OrderCreDto orderCreDto) {
           // 业务逻辑
           return Result.success(orderNo);
       }
   }
   ```

### 服务层规范

1. **接口定义**
   ```java
   public interface IOrdersService extends IService<Orders> {
       String createOrder(OrderCreDto orderCreDto);
       boolean deleteOrders(OrderDelDto dto);
   }
   ```

2. **实现类**
   ```java
   @Service
   @Transactional(rollbackFor = Exception.class)
   public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders>
           implements IOrdersService {
       // 实现方法
   }
   ```

### 数据传输对象规范

1. **DTO (Data Transfer Object)**
   - 用于接口参数传递
   - 类名以 `Dto` 结尾
   - 包含必要的验证注解

2. **VO (Value Object)**
   - 用于接口返回值
   - 类名以 `Vo` 结尾
   - 包含完整的返回数据结构

3. **PO (Persistent Object)**
   - 数据库实体类
   - 类名以实体名称表示
   - 使用MyBatis-Plus注解

### 异常处理规范

1. **自定义异常**
   ```java
   throw new BaseException("错误信息");
   ```

2. **全局异常处理**
   - 在 `shop-common` 模块中定义全局异常处理器
   - 统一返回格式

## 数据库设计

### 核心表结构

1. **用户表 (user)**
   - id: 用户ID
   - username: 用户名
   - password: 密码（BCrypt加密）
   - email: 邮箱
   - phone: 手机号
   - level: 用户等级
   - points: 积分

2. **商品表 (commodity)**
   - id: 商品ID
   - name: 商品名称
   - price: 价格
   - stock: 总库存
   - sold: 已售数量
   - image_url: 商品图片
   - spec: 规格信息
   - status: 状态

3. **订单表 (orders)**
   - id: 订单项ID
   - user_id: 用户ID
   - order_no: 订单号
   - commodity_id: 商品ID
   - commodity_name: 商品名称
   - commodity_num: 商品数量
   - money: 金额
   - pay_status: 支付状态
   - pay_time: 支付时间
   - trade_no: 交易号

4. **购物车表 (cart)**
   - id: 购物车项ID
   - user_id: 用户ID
   - commodity_id: 商品ID
   - quantity: 数量

### 索引设计

- 用户表：username唯一索引
- 商品表：name普通索引
- 订单表：order_no唯一索引，user_id普通索引
- 购物车表：user_id普通索引

## 消息队列使用

### Kafka主题

1. **add_commodity_to_redis**
   - 用途：商品信息同步到Redis
   - 生产者：商品服务
   - 消费者：商品服务

### 延迟消息实现

使用Redis实现延迟队列：

```java
// 添加延迟任务
delayedQueue.offer(orderNo, 15, TimeUnit.MINUTES);

// 消费延迟消息
String orderNo = orderTimeoutQueue.take();
processTimeoutOrder(orderNo);
```

## 缓存策略

### Redis缓存设计

1. **商品信息缓存**
   - Key: `commodity`
   - Value: 商品ID到商品信息的映射
   - 过期时间: 30分钟

2. **库存管理**
   - 使用Redis存储实时库存信息
   - 支持高并发库存扣减

### 缓存更新策略

1. **主动更新**
   - 商品信息变更时主动更新缓存
   - 通过Kafka消息通知其他服务

2. **被动更新**
   - 缓存过期后重新加载
   - 保证数据最终一致性

## 安全考虑

### 认证与授权

1. **JWT认证**
   - 用户登录后获取JWT token
   - 网关验证token有效性
   - 用户信息通过Dubbo attachment传递

2. **接口权限**
   - 网关层统一认证
   - 服务间调用通过Dubbo进行

### 数据安全

1. **密码加密**
   - 使用BCrypt算法加密用户密码
   - 盐值随机生成

2. **敏感信息**
   - 配置信息通过Nacos管理
   - 不硬编码在代码中

## 性能优化

### 数据库优化

1. **索引优化**
   - 为常用查询字段创建索引
   - 避免全表扫描

2. **连接池**
   - 使用HikariCP连接池
   - 合理配置连接数

### 缓存优化

1. **多级缓存**
   - Redis缓存热点数据
   - 本地缓存减少Redis访问

2. **缓存预热**
   - 系统启动时预热常用数据
   - 通过Kafka消息触发缓存更新

### 并发控制

1. **库存扣减**
   - 使用数据库悲观锁
   - 防止超卖问题

2. **分布式锁**
   - 使用Redisson实现分布式锁
   - 保证分布式环境下的数据一致性

## 监控与日志

### 日志配置

- 使用SLF4J + Logback
- 按服务区分日志文件
- 日志级别：INFO（生产环境），DEBUG（开发环境）

### 监控指标

1. **业务指标**
   - 订单创建速率
   - 支付成功率
   - 库存变化

2. **系统指标**
   - 服务响应时间
   - 数据库连接池状态
   - Redis缓存命中率

## 部署说明

### 开发环境

1. 本地启动所有服务
2. 使用H2内存数据库（可选）
3. 配置开发环境参数

### 生产环境

1. **服务部署**
   - 使用Docker容器化部署
   - 配置负载均衡
   - 设置服务健康检查

2. **数据库部署**
   - MySQL主从复制
   - Redis集群
   - Kafka集群

3. **监控部署**
   - Prometheus + Grafana监控
   - ELK日志收集
   - 告警系统

## 常见问题

### 1. 服务启动失败

**问题**: 服务无法连接到Nacos
**解决**: 检查Nacos服务状态和配置信息

### 2. 数据库连接失败

**问题**: 无法连接到MySQL
**解决**: 检查数据库配置和网络连接

### 3. 支付超时不生效

**问题**: 订单15分钟后没有自动取消
**解决**: 检查Redis连接和延迟队列配置

### 4. 库存扣减异常

**问题**: 并发下单时库存扣减错误
**解决**: 检查悲观锁实现和事务配置

## 扩展建议

### 功能扩展

1. **商品评价系统**
   - 用户可以对已购买商品进行评价
   - 支持图片和文字评价

2. **优惠券系统**
   - 支持多种优惠券类型
   - 优惠券使用限制和有效期

3. **物流跟踪**
   - 集成第三方物流API
   - 实时跟踪订单配送状态

### 架构优化

1. **服务拆分**
   - 将支付服务独立出来
   - 增加用户积分服务

2. **缓存优化**
   - 引入多级缓存架构
   - 使用CDN加速静态资源

3. **消息队列优化**
   - 增加更多业务topic
   - 实现消息重试机制

## 版本历史

- **v1.0.0** (2024-12-01)
  - 初始版本
  - 基础电商功能
  - 支付系统重构
  - 延迟消息队列

- **v1.1.0** (计划中)
  - 商品评价系统
  - 优惠券功能
  - 性能优化