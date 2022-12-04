package com.kqinfo.universal.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExcelExportDemo {

    @ExcelProperty("编号")
    Integer id;
    @ExcelProperty("姓名")
    String name;
    @ExcelProperty("性别")
    String sex;
    @ExcelProperty("年龄")
    Integer age;
    @ExcelProperty("职业")
    String profession;
    @ExcelProperty("称号")
    String title;
}
