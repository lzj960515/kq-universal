package com.kqinfo.universal.common.util;

import java.util.regex.Pattern;

/**
 * 密码工具
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
public final class PasswordUtil {

    private PasswordUtil(){}

    private static final Pattern NUMBER_PATTERN = Pattern.compile(".*\\d.*");
    private static final Pattern LETTER_PATTERN = Pattern.compile(".*[A-Za-z].*");
    private static final Pattern SYMBOL_PATTERN = Pattern.compile(".*\\W.*");

    /**
     * 校验密码强度，返回密码安全等级（含有数字、字母、标点符号）
     * @param password 密码
     * @return 安全等级
     */
    public static int checkPasswordSecurity(String password){
        int level = 0;
        if (NUMBER_PATTERN.matcher(password).matches()) {
            level ++ ;
        }
        if(LETTER_PATTERN.matcher(password).matches()){
            level ++ ;
        }
        if(SYMBOL_PATTERN.matcher(password).matches()){
            level ++ ;
        }
        return level;
    }
}
