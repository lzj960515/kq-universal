package com.kqinfo.universal.test.test;

import com.kqinfo.universal.test.util.MockUtil;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

/**
 * @author Zijian Liao
 * @since 2.0.0
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping
    public User get(){
        Hobby hobby = new Hobby();
        hobby.setName("xx");
        User user = new User();
        user.setName("zhangsan");
        user.setAge(18);
        user.setHobbies(Collections.singletonList(new Hobby("xx")));
        user.setOrder(new Order("xx"));
        return user;
    }

    @PostMapping
    public User post(@RequestBody User param){
        Hobby hobby = new Hobby();
        hobby.setName("xx");
        User user = new User();
        user.setName("zhangsan");
        user.setAge(18);
        user.setHobbies(Collections.singletonList(new Hobby("xx")));
        user.setOrder(new Order("xx"));
        return user;
    }

    @PutMapping
    public User put(@RequestBody User param){
        Hobby hobby = new Hobby();
        hobby.setName("xx");
        User user = new User();
        user.setName("zhangsan");
        user.setAge(18);
        user.setHobbies(Collections.singletonList(new Hobby("xx")));
        user.setOrder(new Order("xx"));
        return user;
    }
    @DeleteMapping
    public User delete(){
        Hobby hobby = new Hobby();
        hobby.setName("xx");
        User user = new User();
        user.setName("zhangsan");
        user.setAge(18);
        user.setHobbies(Collections.singletonList(new Hobby("xx")));
        user.setOrder(new Order("xx"));
        return user;
    }
}
