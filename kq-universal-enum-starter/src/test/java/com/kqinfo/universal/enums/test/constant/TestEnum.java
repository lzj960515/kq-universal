package com.kqinfo.universal.enums.test.constant;

import com.kqinfo.universal.enums.annotation.KqEnum;

/**
 * @author Zijian Liao
 * @since 2.4.0
 */
@KqEnum("test")
public enum TestEnum {
    TEST("test1","desc1"),
    TEST2("test2","desc2");

    private final String code;
    private final String desc;


    TestEnum(String code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}

