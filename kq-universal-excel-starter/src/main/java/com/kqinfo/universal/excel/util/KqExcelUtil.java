package com.kqinfo.universal.excel.util;

import com.alibaba.excel.EasyExcel;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author xujiahong
 */
public class KqExcelUtil {

    /**
     * 按表头导出excel
     */
    public static <T> void exportExcel(HttpServletResponse response, List<String> head, String filename, String sheetName, List<T> data) throws IOException {
        /*
        easyexcel 的多层次表头设定，对单表导出来说有点迷惑
         */
        List<List<String>> listListHead = new ArrayList<>(head.size());
        for (String fieldName : head) {
            List<String> list = Collections.singletonList(fieldName);
            listListHead.add(list);
        }
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        filename = URLEncoder.encode(filename, StandardCharsets.UTF_8.name());
        response.setHeader("Content-disposition", "attachment;filename=" + filename + ".xlsx");
        EasyExcel.write(response.getOutputStream()).head(listListHead).useDefaultStyle(Boolean.FALSE).sheet(sheetName).doWrite(data);
    }

    /**
     * 按实体类导出excel
     */
    public static <T> void exportExcel(HttpServletResponse response, Class<T> clazz, String filename, String sheetName, List<T> data) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        filename = URLEncoder.encode(filename, StandardCharsets.UTF_8.name());
        response.setHeader("Content-disposition", "attachment;filename=" + filename + ".xlsx");
        EasyExcel.write(response.getOutputStream(), clazz).useDefaultStyle(Boolean.FALSE).sheet(sheetName).doWrite(data);
    }
}
