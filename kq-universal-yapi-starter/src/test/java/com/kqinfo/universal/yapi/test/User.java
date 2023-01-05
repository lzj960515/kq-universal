package com.kqinfo.universal.yapi.test;

import com.alibaba.fastjson.JSONArray;
import com.kqinfo.universal.yapi.annotation.YapiParameter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
* 用户
*
* @author Zijian Liao
* @since 1.0.0
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class User extends BaseDomain {

    private static final long serialVersionUID = 1L;

    @YapiParameter(value = "姓名")
    private String name;

    @YapiParameter(value = "年龄", mock = "@int(1,100)")
    private Integer age;

    @YapiParameter(value = "地址")
    private String address;

    @YapiParameter(value = "兴趣列表")
    private List<Hobby> hobbyList;

    @YapiParameter(value = "array")
    private JSONArray jsonArray;

    @YapiParameter(value = "用户id")
    private List<String> userIds;
}