package com.kqinfo.universal.common.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class PageBean<T> implements Serializable {
    private static final long serialVersionUID = -2165891535139903764L;
    private long currentPage;
    private long numPerPage;
    private long totalCount;
    private List<T> recordList;
    private long pageCount;
    private long beginPageIndex;
    private long endPageIndex;
    private long beginIndex;
    private Map<String, Object> countResultMap;

    public PageBean(IPage<T> iPage) {
        this(iPage.getCurrent(), iPage.getSize(), iPage.getTotal(), iPage.getRecords());
    }

    public PageBean(long currentPage, long numPerPage, long totalCount, List<T> recordList) {
        this.currentPage = currentPage;
        this.numPerPage = numPerPage;
        this.totalCount = totalCount;
        this.recordList = recordList;
        this.beginIndex = (currentPage - 1) * numPerPage + 1;
        this.pageCount = (totalCount + numPerPage - 1) / numPerPage;
        if (this.pageCount <= 10) {
            this.beginPageIndex = 1;
            this.endPageIndex = this.pageCount;
        } else {
            this.beginPageIndex = currentPage - 4;
            this.endPageIndex = currentPage + 5;
            if (this.beginPageIndex < 1) {
                this.beginPageIndex = 1;
                this.endPageIndex = 10;
            }

            if (this.endPageIndex > this.pageCount) {
                this.endPageIndex = this.pageCount;
                this.beginPageIndex = this.pageCount - 10 + 1;
            }
        }

    }

    public static <T> PageBean<T> of(IPage<T> iPage) {
        return new PageBean<>(iPage);
    }
}