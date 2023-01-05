package com.kqinfo.universal.yapi.test;

import com.kqinfo.universal.yapi.generator.YapiGenerator;
import org.junit.jupiter.api.Test;

/**
 * @author Zijian Liao
 * @since 2.13.0
 */
public class YapiGeneratorTest {

    private final String token = "cef29ec085e7135dcd57e3e5764596784979b174010e86b7e33a746f2465a29e";


    @Test
    public void testGenerate2() {
        YapiGenerator.generateYapi( "com.kqinfo.universal.yapi.test"
                , "https://yapi.cqkqinfo.com", token, "yapi", true);
    }
}
