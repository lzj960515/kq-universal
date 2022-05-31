package com.kqinfo.universal.stream.test;

import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Zijian Liao
 * @since 1.4.0
 */
public class TestGetHost {

    @Test
    public void test(){
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            System.out.println(localHost.getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
