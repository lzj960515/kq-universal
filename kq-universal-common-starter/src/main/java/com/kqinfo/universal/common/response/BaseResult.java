package com.kqinfo.universal.common.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

/**
 * API 统一返回状态码
 *
 * @author Zijian Liao
 * @since v1.0.0
 */
@Data
public class BaseResult<T> implements Serializable {

    private static final long serialVersionUID = -5397818122651899167L;
    private static final BaseResult<Void> SUCCESS_RESULT = new BaseResult<>(BaseResultCode.SUCCESS);
    private static final BaseResult<Void> FAILURE_RESULT = new BaseResult<>(BaseResultCode.FAILURE);

    /**
     * 状态码
     */
    private int code;

    /**
     * 消息
     */
    private String message;
    private String msg;

    /**
     * 需要返回的数据对象
     */
    private T data;

    public BaseResult() {
        super();
    }

    /**
     * 一般的响应结果，不返回数据
     *
     * @param code {@link ResultCode}
     */
    public BaseResult(ResultCode code) {
        super();
        this.code = code.code();
        this.message = code.message();
        this.msg = code.message();
    }

    /**
     * 一般的响应结果，不返回数据，自定义消息
     *
     * @param code    {@link ResultCode}
     * @param message {@code String} 自定义消息
     */
    public BaseResult(ResultCode code, String message) {
        super();
        this.code = code.code();
        this.message = message;
        this.msg = message;
    }

    /**
     * 一般的响应结果，不返回数据，自定义消息
     *
     * @param code    {@link ResultCode}
     * @param message {@code String} 自定义消息
     */
    public BaseResult(int code, String message) {
        super();
        this.code = code;
        this.message = message;
        this.msg = message;
    }

    /**
     * 一般的响应结果，包含数据
     *
     * @param code {@link ResultCode}
     * @param data {@code Object}
     */
    public BaseResult(ResultCode code, T data) {
        super();
        this.code = code.code();
        this.message = code.message();
        this.msg = code.message();
        this.data = data;
    }

    /**
     * 一般的响应结果，自定义消息并包含数据
     *
     * @param code    {@link ResultCode}
     * @param message {@link String}
     * @param data    {@code Object}
     */
    public BaseResult(ResultCode code, String message, T data) {
        super();
        this.code = code.code();
        this.message = message;
        this.msg = message;
        this.data = data;
    }

    /**
     * 请求成功
     *
     * @return this
     */
    public static BaseResult<Void> success() {
        return SUCCESS_RESULT;
    }

    /**
     * 请求成功
     *
     * @param message {@code String} 自定义消息
     * @return this
     */
    public static BaseResult<Void> success(String message) {
        return new BaseResult<>(BaseResultCode.SUCCESS, message);
    }

    /**
     * 请求成功，包含数据部分
     *
     * @param data {@code Object}
     * @return this
     */
    public static <T> BaseResult<T> success(T data) {
        return new BaseResult<>(BaseResultCode.SUCCESS, data);
    }

    /**
     * 请求成功，自定义状态码，包含数据
     *
     * @param code {@link ResultCode} 自定义状态码
     * @param data {@code Object}
     * @return this
     */
    public static <T> BaseResult<T> success(ResultCode code, T data) {
        return new BaseResult<>(code, data);
    }

    /**
     * 请求成功，自定义消息并包含数据
     *
     * @param message {@link String} 自定义消息
     * @param data    {@code Object}
     * @return this
     */
    public static <T> BaseResult<T> success(String message, T data) {
        return new BaseResult<>(BaseResultCode.SUCCESS, message, data);
    }

    /**
     * 请求失败
     *
     * @return this
     */
    public static BaseResult<Void> failure() {
        return FAILURE_RESULT;
    }

    /**
     * 请求失败，自定义消息
     *
     * @param message {@code String} 消息
     * @return this
     */
    public static BaseResult<Void> failure(String message) {
        return new BaseResult<>(BaseResultCode.FAILURE, message);
    }

    /**
     * 请求失败，自定义消息
     *
     * @param message {@code String} 消息
     * @return this
     */
    public static BaseResult<Void> failure(int code, String message) {
        return new BaseResult<>(code, message);
    }

    /**
     * 请求失败，自定义状态码
     *
     * @param code {@link ResultCode} 自定义状态码
     * @return this
     */
    public static BaseResult<Void> failure(ResultCode code) {
        return new BaseResult<>(code);
    }

    /**
     * 请求失败，自定义状态码，包含数据
     *
     * @param code {@link ResultCode} 自定义状态码
     * @param data {@code Object}
     * @return this
     */
    public static <T> BaseResult<T> failure(ResultCode code, T data) {
        return new BaseResult<>(code, data);
    }

    @JsonIgnore
    public boolean isSuccess() {
        return this.code == BaseResultCode.SUCCESS.code();
    }

    @JsonIgnore
    public boolean isFailure() {
        return this.code != BaseResultCode.SUCCESS.code();
    }


}