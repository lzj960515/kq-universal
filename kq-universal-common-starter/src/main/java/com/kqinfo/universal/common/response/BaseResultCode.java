package com.kqinfo.universal.common.response;

/**
 * API 统一返回状态码
 *
 * @author Zijian Liao
 * @since v1.0.0
 */
public enum BaseResultCode implements ResultCode {

	/**
	 * 请求成功
	 */
	SUCCESS(0, "成功"),
	/**
	 * 请求失败
	 */
	FAILURE(-1, "失败"),

	// ------------------------------------------------------- 参数错误：10001-19999 Start

	/**
	 * 参数无效
	 */
	PARAM_IS_INVALID(10001, "参数无效"),
	/**
	 * 参数为空
	 */
	PARAM_IS_BLANK(10002, "参数为空"),
	/**
	 * 参数类型错误
	 */
	PARAM_TYPE_BIND_ERROR(10003, "参数类型错误"),
	/**
	 * 参数缺失
	 */
	PARAM_NOT_COMPLETE(10004, "参数缺失"),

	// ------------------------------------------------------- 参数错误：10001-19999 End

	// ------------------------------------------------------- 用户错误：20001-29999 Start

	/**
	 * 用户未登录
	 */
	USER_NOT_LOGGED_IN(20001, "用户未登录"),
	/**
	 * 账号不存在或密码错误
	 */
	USER_LOGIN_ERROR(20002, "账号不存在或密码错误"),
	/**
	 * 账号已被禁用
	 */
	USER_ACCOUNT_FORBIDDEN(20003, "账号已被禁用"),
	/**
	 * 用户不存在
	 */
	USER_NOT_EXIST(20004, "用户不存在"),
	/**
	 * 用户已存在
	 */
	USER_HAS_EXISTED(20005, "用户已存在"),
	/**
	 * 昵称已存在
	 */
	USER_NICK_HAS_EXISTED(20006, "昵称已存在"),
	/**
	 * 邮箱已存在
	 */
	USER_EMAIL_HAS_EXISTED(20008, "邮箱已存在"),

	// ------------------------------------------------------- 用户错误：20001-29999 End

	// ------------------------------------------------------- 业务错误：30001-39999 Start

	/**
	 * 业务系统中用户不存在
	 */
	SPECIFIED_QUESTIONED_USER_NOT_EXIST(30001, "业务系统中用户不存在"),
	/**
	 * 密码错误
	 */
	PASSWORD_WRONG(30002, "密码错误"),

	// ------------------------------------------------------- 业务错误：30001-39999 End

	// ------------------------------------------------------- 系统错误：40001-49999 Start

	/**
	 * 系统繁忙，请稍后重试
	 */
	SYSTEM_INNER_ERROR(40001, "系统繁忙，请稍后重试"),

	// ------------------------------------------------------- 系统错误：40001-49999 End

	// ------------------------------------------------------- 数据错误：50001-599999 Start

	/**
	 * 数据未找到
	 */
	RESULT_DATA_NONE(50001, "数据未找到"),
	/**
	 * 数据有误
	 */
	DATA_IS_WRONG(50002, "数据有误"),
	/**
	 * 数据已存在
	 */
	DATA_ALREADY_EXISTED(50003, "数据已存在"),

	// ------------------------------------------------------- 数据错误：50001-599999 End

	// ------------------------------------------------------- 接口错误：60001-69999 Start

	/**
	 * 内部系统接口调用异常
	 */
	INTERFACE_INNER_INVOKE_ERROR(60001, "内部系统接口调用异常"),
	/**
	 * 外部系统接口调用异常
	 */
	INTERFACE_OUTER_INVOKE_ERROR(60002, "外部系统接口调用异常"),
	/**
	 * 该接口禁止访问
	 */
	INTERFACE_FORBID_VISIT(60003, "该接口禁止访问"),
	/**
	 * 接口地址无效
	 */
	INTERFACE_ADDRESS_INVALID(60004, "接口地址无效"),
	/**
	 * 接口请求超时
	 */
	INTERFACE_REQUEST_TIMEOUT(60005, "接口请求超时"),
	/**
	 * 接口负载过高
	 */
	INTERFACE_EXCEED_LOAD(60006, "接口负载过高"),

	// ------------------------------------------------------- 接口错误：60001-69999 End

	// ------------------------------------------------------- 权限错误：70001-79999 Start

	/**
	 * 无访问权限
	 */
	PERMISSION_NO_ACCESS(403, "无访问权限"),
	/**
	 * TOKEN 已失效
	 */
	TOKEN_EXPIRATION(401, "TOKEN 已失效");

	// ------------------------------------------------------- 权限错误：70001-79999 End

	private final Integer code;

	private final String message;

	BaseResultCode(Integer code, String message) {
		this.code = code;
		this.message = message;
	}

	@Override
	public Integer code() {
		return this.code;
	}

	@Override
	public String message() {
		return this.message;
	}

	public static String getMessage(String name) {
		for (BaseResultCode item : BaseResultCode.values()) {
			if (item.name().equals(name)) {
				return item.message;
			}
		}
		return name;
	}

	public static Integer getCode(String name) {
		for (BaseResultCode item : BaseResultCode.values()) {
			if (item.name().equals(name)) {
				return item.code;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return this.name();
	}

}