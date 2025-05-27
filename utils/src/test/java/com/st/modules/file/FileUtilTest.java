package com.st.modules.file;

import org.junit.jupiter.api.Test;

import java.io.File;

class FileUtilTest {

    String property = System.getProperty("user.dir");

    @Test
    public void testCreateFileNoOverwrite() throws Exception {
        // 若文件已存在，不删除不新建，输出提示
        File f1 = FileUtils.buildFile(property+"/tmp/testdir/test1.txt", 0, 0);
        System.out.println("创建文件: " + f1.getAbsolutePath());
    }

    @Test
    public void testCreateDirWithOverwrite() throws Exception {
        // 若目录已存在，先递归删除再重建
        File dir = FileUtils.buildFile(property+"/tmp/testdir/subdir", 1, 1);
        System.out.println("创建目录: " + dir.getAbsolutePath());
    }

    @Test
    public void testCreateFileIfNotExists() throws Exception {
        File f = FileUtils.createFileIfNotExists("/tmp/testdir/abc.txt");
        System.out.println("文件: " + f.getAbsolutePath());
    }

    @Test
    public void testCreateFileOverwrite() throws Exception {
        File f = FileUtils.createFileOverwrite("/tmp/testdir/abc.txt");
        System.out.println("覆盖文件: " + f.getAbsolutePath());
    }

    @Test
    public void testCreateDirIfNotExists() throws Exception {
        File dir = FileUtils.createDirIfNotExists("/tmp/testdir/dir1");
        System.out.println("目录: " + dir.getAbsolutePath());
    }

    @Test
    public void testCreateDirOverwrite() throws Exception {
        File dir = FileUtils.createDirOverwrite("/tmp/testdir/dir1");
        System.out.println("覆盖目录: " + dir.getAbsolutePath());
    }


    @Test
    void delete() {

        // 测试删除单个文件
        boolean ok1 = FileUtils.delete(property+"/tmp/testdir/abc.txt");
        System.out.println("删除单个文件结果: " + ok1);

        // 测试删除目录（含递归删除子文件）
        boolean ok2 = FileUtils.delete(property+"/tmp/testdir/dir1");
        System.out.println("删除目录结果: " + ok2);

        // 测试空路径
        boolean ok3 = FileUtils.delete("   ");
        System.out.println("删除空路径: " + ok3);

        // 测试不存在的文件
        boolean ok4 = FileUtils.delete(property+"/tmp/testdir/not-exist.txt");
        System.out.println("删除不存在: " + ok4);

        // 测试传入null
        boolean ok5 = FileUtils.delete((File)null);
        System.out.println("删除null: " + ok5);
    }

}