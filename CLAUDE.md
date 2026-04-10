# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Cloud Mall is a microservices-based e-commerce platform built with Spring Boot, Spring Cloud, and Apache Dubbo. The system consists of multiple independent services that communicate through both REST APIs and Dubbo RPC.

## Architecture

### Core Technologies
- **Framework**: Spring Boot 2.7.12, Spring Cloud 2021.0.8
- **Service Discovery**: Alibaba Nacos
- **RPC Framework**: Apache Dubbo 3.3.1
- **Database**: MySQL 8.0.23 with MyBatis-Plus
- **Message Queue**: Apache Kafka
- **Search**: Elasticsearch with Easy-ES
- **Cache**: Redis with Redisson
- **API Gateway**: Spring Cloud Gateway
- **Build Tool**: Maven
- **Java Version**: 17

### Service Structure

The project follows a modular microservices architecture:

1. **gateway** - API Gateway service (port 8080)
   - Routes requests to appropriate services
   - Handles authentication and JWT validation
   - Spring Cloud Gateway based

2. **users** - User management service (port 8082)
   - User registration, login, authentication
   - JWT token generation and validation
   - Bcrypt password encryption

3. **products** - Product catalog service (port 8083)
   - Product management and search
   - Elasticsearch integration for product search
   - Kafka integration for product updates
   - XXL-Job integration for scheduled tasks

4. **carts** - Shopping cart service (port 8084)
   - Cart management (add, remove, update items)
   - Redis-based cart storage

5. **orders** - Order management service (port 8081)
   - Order creation, management, and processing
   - Kafka integration for order events
   - MySQL database with MyBatis-Plus

6. **shop-common** - Shared common module
   - Common utilities, DTOs, and configurations
   - Exception handling and result wrappers
   - Dubbo filters and interceptors

7. **shop-api** - API definitions module
   - Dubbo service interfaces
   - Shared DTOs and VOs
   - Service contracts

## Development Commands

### Build Commands

```bash
# Build entire project
mvn clean install

# Build specific service
cd orders && mvn clean install

# Build with skipping tests
mvn clean install -DskipTests

# Build in development mode
mvn spring-boot:run -pl orders
```

### Run Commands

```bash
# Run specific service
mvn spring-boot:run -pl users
mvn spring-boot:run -pl products
mvn spring-boot:run -pl carts
mvn spring-boot:run -pl orders
mvn spring-boot:run -pl gateway

# Run with custom profile
mvn spring-boot:run -pl orders -Dspring-boot.run.profiles=dev
```

### Test Commands

```bash
# Run all tests
mvn test

# Run tests for specific service
cd orders && mvn test

# Run single test class
mvn test -Dtest=OrdersServiceImplTest

# Run single test method
mvn test -Dtest=OrdersServiceImplTest#testCreateOrder
```

### Code Generation & Utilities

```bash
# MyBatis-Plus code generation (if configured)
mvn mybatis-plus-generator:generate

# Check code style
grep -r "TODO\|FIXME" src/

# Find compilation errors
mvn compile
```

## Configuration

### Environment Configuration
- **Nacos Server**: 121.43.197.241:8848
- **Kafka Server**: 122.51.149.223:9092
- **Database**: Configured via Nacos config center
- **Redis**: Configured via Nacos config center

### Service Ports
- Gateway: 8080
- Orders: 8081
- Users: 8082
- Products: 8083
- Carts: 8084

### Profiles
- **dev**: Development environment
- **prod**: Production environment
- Configuration is managed through Nacos config center

## Key Patterns & Conventions

### Package Structure
Each service follows the standard Spring Boot package structure:
```
src/main/java/com/chenzhihao/{service}/
├── {Service}Application.java          # Main application class
├── controller/                        # REST controllers
├── service/                           # Business logic interfaces
├── service/imp/                       # Business logic implementations
├── mapper/                            # MyBatis-Plus mappers
├── domain/po/                         # Persistence objects (entities)
├── domain/dto/                        # Data transfer objects
├── domain/vo/                         # Value objects (API responses)
├── facade/                            # Dubbo service implementations
└── config/                           # Configuration classes
```

### Response Format
All REST APIs return standardized responses using `com.chenzhihao.shopcommon.result.Result<T>`:
```java
Result.success(data)      // Success with data
Result.success()          // Success without data
Result.error("message")   // Error with message
```

### Exception Handling
- Global exception handling in `shop-common` module
- Custom exceptions extend `BaseException`
- Dubbo exception handling via `@DubboException` annotation

### Database Conventions
- MyBatis-Plus for ORM
- Camel case mapping from underscore database columns
- Auto-increment primary keys
- Soft delete patterns where applicable

## Service Communication

### REST Communication
- Services expose REST APIs through controllers
- Gateway routes external requests to appropriate services
- Load balancing via Spring Cloud LoadBalancer

### Dubbo RPC Communication
- Internal service communication via Apache Dubbo
- Service interfaces defined in `shop-api` module
- Nacos as service registry
- Services exposed and consumed via `@DubboService` and `@DubboReference`

### Message Queue (Kafka)
- Order events published to Kafka topics
- Product updates broadcast via Kafka
- Consumer groups for different services
- Manual offset management

## Security

### Authentication
- JWT-based authentication
- Gateway validates tokens via `AuthGlobalFilter`
- User context propagated via Dubbo attachments
- BCrypt password hashing

### Authorization
- Role-based access control (if implemented)
- Service-level authorization checks
- API endpoint protection

## Monitoring & Logging

### Logging Configuration
- SLF4J with Logback
- Service-specific log levels
- Dubbo and Nacos logging configured
- Structured logging for key operations

### Health Checks
- Spring Boot Actuator endpoints
- Service health via Nacos discovery
- Custom health indicators where needed

## Database Schema

The `base.sql` file contains the database schema. Key tables include:
- `user` - User accounts and authentication
- `commodity` - Product catalog
- `orders` - Order management
- `cart` - Shopping cart data

## Common Development Tasks

### Adding a New API Endpoint
1. Create DTO in `domain/dto/`
2. Create VO in `domain/vo/`
3. Add method to service interface
4. Implement in service implementation
5. Add controller method
6. Update API documentation

### Adding a New Dubbo Service
1. Define interface in `shop-api` module
2. Implement service in appropriate module
3. Annotate with `@DubboService`
4. Inject and use via `@DubboReference`

### Database Changes
1. Update `base.sql` schema file
2. Update or create entity in `domain/po/`
3. Create or update mapper interface
4. Update service layer as needed

## Troubleshooting

### Common Issues
- **Service registration failures**: Check Nacos connectivity and configuration
- **Database connection issues**: Verify database credentials in Nacos config
- **Kafka connectivity**: Check Kafka server availability and topic configuration
- **Build failures**: Ensure all modules are properly installed (`mvn clean install`)

### Debug Commands
```bash
# Check service logs
mvn spring-boot:run -pl orders --debug

# Test database connectivity
mysql -h ${cs.db.host} -u ${cs.db.user} -p

# Check Nacos configuration
curl -X GET "http://121.43.197.241:8848/nacos/v1/cs/configs?dataId=orders-service&group=DEFAULT_GROUP"
```

## Dependencies

Key dependencies managed in parent POM:
- Spring Boot 2.7.12
- Spring Cloud Alibaba 2021.0.5.0
- Apache Dubbo 3.3.1
- MyBatis-Plus 3.4.3
- Hutool 5.8.11
- Redisson 3.19.3
- Easy-ES 3.0.0

## Git Workflow

- Main branch: `main`
- Development branches: feature-specific branches
- Pull requests should target `main`
- Commit messages should be descriptive and follow conventional commits