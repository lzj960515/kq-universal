# 枚举组件

## 介绍

业务场景：项目中总有各种各样的枚举，这些枚举呈前后端一一对应关系，当某个枚举值发生变更时，前后端需要同时作出改变，为了解决这样的问题，我们可以作出这样的方式：后端提高枚举接口，返回枚举字典，前端依赖枚举字典渲染数据。这样，枚举发生改变时，需要变更的只有后端代码。

而生成枚举接口的方式是与业务无关的，于是该枚举组件诞生了。

| 功能                                     | 完成 |
| ---------------------------------------- | ---- |
| 扫描指定包下的枚举类，生成枚举接口       | ✔    |
| 枚举工具——通过字段A获取枚举中对应的字段B | ✔    |

## 使用方式

### 1.引入依赖

```xml
<dependency>
  <groupId>com.github.lzj960515</groupId>
  <artifactId>kq-universal-enum-starter</artifactId>
  <version>version</version>
</dependency>
```

### 2.使用

首先定义一个枚举类用于测试

```java
package com.kqinfo.universal.enums.test.constant;

import com.kqinfo.universal.enums.annotation.KqEnum;

public enum TestEnum {
    TEST("test1","desc1"),
    TEST2("test2","desc2");

    private final String code;
    private final String desc;

    TestEnum(String code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
```

#### 2.1 扫描指定包下的枚举类

1、在枚举类上增加`@KqEnum`注解，用于标识该类可以生成枚举字典

```java
@KqEnum("test")
public enum TestEnum {
}
```

> 值`test`为字典名称

2、在启动类上增加扫描注解`@KqEnumScan`

```java
@KqEnumScan(basePackages = "com.kqinfo.universal.enums.test.constant")
@SpringBootApplication
public class AppApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }
}
```

3、使用`KqEnumHandler`获取字典数据

```java
@Test
public void testGetAll(){
  final Map<String, Map<Object, Object>> all = KqEnumHandler.getAll();
  Assertions.assertEquals(2, all.size());
}
```

map转json后样式

```json
{"test":{"test2":"desc2","test1":"desc1"}}
```

> 数据以枚举类中的code字段为key，desc字段为value

也由于如此，那么当枚举类中没有code和desc字段时，我们就需要告诉组件哪个字段为key，哪个字段为value

用法如下：

```java
@KqEnum("order")
public enum OrderEnum {

    PAY(1, "支付订单"),
    REFUND(2,"退款订单");

    @KqCode
    private final Integer type;
    @KqDesc
    private final String value;


    OrderEnum(Integer type, String value){
        this.type = type;
        this.value = value;
    }

    public Integer getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
```

> `@KqCode`注解标识的字段为key，`@KqDesc`标识的字段为value

使用`KqEnumHandler.getAll()`获取字典

```json
{"test":{"test2":"desc2","test1":"desc1"},"order":{1:"支付订单",2:"退款订单"}}
```

当然，如果只想要获取其中某个枚举中的字典，可使用`KqEnumHandler.get(enumName)`方法

```java
Map<Object, Object> orderDict = KqEnumHandler.get("order");
```

#### 2.2 枚举工具

在某些场景下，我们还会遇到通过枚举中的key获取对应value的情况，比如导出excel，总不能导出key的数据给用户看吧

那么此时`KqEnumUtil`就派上用场了

KqEnumUtil提供三个方法

1、getField1ByField2: 使用枚举类中filed1字段的值获取对呀field2字段的值

2、getDescByCode：使用枚举类中code字段的值获取对应desc字段的值

3、getCodeByDesc：使用枚举类中desc字段获取对应code字段的值

使用方式如下：

```java
@Test
public void testGetCodeByDesc(){
  final Object code = KqEnumUtil.getCodeByDesc(TestEnum.class, "desc1");
  Assertions.assertEquals("test1", code);
}

@Test
public void testGetDescByCode(){
  final Object code = KqEnumUtil.getDescByCode(TestEnum.class, "test2");
  Assertions.assertEquals("desc2", code);
}

@Test
public void testGetField1ByField2(){
  final Object value = KqEnumUtil.getField1ByField2(OrderEnum.class, "value", "type", 2);
  Assertions.assertEquals("退款订单", value);
}
```