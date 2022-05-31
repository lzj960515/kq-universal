package com.kqinfo.universal.common.test;

import com.kqinfo.universal.common.util.PasswordUtil;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
public class PasswordUtilTest {

    @Test
    public void securityTest(){
        {
            String password  = "123456";
            final int level = PasswordUtil.checkPasswordSecurity(password);
            assertThat(level, CoreMatchers.is(1));
        }

        {
            String password  = "123456a";
            final int level = PasswordUtil.checkPasswordSecurity(password);
            assertThat(level, CoreMatchers.is(2));
        }

        {
            String password  = "123456a.";
            final int level = PasswordUtil.checkPasswordSecurity(password);
            assertThat(level, CoreMatchers.is(3));
        }
    }
}
