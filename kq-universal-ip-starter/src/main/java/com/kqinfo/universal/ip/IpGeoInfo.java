package com.kqinfo.universal.ip;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ip地址信息
 *
 * @author Zijian Liao
 * @since 1.13.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IpGeoInfo {

    /**
     * 国家
     */
    private String country;
    /**
     * 地区（一般为省）
     */
    private String region;
    /**
     * 城市
     */
    private String city;
    /**
     * 运营商
     */
    private String isp;
}
