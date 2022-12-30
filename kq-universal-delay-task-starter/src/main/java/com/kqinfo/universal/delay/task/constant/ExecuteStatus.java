package com.kqinfo.universal.delay.task.constant;

/**
 * @author Zijian Liao
 * @since  1.0.0
 */
public enum  ExecuteStatus {

    NEW(1),

    EXECUTE(2),

    SUCCESS(3),

    FAIL(4),

    IN_RING(5);

    private final Integer status;

    ExecuteStatus(Integer status){
        this.status = status;
    }

    public Integer status() {
        return status;
    }
}
