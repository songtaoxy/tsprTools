package com.st.modules.ftp;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FtpUtilsTest {

    @Test
    void uploadString() {
        // 上传字符串
        boolean ok = FtpUtils.uploadString("hello.txt", "Hello, FTP!\n中文测试");
        System.out.println("字符串上传结果：" + ok);
    }

    @Test
    void uploadFile() {
        // 上传本地文件
        File file = new File("/tmp/data.txt");
        boolean ok2 = FtpUtils.uploadFile("data.txt", file);
        System.out.println("文件上传结果：" + ok2);
    }

    @Test
    void uploadStream() {
        System.out.println("");
    }

    @Test
    void uploadStringC() {
        // 字符串断点续传
        FtpUtils.uploadString("a.txt", "Hello, FTP with Resume!");

    }

    @Test
    void uploadFileC() {
        // 文件断点续传
        FtpUtils.uploadFile("bigfile.zip", new File("/tmp/bigfile.zip"));
    }

    @Test
    void uploadStreamC() {
        System.out.println("");
    }
}