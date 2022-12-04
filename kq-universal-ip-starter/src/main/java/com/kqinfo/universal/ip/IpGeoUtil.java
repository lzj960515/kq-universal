package com.kqinfo.universal.ip;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * ip地理位置获取
 *
 * @author Zijian Liao
 * @since 1.13.0
 */
@Slf4j
public final class IpGeoUtil {

    private static final String GEO_IP_URL = "http://ip.taobao.com/outGetIpInfo";
    private static final Integer OK = 0;

    private IpGeoUtil() {
    }

    public static IpGeoInfo getGeo() {
        final String ipAddress = IpUtil.getIpAddress();
        return getGeo(ipAddress);
    }

    public static IpGeoInfo getGeo(String ipAddress) {
        if(StringUtils.isEmpty(ipAddress)){
            return null;
        }
        try{
            String result = HttpRequest.post(GEO_IP_URL).form("ip", ipAddress)
                    .form("accessKey", "alibaba-inc")
                    .execute()
                    .body();
            final JSONObject resultJson = JSONUtil.parseObj(result);
            final Integer code = resultJson.getInt("code");
            if (!OK.equals(code)) {
                log.error("通过ip:{} 获取地理位置信息失败：{}", ipAddress, resultJson.getStr("msg"));
                return new IpGeoInfo();
            }
            final JSONObject geoJson = resultJson.getJSONObject("data");
            final String country = geoJson.getStr("country");
            final String region = geoJson.getStr("region");
            final String city = geoJson.getStr("city");
            final String isp = geoJson.getStr("isp");
            return new IpGeoInfo(country, region, city, isp);
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return null;
        }

    }

}