# mybatis-plus组件
功能
- 整合mybatis-plus需要的依赖
- 引入flyway
- 配置好使用mybatis-plus时需要的功能，如分页，自动填充

## 使用
引入依赖
```xml
       <dependency>
            <groupId>com.github.lzj960515</groupId>
            <artifactId>kq-universal-mybatis-plus-starter</artifactId>
            <version>?</version>
       </dependency>
```
关于自动填充功能

如创建时间和修改时间字段常常发生变动，那么只需在字段上加上注解
```java
	/**
	 * 创建时间
	 */
	@TableField(value = "create_time", fill = FieldFill.INSERT)
	private LocalDateTime createTime;

	/**
	 * 修改时间
	 */
	@TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime updateTime;
```
> 以上配置表示createTime在insert时自动填充，updateTime在insert&update时自动填充
>
> 当前只支持自动填充命名为createTime和updateTime的字段，如果有自定义字段，可参考[MyMetaObjectHandler](src/main/java/com/kqinfo/universal/mybatisplus/repository/MyMetaObjectHandler.java)进行重写