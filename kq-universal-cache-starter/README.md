# 缓存组件

## 概述

我们常常遇到这样的需求：从某个地方获取数据，将数据放入缓存，下次从缓存中获取

写成代码大概是这样

```java
// 先尝试从缓存获取
Object value = CacheService.get(key);
if(value != null){
  return value;
}
// 获取数据
value = getData();
CacheService.set(key, value);
return value;
```

显然，以上代码除了`getData()`是特别的，其他代码都是通用的模板，为此，我们可以将这一套逻辑封装成组件。

## 使用方式
1. 引入依赖
```xml
<dependency>
    <groupId>com.github.lzj960515</groupId>
    <artifactId>kq-universal-cache-starter</artifactId>
    <version>1.1.0</version>
</dependency>
```
2. 使用

缓存组件有两种方式:

- 只有一级缓存的普通方式，如获取微信的token情况
- 具备二级缓存的方式，对标前置机获取号源情况，保证缓存一直存在

**该两种方式在【未获取到锁且缓存中无数据】时都会返回null, 请使用者自行根据null的情况处理**

### 只有一级缓存的普通方式

```java
getCache(String key, Class<T> type, Supplier<T> supplier);

getCache(String key, long lockTime, long cacheTime, Class<T> type, Supplier<T> supplier)
```

> key: 缓存的key值
>
> type: 得到缓存返回的对象类型
>
> supplier: lambda表达式，业务中获取数据的逻辑，如上面的`getData`
>
> lockTime: 加锁时间，默认30s
>
> cacheTime: 缓存时间，默认3分钟

示例：

```java
@Resource
private KqCacheManager kqCacheManager;

JSONObject data = kqCacheManager.getCache("test", JSONObject.class, this::getData);
```

### 具备二级缓存的方式

```java
getCacheHasSecond(String key, Class<T> type, Supplier<T> supplier);

getCacheHasSecond(String key, long lockTime, long cacheTime, long secondCacheTime, Class<T> type, Supplier<T> supplier);
```

> key: 缓存的key值
>
> type: 得到缓存返回的对象类型
>
> supplier: lambda表达式，业务中获取数据的逻辑，如上面的`getData`
>
> lockTime: 加锁时间，默认30s
>
> cacheTime: 一级缓存时间，默认3分钟
>
> secondCacheTime: 二级缓存时间，默认1天

示例：

```java
JSONObject data = kqCacheManager.getCacheHasSecond("test", JSONObject.class, this::getData);
```

### 二级缓存的异步方式

```java
asyncGetCacheHasSecond(String key, Class<T> type, Supplier<T> supplier);

asyncGetCacheHasSecond(String key, long lockTime, long cacheTime, long secondCacheTime, Class<T> type, Supplier<T> supplier);
```

示例：

```java
JSONObject data = kqCacheManager.asyncGetCacheHasSecond("test", JSONObject.class, this::getData);
```

### 获取List结构缓存


```java
getListCache

getListCacheHasSecond

asyncGetListCacheHasSecond
```



