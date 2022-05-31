package com.kqinfo.universal.async.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Zijian Liao
 * @since
 */
@Service
public class TestService2 {
    
    @Autowired
    private TestService1 testService1;


    
}
