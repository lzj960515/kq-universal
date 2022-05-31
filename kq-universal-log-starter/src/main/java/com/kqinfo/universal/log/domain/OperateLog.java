package com.kqinfo.universal.log.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class OperateLog implements Serializable {

    private static final long serialVersionUID = 2776565456017573278L;

    private Long id;
    /**
     * 操作人id
     */
    private String userId;
    /**
     * 操作人名称
     */
    private String username;
    /**
     * 操作内容
     */
    private String content;
    /**
     * 日志种类
     */
    private String category;
    /**
     * 业务编号
     */
    private String businessNo;
    /**
     * 方法参数
     */
    private String param;
    /**
     * ip地址
     */
    private String ipAddress;
    /**
     * 地理位置
     */
    private String geo;
    /**
     * 浏览器
     */
    private String browser;
    /**
     * 操作时间
     */
    private LocalDateTime operateTime;

    public OperateLog(Long id, String userId, String username, String content, String category, String businessNo, String ipAddress, String geo, String browser, LocalDateTime operateTime) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.content = content;
        this.category = category;
        this.businessNo = businessNo;
        this.ipAddress = ipAddress;
        this.geo = geo;
        this.browser = browser;
        this.operateTime = operateTime;
    }
}
