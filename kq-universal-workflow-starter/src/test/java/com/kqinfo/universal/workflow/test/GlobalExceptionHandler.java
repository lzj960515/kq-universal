package com.kqinfo.universal.workflow.test;

import com.kqinfo.universal.workflow.exception.WorkflowException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zijian Liao
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WorkflowException.class)
    public ResponseEntity<?> handlerException(WorkflowException ex) {
        Map<String, Object> error = new HashMap<>(2, 1);
        error.put("code", -1);
        error.put("msg", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.OK);
    }
}
