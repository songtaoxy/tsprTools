package com.st.modules.file;

import com.st.modules.file.local.FileCopyUtils;
import org.junit.jupiter.api.Test;

import java.io.File;

class FileCopyUtilsTest {

    @Test

    void copyWithRename() {
        String srcPaht = "/Users/songtao/Downloads/filetest/readme.md";
        String srcPathNew = "/Users/songtao/Downloads/filetest_01";
        String newName= "readme2.md";

        // dir
        String dirPaht = "/Users/songtao/Downloads/filetest2/";
        String dirPahtNew="/Users/songtao/Downloads/filetest2_copy/";
        String dirPahtNew2="/Users/songtao/Downloads/filetest2_copy/";
        String newNameDir= "filetest2_copy_copy";
        try {
            // 文件复制并重命名
            File copied = FileCopyUtils.copyWithRename(srcPaht, srcPathNew, "readme.txt");
            System.out.println("已复制到: " + copied.getAbsolutePath());

            // 复制整个目录并重命名
            File copiedDir = FileCopyUtils.copyWithRename(dirPaht, dirPahtNew, newNameDir);
            System.out.println("已复制目录到: " + copiedDir.getAbsolutePath());

            // 保持原名复制
            File copiedKeep = FileCopyUtils.copyWithRename(dirPaht, dirPahtNew2, null);
            System.out.println("保持原名: " + copiedKeep.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}