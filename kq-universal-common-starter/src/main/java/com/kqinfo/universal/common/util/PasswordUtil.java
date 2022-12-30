package com.kqinfo.universal.common.util;

import cn.hutool.core.util.RandomUtil;
import com.kqinfo.universal.common.exception.BusinessException;

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
    private static final String CONTINUITY_LETTER = "1234567890-=!@#$%^&()_+1qaz2wsx3edc4rfv5tgb6yhn7ujm8ik,9ol.0p;/qwertyuiop[]asdfghjkl;'zxcvbnm,./!QAZ@WSX#EDC$RFV%TGB^YHN&UJM*IK<(OL>)P:?";

    /**
     * 校验密码强度，返回密码安全等级（含有数字、字母、标点符号）
     * @param password 密码
     * @return 安全等级
     */
    public static int checkPasswordSecurity(String password){
        if(password == null || password.length() < 8){
            return 0;
        }
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
        // 判断连续密码
        int commonNum = longestCommonSubstring(CONTINUITY_LETTER, password);
        if(commonNum >= 3){
            return 0;
        }
        // 重复字符
        if(repeatString(password)){
            return 0;
        }
        return level;
    }

    private static boolean repeatString(String password){
        char[] chars = password.toCharArray();
        int repeat = 0;
        char tmpC = chars[0];
        for (int i = 1; i < chars.length; i++) {
            char c = chars[i];
            if(tmpC == c){
                repeat++;
            }else {
                tmpC = c; repeat = 0;
            }
            if(repeat >= 2){
                return true;
            }
        }
        return false;
    }

    /**
     * 求两个字符串的公共子串，返回子串的长度
     * @param a 字符串1
     * @param b 字符串2
     * @return 公共子串长度
     */
    public static int longestCommonSubstring(String a, String b) {
        int m = a.length();
        int n = b.length();
        int[][] dp = new int[m][n];
        int maxLen = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (a.charAt(i) == b.charAt(j)) {
                    if (i == 0 || j == 0) {
                        dp[i][j] = 1;
                    } else {
                        dp[i][j] = dp[i-1][j-1] + 1;
                    }
                    maxLen = Math.max(maxLen, dp[i][j]);
                }
            }
        }
        return maxLen;
    }

    /**
     * 求两个字符串的公共子串，返回具体的子串
     * @param a 字符串1
     * @param b 字符串2
     * @return 公共子串
     */
    public static String longestCommonSubstring2(String a, String b) {
        int m = a.length();
        int n = b.length();
        int[][] dp = new int[m][n];
        int maxLen = 0;
        int endA = 0;
        int endB = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (a.charAt(i) == b.charAt(j)) {
                    if (i == 0 || j == 0) {
                        dp[i][j] = 1;
                    } else {
                        dp[i][j] = dp[i-1][j-1] + 1;
                    }
                    if (dp[i][j] > maxLen) {
                        maxLen = dp[i][j];
                        endA = i;
                        endB = j;
                    }
                }
            }
        }
        return a.substring(endA - maxLen + 1, endA + 1);
    }

    public static boolean hasContinuousChars(String password) {
        int[][] keyboard = {
                {1, 2, 3, 4, 5, 6, 7, 8, 9, 0},
                {'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p'},
                {'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l'},
                {'z', 'x', 'c', 'v', 'b', 'n', 'm'}
        };

        for (int i = 0; i < password.length() - 1; i++) {
            char c1 = password.charAt(i);
            char c2 = password.charAt(i + 1);
            int row1 = 0;
            int col1 = 0;
            int row2 = 0;
            int col2 = 0;
            for (int j = 0; j < keyboard.length; j++) {
                for (int k = 0; k < keyboard[j].length; k++) {
                    if (keyboard[j][k] == c1) {
                        row1 = j;
                        col1 = k;
                    }
                    if (keyboard[j][k] == c2) {
                        row2 = j;
                        col2 = k;
                    }
                }
            }
            if (row1 == row2 && Math.abs(col1 - col2) == 1) {
                return true;
            }
            if (col1 == col2 && Math.abs(row1 - row2) == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 生成随机密码
     * @return 随机密码
     */
    public static String generatePasswd(){
        while (true){
            String passwd = RandomUtil.randomString(12);
            int level = checkPasswordSecurity(passwd);
            if(level >= 2){
                return passwd;
            }
        }
    }
}
