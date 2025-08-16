package com.st.modules.file;

import com.st.modules.file.local.FileRenameUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

class FileRenameUtilsTest {

    @Test
    void rename() {

        String srcPaht = "/Users/songtao/Downloads/filetest/readme.md";
        String newName= "readme2.md";

        // dir
        String dirPaht = "/Users/songtao/Downloads/filetest2/";
        String newNameDir= "filetest3";

        try {
            // 文件重命名
            File renamed = FileRenameUtils.rename(srcPaht, newName);
            System.out.println("重命名后: " + renamed.getAbsolutePath());

            // 目录重命名
            File renamedDir = FileRenameUtils.rename(dirPaht, newNameDir);
            System.out.println("目录重命名后: " + renamedDir.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}