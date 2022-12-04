package com.kqinfo.universal.excel;

import com.kqinfo.universal.excel.util.KqExcelUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/excel")
public class ExcelTestController {

    @GetMapping("/download-by-head")
    public void downloadByHead(HttpServletResponse response) throws IOException {

        // 需要导出的数据列表
        List<ExcelExportDemo> list = new ArrayList<>();
        list.add(new ExcelExportDemo(1, "刘备", "男", 20, "君主", "惟贤惟德"));
        list.add(new ExcelExportDemo(2, "关羽", "男", 19, "都督", "威震华夏"));
        list.add(new ExcelExportDemo(3, "张飞", "男", 18, "太守", "万人敌"));

        // 自定义的表头
        List<String> head = Arrays.asList("编号", "姓名", "性别", "年龄", "职业", "称号");

        // 一行代码导出excel
        KqExcelUtil.exportExcel(response, head, "三国花名册", "sheet1", list);
    }

    @GetMapping("/download-by-obj")
    public void downloadByObj(HttpServletResponse response) throws IOException {

        // 需要导出的数据列表
        List<ExcelExportDemo> list = new ArrayList<>();
        list.add(new ExcelExportDemo(1, "刘备", "男", 20, "君主", "惟贤惟德"));
        list.add(new ExcelExportDemo(2, "关羽", "男", 19, "都督", "威震华夏"));
        list.add(new ExcelExportDemo(3, "张飞", "男", 18, "太守", "万人敌"));

        // 一行代码导出excel
        KqExcelUtil.exportExcel(response, ExcelExportDemo.class, "三国花名册", "sheet1", list);
    }

}
