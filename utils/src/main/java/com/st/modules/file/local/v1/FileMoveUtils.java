package com.st.modules.file.local.v1;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class FileMoveUtils {

    /**
     * 移动文件或目录到目标路径，并可重命名
     * <pre>
     * - 支持文件和目录，目录会整体移动，包括全部内容（不会做递归拷贝，是系统级移动）。
     * - 如目标目录中已有同名文件/目录，会被覆盖（StandardCopyOption.REPLACE_EXISTING）。
     * - 若目标目录不存在，会自动递归创建
     * - 原始文件或目录移动后即消失，仅在目标路径下可见。
     * - 适用于本地磁盘，跨分区移动时也能正确工作（Java 7+）。
     * - 如只想“重命名”文件，destDir可传父目录，newName传新名
     * - NIO Files.move() 是目前 Java 官方推荐的跨平台原子移动方案，可靠且高效
     * </pre>
     *
     * usage
     * <pre>
     * {@code
     *  try {
     *             // 将 /tmp/test.txt 移动到 /tmp/backup/ 并重命名为 test_2024.txt
     *             File moved = FileMoveUtils.moveWithRename("/tmp/test.txt", "/tmp/backup", "test_2024.txt");
     *             System.out.println("已移动到: " + moved.getAbsolutePath());
     *
     *             // 也支持移动整个目录并改名
     *             File movedDir = FileMoveUtils.moveWithRename("/tmp/data", "/tmp/backup", "data_copy");
     *             System.out.println("已移动目录到: " + movedDir.getAbsolutePath());
     *
     *             // 保持原名
     *             File movedKeep = FileMoveUtils.moveWithRename("/tmp/file.log", "/tmp/backup", null);
     *             System.out.println("保持原名: " + movedKeep.getAbsolutePath());
     *
     *         } catch (IOException e) {
     *             e.printStackTrace();
     *         }
     * }
     * </pre>
     * @param srcPath    源文件或目录路径（绝对路径/相对路径均可）
     * @param destDir    目标目录路径（如 "/tmp/backup"）
     * @param newName    新文件/目录名，若为null或空字符串则保持原名
     * @return 移动后文件的File对象
     * @throws IOException 若移动失败
     */
    public static File moveWithRename(String srcPath, String destDir, String newName) throws IOException {
        File src = new File(srcPath);
        if (!src.exists()) throw new IOException("源文件/目录不存在: " + srcPath);

        // 目标目录必须存在
        File destDirectory = new File(destDir);
        if (!destDirectory.exists() && !destDirectory.mkdirs()) {
            throw new IOException("无法创建目标目录: " + destDir);
        }

        // 构造目标路径
        String name = (newName != null && !newName.trim().isEmpty()) ? newName : src.getName();
        File dest = new File(destDirectory, name);

        // 使用 Java NIO 原子移动
        Files.move(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);

        return dest;
    }
}
