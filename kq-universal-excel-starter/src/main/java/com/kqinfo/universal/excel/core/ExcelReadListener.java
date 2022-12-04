package com.kqinfo.universal.excel.core;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Zijian Liao
 * @since 2.21.0
 */
public class ExcelReadListener<T> implements ReadListener<T> {
    /**
     * 单处理数据量
     */
    private final int batchCount;
    /**
     * 数据的临时存储
     */
    private List<T> cachedDataList;
    /**
     * consumer
     */
    private final Consumer<List<T>> consumer;

    public ExcelReadListener(Consumer<List<T>> consumer, int batchCount) {
        this.batchCount = batchCount;
        this.consumer = consumer;
        this.cachedDataList = ListUtils.newArrayListWithExpectedSize(batchCount);
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        cachedDataList.add(data);
        if (cachedDataList.size() >= batchCount) {
            consumer.accept(cachedDataList);
            cachedDataList = ListUtils.newArrayListWithExpectedSize(batchCount);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (CollectionUtils.isNotEmpty(cachedDataList)) {
            consumer.accept(cachedDataList);
        }
    }
}
