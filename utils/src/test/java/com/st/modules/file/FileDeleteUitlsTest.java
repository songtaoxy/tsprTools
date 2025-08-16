package com.st.modules.file;

import com.st.modules.file.local.FileDeleteUitls;
import org.junit.jupiter.api.Test;

import java.io.File;

class FileDeleteUitlsTest {

    @Test
    void delete() {

        String property = System.getProperty("user.dir");


        // 测试删除单个文件
        boolean ok1 = FileDeleteUitls.delete(property+"/tmp/testdir/abc.txt");
        System.out.println("删除单个文件结果: " + ok1);

        // 测试删除目录（含递归删除子文件）
        boolean ok2 = FileDeleteUitls.delete(property+"/tmp/testdir/dir1");
        System.out.println("删除目录结果: " + ok2);

        // 测试空路径
        boolean ok3 = FileDeleteUitls.delete("   ");
        System.out.println("删除空路径: " + ok3);

        // 测试不存在的文件
        boolean ok4 = FileDeleteUitls.delete(property+"/tmp/testdir/not-exist.txt");
        System.out.println("删除不存在: " + ok4);

        // 测试传入null
        boolean ok5 = FileDeleteUitls.delete((File)null);
        System.out.println("删除null: " + ok5);
    }

}