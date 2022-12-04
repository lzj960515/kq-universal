package com.kqinfo.universal.comdao.core;

import lombok.Data;

import java.util.List;

/**
 * @author zpj
 */
@Data
public class PageResult<T> {

    /**
     * 结果集
     */
    private List<T> list;
    /**
     * 当前页
     */
    private int pageNum;
    /**
     * 每页的数量
     */
    private int pageSize;
    /**
     * 当前页的数量
     */
    private int size;
    /**
     * 总页数
     */
    private int pages;
    /**
     * 总条数
     */
    private long total;

    public PageResult(List<T> list, long count, int pageNum, int pageSize) {
        this.list = list;
        this.total = count;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.size = (list != null) ? list.size() : 0;
        this.pages = (int) (count + pageSize - 1) / pageSize;

        //分页大小不能小于1
        if (pageSize < 1) {
            this.pageSize = 10;
        }

    }

}
