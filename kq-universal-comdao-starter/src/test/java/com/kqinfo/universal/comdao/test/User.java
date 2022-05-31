package com.kqinfo.universal.comdao.test;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Data
@TableName("user")
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class User {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String name;

    private Integer age;

    private LocalDateTime createTime;
}
