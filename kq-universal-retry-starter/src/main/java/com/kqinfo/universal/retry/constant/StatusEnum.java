package com.kqinfo.universal.retry.constant;

/**
 * @author Zijian Liao
 * @since 1.12.0
 */
public enum StatusEnum {

    /**
     * 执行中
     */
    EXECUTING(1),
    /**
     * 成功
     */
    SUCCESS(2),
    /**
     * 失败
     */
    FAIL(3);

    private final Integer status;

    StatusEnum(Integer status){
        this.status = status;
    }

    public Integer status(){
        return this.status;
    }

}
