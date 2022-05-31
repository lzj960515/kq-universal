package com.kqinfo.universal.enums.test;

import com.kqinfo.universal.enums.annotation.KqEnumScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Zijian Liao
 * @since 2.5.0
 */
@KqEnumScan(basePackages = "com.kqinfo.universal.enums.test.constant")
@SpringBootApplication
public class AppApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }
}
