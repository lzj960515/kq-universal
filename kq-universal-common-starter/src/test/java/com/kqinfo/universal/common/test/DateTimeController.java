package com.kqinfo.universal.common.test;

import com.kqinfo.universal.common.response.BaseResult;
import com.kqinfo.universal.common.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@RestController
@RequestMapping("/user")
public class DateTimeController {

    @PostMapping
    public BaseResult<Void> post(@RequestBody User user){
        Assert.notNull(user.getBirthday(), "");
        Assert.notNull(user.getCreateTime(), "");
        return BaseResult.success();
    }

    @GetMapping
    public BaseResult<Void> get(LocalDateTime localDateTime, LocalDate localDate){
        Assert.notNull(localDateTime, "");
        Assert.notNull(localDate, "");
        return BaseResult.success();
    }
}
