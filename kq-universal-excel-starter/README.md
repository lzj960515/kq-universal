# excel组件

## 介绍

我们在开发过程中经常会遇到excel导入导出的需求。经调研，阿里的easyexcel是比较高效易用的工具。本组件主要为大家引入easyexcel和poi相关的依赖，封装了一些操作更简单的工具方法。

easyexcel项目地址：https://github.com/alibaba/easyexcel

easyexcel文档：https://www.yuque.com/easyexcel/doc/easyexcel

## 使用方式

### 1.引入依赖

```xml
<dependency>
  <groupId>com.github.lzj960515</groupId>
  <artifactId>kq-universal-excel-starter</artifactId>
  <version>version</version>
</dependency>
```

### 2.使用

目前只提供了最常见的单表导出功能，复杂一些的导出需求可以直接使用easyexcel提供的方法。

关于IOException，业务方可以自行try-catch，返回BaseResult对象

KqExcelUtil提供了两个重载方法：按表头导出，按实体类导出

先定义一个导出数据的VO类，如果按自定义表头导出，则可以不用写@ExcelProperty注解
```java
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
```

按表头导出
```java
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
```

按实体类导出
```java
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
```