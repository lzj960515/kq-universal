package com.kqinfo.universal.workflow.constant;

/**
 * 流程状态
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
public enum StatusEnum {

	/**
	 * 审核中
	 */
	START(1),
	/**
	 * 审核通过
	 */
	END(2),
	/**
	 * 驳回
	 */
	REJECT(3),
	/**
	 * 取消审核
	 */
	CANCEL(4);

	private final Integer value;

	StatusEnum(Integer value) {
		this.value = value;
	}

	public Integer value() {
		return this.value;
	}

}
