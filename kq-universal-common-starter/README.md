# 通用组件
## 功能

- [统一异常处理](src/main/java/com/kqinfo/universal/common/exception)
- [统一返回基类](src/main/java/com/kqinfo/universal/common/response)
- [Swagger](src/main/java/com/kqinfo/universal/common/config/SwaggerConfiguration.java)
- [工具](src/main/java/com/kqinfo/universal/common/util)


## 使用方式

引入依赖
```xml
<dependency>
 <groupId>com.github.lzj960515</groupId>
 <artifactId>kq-universal-common-starter</artifactId>
 <version>${revision}</version>
</dependency>
```
配置swagger
```yaml
kq:
  swagger:
    base-package: com.cqkqinfo.universal
    title: common
    description: common
```