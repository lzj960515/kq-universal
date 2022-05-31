package com.kqinfo.universal.mybatis.util;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.kqinfo.universal.mybatis.properties.EncryptProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 加解密工具
 * @author Zijian Liao
 * @since 1.0.0
 */
@Component
public class EncryptHandler implements InitializingBean {

    @Resource
    private EncryptProperties encryptProperties;

    private SymmetricCrypto aes;

    public String encrypt(String data){
        return aes.encryptHex(data);
    }

    public String decrypt(String data){
        return aes.decryptStr(data, CharsetUtil.CHARSET_UTF_8);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        aes = new SymmetricCrypto(SymmetricAlgorithm.AES, encryptProperties.getSecret().getBytes());
    }
}
