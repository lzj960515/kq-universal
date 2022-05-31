package com.kqinfo.universal.enums.test.constant;

import com.kqinfo.universal.enums.annotation.KqCode;
import com.kqinfo.universal.enums.annotation.KqDesc;
import com.kqinfo.universal.enums.annotation.KqEnum;

/**
 * @author Zijian Liao
 * @since 2.5.0
 */
@KqEnum("order")
public enum OrderEnum {

    PAY(1, "支付订单"),
    REFUND(2,"退款订单");

    @KqCode
    private final Integer type;
    @KqDesc
    private final String value;


    OrderEnum(Integer type, String value){
        this.type = type;
        this.value = value;
    }

    public Integer getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
