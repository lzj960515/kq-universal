package com.kqinfo.universal.dynamic.datasource;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;

/**
 * 加解密工具
 * @author Zijian Liao
 * @since 1.0.0
 */
public class EncryptHandler {

    private final SymmetricCrypto aes;

    public EncryptHandler(SymmetricCrypto aes) {
        this.aes = aes;
    }

    public String encrypt(String data){
        return aes.encryptHex(data);
    }

    public String decrypt(String data){
        return aes.decryptStr(data, CharsetUtil.CHARSET_UTF_8);
    }
}
