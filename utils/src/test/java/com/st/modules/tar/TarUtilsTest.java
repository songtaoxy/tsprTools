package com.st.modules.tar;

import com.st.modules.file.FileUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.File;


class TarUtilsTest {

    @SneakyThrows
    @Test
    void compressToTarGz() {


        String property = System.getProperty("user.dir");
        System.out.println(property);

        // 单个文件
        File sourceFile = FileUtil.createFileIfNotExists(property+"/tmp/hello.txt");
        System.out.println(sourceFile.getAbsolutePath());

        File outTarGz = FileUtil.createFileOverwrite(property+"/tmp/hello.tar.gz");
        TarUtils.compressToTarGz(sourceFile, outTarGz);
        System.out.println("压缩单文件完成: " + outTarGz.getAbsolutePath());

        // 目录
        String dirPath = property+"/tmp/testdir";
        File dirFIle = FileUtil.createDirIfNotExists(dirPath);

        String outTarGzPath= property+"/tmp/testdir.tar.gz";
        File outTarGzDir = FileUtil.createFileOverwrite(outTarGzPath);

        TarUtils.compressToTarGz(dirFIle, outTarGzDir);
        System.out.println("压缩目录完成: " + outTarGzDir.getAbsolutePath());


    }
}