package com.kqinfo.universal.yapi.test;

import com.kqinfo.universal.yapi.annotation.YapiApi;
import com.kqinfo.universal.yapi.annotation.YapiOperation;
import com.kqinfo.universal.yapi.annotation.YapiParameter;
import com.kqinfo.universal.yapi.annotation.YapiParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zijian Liao
 * @since 2.13.0
 */
@YapiApi("用户管理")
@RequestMapping("/user")
public class UserController {

    @YapiOperation("创建用户")
    @PostMapping("/create")
    public BaseResult<User> create(@RequestBody @Validated User user){
        return BaseResult.success(user);
    }

    @YapiOperation("批量添加用户")
    @PostMapping("/batch-create")
    public BaseResult<List<User>> batchCreate(@RequestBody @Validated List<User> user){
        return BaseResult.success(user);
    }


    @YapiOperation("更新用户")
    @PutMapping("/update")
    public BaseResult<User> update(@RequestBody User user){
        return BaseResult.success(user);
    }

    @YapiOperation("分页查询用户列表")
    @GetMapping("/page")
    public BaseResult<List<User>> page(
            @YapiParameter(value = "分页页码", required = true) Integer current,
            @YapiParameter(value = "分页大小", required = true) Integer size){
        return BaseResult.success(new ArrayList<>());
    }

    @YapiOperation("获取用户信息")
    @GetMapping("/get/{id}")
    public BaseResult<User> get(@PathVariable Long id){
        return BaseResult.success(new User());
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


    @YapiOperation("批量删除")
    @DeleteMapping("/batch")
    public BaseResult<Void> batchDelete(@RequestBody List<Long> ids){
        return BaseResult.success();
    }

}
