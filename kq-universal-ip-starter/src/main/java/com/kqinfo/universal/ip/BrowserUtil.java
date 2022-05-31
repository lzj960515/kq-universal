package com.kqinfo.universal.ip;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author Zijian Liao
 * @since 1.13.0
 */
public final class BrowserUtil {

    private BrowserUtil(){}

    public static String getBrowser(){
        HttpServletRequest request =
                ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        return getBrowser(request);
    }

    public static String getBrowser(HttpServletRequest request){
        String agent = request.getHeader("User-Agent");
        if(StringUtils.isEmpty(agent)){
            return null;
        }
        UserAgent userAgent = UserAgentUtil.parse(agent);
        if (userAgent.isMobile()) {
            return "mobile";
        }
        return userAgent.getBrowser().getName();
    }
}
