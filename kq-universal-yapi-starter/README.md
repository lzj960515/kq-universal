# Yapi组件
## 功能
1. 自定义Yapi注解，支持手写mock
1. 生成Yapi接口，智能mock
## 使用
1、引入依赖

```xml
<dependency>
    <groupId>com.github.lzj960515</groupId>
    <artifactId>kq-universal-yapi-starter</artifactId>
    <version>?</version>
</dependency>
```
> 版本参照父级版本
2、使用

与openApi类型，组件中一共有以下注解

```java
@YapiApi  controller注解，加在controller类上
@YapiOperation  method注解，加在controller接口上
@YapiParameter	参数注解，加在类的属性上，或者是接口的参数上
@YapiParameterObject 参数注解，加在接口的参数上，用来标识这个参数是个对象
```

具体例子

```java
@Data
public class User {

    @YapiParameter(value = "姓名")
    private String name;

    @YapiParameter(value = "年龄", mock = "@int(1,100)")
    private Integer age;
}
```

> YapiParameter支持手写mock，如果不写的话，则将智能mock

```java
@YapiApi("用户管理")
@RequestMapping("/user")
public class UserController {

    @YapiOperation("创建用户")
    @PostMapping("/create")
    public BaseResult<User> create(@RequestBody @Validated User user){
        return BaseResult.success(user);
    }

    @YapiOperation("分页查询用户列表")
    @GetMapping("/page")
    public BaseResult<List<User>> page(
            @YapiParameter("分页页码") Integer current,
            @YapiParameter("分页大小") Integer size){
        return BaseResult.success(new ArrayList<>());
    }

    @YapiOperation("删除用户")
    @DeleteMapping("/delete/{id}")
    public BaseResult<Void> delete(@PathVariable Long id){
        return BaseResult.success();
    }

    @YapiOperation("根据用户名查询用户")
    @GetMapping("/get-by-name")
    public BaseResult<User> getByName(@YapiParameterObject User user){
        return BaseResult.success(new User());
    }

}
```

3、生成yapi接口

使用`YapiGenerator.generateYapi`即可生成yapi接口

```java
YapiGenerator.generateYapi("com.kqinfo.universal.yapi.controller"
                , "https://yapi.cqkqinfo.com", token, "yapi", true);
```

> 从左往右参数分别为：扫描包路径，yapi地址，项目token，生成模式(目前写死为yapi), 是否覆盖原接口

运行完毕后，进入yapi即可查看到生成的所有接口




