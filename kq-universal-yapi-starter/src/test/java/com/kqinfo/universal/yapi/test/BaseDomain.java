package com.kqinfo.universal.yapi.test;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通用领域模型
 *
 * @author Zijian Liao
 * @since v1.0.0
 */
@Data
@Accessors(chain = true)
public abstract class BaseDomain implements Serializable {

    private static final long serialVersionUID = -1239542174707898422L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
}
