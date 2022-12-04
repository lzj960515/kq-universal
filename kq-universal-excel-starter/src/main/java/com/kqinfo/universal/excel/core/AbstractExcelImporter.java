package com.kqinfo.universal.excel.core;

import java.io.InputStream;
import java.util.List;

/**
 * excel导入模板方法
 *
 * @author Zijian Liao
 * @since 2.21.0
 */
public abstract class AbstractExcelImporter<T> {

    public void importExcel(InputStream inputStream){

    }

    /**
     * 校验数据正确性
     * @param ts 数据列表
     */
    abstract void validData(List<T> ts);

    /**
     * 导入数据
     * @param ts 数据列表
     */
    abstract void importData(List<T> ts);
}
