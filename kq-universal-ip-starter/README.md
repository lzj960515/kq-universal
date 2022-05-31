# ip信息组件

| 功能                     | 完成 |
| ------------------------ | ---- |
| 获取客户端ip | ✔    |
| 获取客户端地理信息       | ✔    |
| 获取客户端浏览器      | ✔    |

## 使用方式

### 1.引入依赖

```xml
<dependency>
  <groupId>com.github.lzj960515</groupId>
  <artifactId>kq-universal-ip-starter</artifactId>
  <version>1.13.0</version>
</dependency>
```

### 2.使用
```java
    // 获取ip
    IpUtil.getIpAddress();
    // 获取地理信息
    IpGeoUtil.getGeo();
    // 获取浏览器信息
    BrowserUtil.getBrowser();
```