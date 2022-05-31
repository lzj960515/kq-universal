package com.kqinfo.universal.redis.test;

import com.kqinfo.universal.redis.exception.RedisLockAcquireTimeoutException;
import com.kqinfo.universal.redis.exception.RedisLockException;
import com.kqinfo.universal.redis.exception.RedisLockFailFastException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Zijian Liao
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RedisLockException.class)
    public ResponseEntity<?> handlerException(HttpServletRequest request, RedisLockException ex) {
        if(ex instanceof RedisLockFailFastException){
            System.out.println("获取锁失败-快速失败策略");
        }
        if(ex instanceof RedisLockAcquireTimeoutException){
            System.out.println("获取锁失败-超时策略");
        }
        Map<String, Object> error = new HashMap<>(2, 1);
        error.put("code", -1);
        error.put("msg", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.OK);
    }
}
