package com.kqinfo.universal.ip;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

/**
 * @author Zijian Liao
 * @since 1.13.0
 */
@Slf4j
public final class IpUtil {

    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST = "127.0.0.1";
    private static final String SEPARATOR = ",";


    private IpUtil(){}

    public static String getIpAddress(){
        HttpServletRequest request =
                ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        return getIpAddress(request);
    }

    public static String getIpAddress(HttpServletRequest request){
        // 先取realIp
        String ipAddress = request.getHeader("X-Real-IP");
        if(isEmpty(ipAddress)){
            ipAddress = request.getHeader("x-forwarded-for");
        }
        if (isEmpty(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (isEmpty(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isEmpty(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (LOCALHOST.equals(ipAddress)) {
                InetAddress inet;
                try {
                    inet = InetAddress.getLocalHost();
                    ipAddress = inet.getHostAddress();
                } catch (UnknownHostException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        // "***.***.***.***".length()
        if (ipAddress != null && ipAddress.length() > 15 && ipAddress.contains(SEPARATOR)) {
            ipAddress = ipAddress.split(SEPARATOR)[0];
        }
        log.info("IpUtil get ip:{}", ipAddress);
        return ipAddress;
    }

    private static boolean isEmpty(String ipAddress){
        return ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress);
    }
}