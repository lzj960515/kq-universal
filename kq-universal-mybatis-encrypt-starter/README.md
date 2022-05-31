# mybatis字段加解密组件
## 功能

- 在字段上添加注解即可在数据入库时加密，出库时解密

## 使用方式

1. 引入依赖
```xml
<dependency>
 <groupId>com.github.lzj960515</groupId>
 <artifactId>kq-universal-mybatis-encrypt-starter</artifactId>
 <version>${revision}</version>
</dependency>
```

2. 编写配置，设置加解密密钥
```yaml
kq:
  encrypt:
    secret: qwer1234asdf5678
```
> 密钥必须是16位

3. 字段上添加注解
```java
public class User {
	@FieldEncrypt
	private String phone;
}
```
接下来只需按平时方式操作数据库就行了。