package com.st.modules.file;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileRenameUtils {

    /**
     * 重命名文件或目录（仅在同一目录下更名，不做移动）
     * <pre>
     * - 只支持__在同一目录下重命名__，不做跨目录移动。
     * - 新名称不能包含路径（比如不能带“/”或“\”）。
     * - 若目标名称已存在则直接抛出异常，不会覆盖。
     * - 文件和目录均可重命名，底层用 NIO Files.move()，更安全、兼容性强。
     * - 原路径会变为新路径，原文件/目录即消失
     * </pre>
     *
     *  unit test(+), and usage:
     *  <pre>
     *  {@code
     *String srcPaht = "/Users/songtao/Downloads/filetest/readme.md";
     *         String newName= "readme2.md";
     *
     *         // dir
     *         String dirPaht = "/Users/songtao/Downloads/filetest2/";
     *         String newNameDir= "filetest3";
     *
     *         try {
     *             // 文件重命名
     *             File renamed = FileRenameUtils.rename(srcPaht, newName);
     *             System.out.println("重命名后: " + renamed.getAbsolutePath());
     *
     *             // 目录重命名
     *             File renamedDir = FileRenameUtils.rename(dirPaht, newNameDir);
     *             System.out.println("目录重命名后: " + renamedDir.getAbsolutePath());
     *         } catch (IOException e) {
     *             e.printStackTrace();
     *         }
     *  }
     *  </pre>
     *
     * @param srcPath 原文件或目录的绝对路径/相对路径
     * @param newName 新文件或目录名（仅名称，不含路径）
     * @return 重命名后的 File 对象
     * @throws IOException 重命名失败（如已存在同名文件等）
     */
    public static File rename(String srcPath, String newName) throws IOException {
        File src = new File(srcPath);
        if (!src.exists()) throw new IOException("源文件/目录不存在: " + srcPath);
        if (newName == null || newName.trim().isEmpty())
            throw new IOException("新名称不能为空");
        if (newName.contains(File.separator))
            throw new IOException("新名称不能包含路径分隔符");

        File dest = new File(src.getParent(), newName);

        // 检查目标是否已存在
        if (dest.exists()) throw new IOException("目标文件/目录已存在: " + dest.getAbsolutePath());

        // 推荐使用 NIO move 实现重命名（跨平台且安全）
        Files.move(src.toPath(), dest.toPath(), StandardCopyOption.ATOMIC_MOVE);
        return dest;
    }
}

