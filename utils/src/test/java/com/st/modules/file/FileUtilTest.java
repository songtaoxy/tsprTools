package com.st.modules.file;

import com.st.modules.file.local.v1.FileCreateUtils;
import org.junit.jupiter.api.Test;

import java.io.File;

class FileUtilTest {

    String property = System.getProperty("user.dir");

    @Test
    public void testCreateFileNoOverwrite() throws Exception {
        // 若文件已存在，不删除不新建，输出提示
        File f1 = FileCreateUtils.createFile(property+"/tmp/testdir/test1.txt", 0, 0);
        System.out.println("创建文件: " + f1.getAbsolutePath());
    }

    @Test
    public void testCreateDirWithOverwrite() throws Exception {
        // 若目录已存在，先递归删除再重建
        File dir = FileCreateUtils.createFile(property+"/tmp/testdir/subdir", 1, 1);
        System.out.println("创建目录: " + dir.getAbsolutePath());
    }

    @Test
    public void testCreateFileIfNotExists() throws Exception {
        File f = FileCreateUtils.createFileIfNotExists("/tmp/testdir/abc.txt");
        System.out.println("文件: " + f.getAbsolutePath());
    }

    @Test
    public void testCreateFileOverwrite() throws Exception {
        File f = FileCreateUtils.createFileOverwrite("/tmp/testdir/abc.txt");
        System.out.println("覆盖文件: " + f.getAbsolutePath());
    }

    @Test
    public void testCreateDirIfNotExists() throws Exception {
        File dir = FileCreateUtils.createDirIfNotExists("/tmp/testdir/dir1");
        System.out.println("目录: " + dir.getAbsolutePath());
    }

    @Test
    public void testCreateDirOverwrite() throws Exception {
        File dir = FileCreateUtils.createDirOverwrite("/tmp/testdir/dir1");
        System.out.println("覆盖目录: " + dir.getAbsolutePath());
    }


}