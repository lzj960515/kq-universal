package com.kqinfo.universal.excel;

import cn.hutool.core.stream.StreamUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.excel.EasyExcel;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 测试web读取
 * @author Zijian Liao
 * @since 2.21.0
 */
@RestController
public class WebReadExcelTest {

    @PostMapping("/upload")
    @ResponseBody
    public String upload(MultipartFile file) throws IOException {
        new Thread(() -> {
            try {
                InputStream inputStream = file.getInputStream();
                byte[] bytes = file.getBytes();
                EasyExcel.read(inputStream, DemoData.class, new DemoDataListener()).sheet().doRead();
                EasyExcel.read(new ByteArrayInputStream(bytes), DemoData.class, new DemoDataListener()).sheet().doRead();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        return "success";
    }
}
