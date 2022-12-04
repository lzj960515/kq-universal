package com.kqinfo.universal.workflow.constant;

/**
 * 任务状态枚举
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
public enum TaskStatusEnum {

    /**
     * 提交审核
     */
    SUBMIT(1),
    /**
     * 审核通过
     */
    APPROVED(2),
    /**
     * 驳回
     */
    REJECT(3),
    /**
     * 取消审核
     */
    CANCEL(4);


    private final Integer value;

    TaskStatusEnum(Integer value) {
        this.value = value;
    }

    public Integer value() {
        return this.value;
    }

}
