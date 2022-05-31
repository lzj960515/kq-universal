package com.kqinfo.universal.log.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Zijian Liao
 * @since 1.11.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = -2625216948706397967L;

    /**
     * 当前页
     */
    private long current;

    /**
     * 每页显示条数
     */
    private long size;

    /**
     * 总行数
     */
    private long total;

    /**
     * 分页记录列表
     */
    private List<T> records;
}
