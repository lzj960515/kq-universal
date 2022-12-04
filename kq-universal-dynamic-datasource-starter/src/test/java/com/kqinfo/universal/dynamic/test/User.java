package com.kqinfo.universal.dynamic.test;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private Integer age;

    private String email;
}
