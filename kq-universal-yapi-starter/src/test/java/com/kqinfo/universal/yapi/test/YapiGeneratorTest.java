package com.kqinfo.universal.yapi.test;

import com.kqinfo.universal.yapi.generator.YapiGenerator;
import org.junit.jupiter.api.Test;

/**
 * @author Zijian Liao
 * @since 2.13.0
 */
public class YapiGeneratorTest {

    private final String token = "c15e8367ee45714662d72826a09019faed3dd121ebe8f2772dcdf3ee4212140c";


    @Test
    public void testGenerate2() {
        YapiGenerator.generateYapi( "com.kqinfo.universal.yapi.test"
                , "https://yapi.cqkqinfo.com", token, "yapi", true);
    }
}
