# 测试组件
## 功能
1. 基础抽象类
2. mock工具
3. api对比工具，用于和yapi接口的数据结构进行对比
## 使用
1. 引入依赖
```xml
<dependency>
    <groupId>com.github.lzj960515</groupId>
    <artifactId>kq-universal-test-starter</artifactId>
    <version>?</version>
</dependency>
```
> 版本参照父级版本
2. 使用
```java
public class TestControllerTest extends BaseRestTest {


    @Test
    public void testGet(){
        JSONObject result = super.get("/test", null, null);
        super.checkGetApi("http://localhost:8090/test", result);
    }

    @Test
    public void testPost(){
        User user = new User("xx", 1, null, null);
        JSONObject result = super.post("/test", null, user);
        super.checkPostApi("http://localhost:8090/test", result, user);
    }

    @Test
    public void testPut(){
        User user = new User("xx", 1, null, null);
        JSONObject result = super.put("/test", null, user);
        super.checkPutApi("http://localhost:8090/test", result, user);
    }

    @Test
    public void testDelete(){
        JSONObject result = super.delete("/test", null, null);
        super.checkDeleteApi("http://localhost:8090/test", result);
    }
}
```
> 如果无须使用mockMvc，那么可以替换父类为BaseTest

> 注意：案例中的@Test 为junit5: org.junit.jupiter.api.Test, 推荐使用该注解进行测试

## Mock
使用MockUtil可以直接生成一个mock类
```java
User mock = MockUtil.mock(User.class);
```
