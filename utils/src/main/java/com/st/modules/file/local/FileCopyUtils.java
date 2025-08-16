package com.st.modules.file.local;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class FileCopyUtils {

    /**
     * 复制文件或目录到目标路径，并可重命名:
     * <pre>
     * - Usage, ref "unit Test":{@code com.st.modules.file.FileCopyUtilsTest}
     * - 单文件直接copy，目录则递归copy，逻辑一致、接口友好，重命名靠newName实现
     * - 支持文件和目录，目录会递归复制所有子文件与子目录。
     * - 如目标已存在同名文件/目录，将被覆盖。
     * - 复制操作不影响源文件/目录，复制后源仍保留。
     * - 如果只是重命名，可以只改newName，destDir传父目录即可。
     * - 使用NIO方式，性能更好，且异常处理完整。
     * </pre>
     *
     * Usage
     * <pre>
     * {@code
     *     try {
     *             // 文件复制并重命名
     *             File copied = FileCopyUtils.copyWithRename("/tmp/test.txt", "/tmp/backup", "test_copy.txt");
     *             System.out.println("已复制到: " + copied.getAbsolutePath());
     *
     *             // 复制整个目录并重命名
     *             File copiedDir = FileCopyUtils.copyWithRename("/tmp/data", "/tmp/backup", "data_copy");
     *             System.out.println("已复制目录到: " + copiedDir.getAbsolutePath());
     *
     *             // 保持原名复制
     *             File copiedKeep = FileCopyUtils.copyWithRename("/tmp/file.log", "/tmp/backup", null);
     *             System.out.println("保持原名: " + copiedKeep.getAbsolutePath());
     *
     *         } catch (IOException e) {
     *             e.printStackTrace();
     *         }
     * }
     * </pre>
     * @param srcPath  源文件/目录全路径
     * @param destDir  目标目录(是目录)
     * @param newName  新文件/目录名，不带路径, 若为null或空字符串则保持原名
     * @return 复制后的File对象
     * @throws IOException 拷贝失败
     */
    public static File copyWithRename(String srcPath, String destDir, String newName) throws IOException {
        File src = new File(srcPath);
        if (!src.exists()) throw new IOException("源文件/目录不存在: " + srcPath);

        File destDirectory = new File(destDir);
        if (!destDirectory.exists() && !destDirectory.mkdirs()) {
            throw new IOException("无法创建目标目录: " + destDir);
        }

        String name = (newName != null && !newName.trim().isEmpty()) ? newName : src.getName();
        File dest = new File(destDirectory, name);

        if (src.isFile()) {
            // 文件直接复制
            Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } else {
            // 目录需要递归复制
            copyDirRecursive(src.toPath(), dest.toPath());
        }
        return dest;
    }

    // 递归复制目录
    private static void copyDirRecursive(Path src, Path dest) throws IOException {
        Files.walkFileTree(src, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path targetDir = dest.resolve(src.relativize(dir));
                if (!Files.exists(targetDir)) {
                    Files.createDirectory(targetDir);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path targetFile = dest.resolve(src.relativize(file));
                Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}

