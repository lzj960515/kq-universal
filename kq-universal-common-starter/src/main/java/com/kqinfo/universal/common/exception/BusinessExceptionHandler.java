package com.kqinfo.universal.common.exception;

import com.kqinfo.universal.common.response.BaseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * @author Zijian Liao
 */
@Slf4j
@Order
@ConditionalOnClass(HttpServletRequest.class)
@RestControllerAdvice
public class BusinessExceptionHandler {

    @Value("${spring.profiles.active:local}")
    private String env;

    @ExceptionHandler(BusinessException.class)
    public BaseResult<Void> handlerException(BusinessException ex) {
        log.warn("[全局业务异常] 业务编码：{} 异常记录：{}", ex.getCode(), ex.getMessage(), ex);
        return BaseResult.failure(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResult<Void> handlerException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        String message = bindingResult.getFieldErrors().stream().map(field -> field.getField() +  field.getDefaultMessage()).distinct()
                .collect(Collectors.joining(","));
        return BaseResult.failure(HttpStatus.BAD_REQUEST.value(), message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public BaseResult<Void> handlerException(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations()
                .stream().map(ConstraintViolation::getMessage).distinct().collect(Collectors.joining(","));
        return BaseResult.failure(HttpStatus.BAD_REQUEST.value(), message);
    }

    @ExceptionHandler(BindException.class)
    public BaseResult<Void> handlerException(BindException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        String message = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                .distinct().collect(Collectors.joining(","));
        return BaseResult.failure(HttpStatus.BAD_REQUEST.value(), message);
    }

    @ExceptionHandler(Exception.class)
    public BaseResult<Void> handlerException(Exception ex) {
        log.error("运行时异常：", ex);
        if("online".equals(env)){
            return BaseResult.failure();
        }
        return BaseResult.failure(ex.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public BaseResult<Void> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException e) {
        return BaseResult.failure(e.getMessage());
    }

}