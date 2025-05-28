package com.st.modules.tar;

import com.st.modules.file.FileCreateUtils;
import com.st.modules.file.tar.TarUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static com.st.modules.constant.FileConst.*;


class TarUtilsTest {

    @SneakyThrows
    @Test
    void compressToTarGz() {


        String property = System.getProperty("user.dir");
        System.out.println(property);

        // 单个文件
        File sourceFile = FileCreateUtils.createFileIfNotExists(property+"/tmp/hello.txt");
        System.out.println(sourceFile.getAbsolutePath());

        File outTarGz = FileCreateUtils.createFileOverwrite(property+"/tmp/hello.tar.gz");
        TarUtils.compressToTarGz(sourceFile, outTarGz);
        System.out.println("压缩单文件完成: " + outTarGz.getAbsolutePath());

        // 目录
        String dirPath = property+"/tmp/testdir";
        File dirFIle = FileCreateUtils.createDirIfNotExists(dirPath);

        String outTarGzPath= property+"/tmp/testdir.tar.gz";
        File outTarGzDir = FileCreateUtils.createFileOverwrite(outTarGzPath);

        TarUtils.compressToTarGz(dirFIle, outTarGzDir);
        System.out.println("压缩目录完成: " + outTarGzDir.getAbsolutePath());


    }

    @Test
    void extractTarGz() {
        String timeFlag = "2025-05-28_013808";
        // 经费总账/下发文件目录
       String fglsDistributeFile = voucherTempDir+fgls+s+"distribute"+s+timeFlag+s;
        // 经费总账/接收文件目录
       String fglsReceiveFile = voucherTempDir+fgls+s+"receive"+s+timeFlag+s;

        // 固定资产/下发文件目录
         String famsDistributeFile = voucherTempDir+fgls+s+"distribute"+s+timeFlag+s;
        // 固定资产/接收文件目录
         String famsReceiveFile = voucherTempDir+fgls+s+"receive"+s+timeFlag+s;



        String tarGzPath = fglsDistributeFile+"AP_GL_AAAAAA123456789_20250528_001.tar.gz";
        String destDir = fglsReceiveFile;
        try {
            TarUtils.extractTarGz(tarGzPath, destDir);
            System.out.println("解压完成！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}