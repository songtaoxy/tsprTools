package com.st.modules.file.tar;

import lombok.SneakyThrows;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

import java.io.*;

/**
 * <pre>
 * - 解压缩
 * </pre>
 */
public class TarUtils {

    /**
     * 压缩文件或目录为 tar.gz
     * <pre>
     * - 支持文件与目录递归压缩
     * - 压缩结果标准 tar.gz，兼容 Linux 命令解压
     * - 适用于大文件、深层目录结构
     * </pre>
     *
     * dependencys:
     * <pre>
     *  {@code
     *        <dependency>
     *             <groupId>org.apache.commons</groupId>
     *             <artifactId>commons-compress</artifactId>
     *             <version>1.24.0</version>
     *         </dependency>}
     * </pre>
     *
     * @param source 要压缩的文件或目录
     * @param tarGzFile 目标 tar.gz 文件
     * @throws IOException
     */
    public static void compressToTarGz(File source, File tarGzFile) throws IOException {
        try (
                FileOutputStream fos = new FileOutputStream(tarGzFile);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                GzipCompressorOutputStream gzos = new GzipCompressorOutputStream(bos);
                TarArchiveOutputStream taos = new TarArchiveOutputStream(gzos)
        ) {
            // 推荐：长文件名兼容
            taos.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
            addFileToTar(taos, source, source.getName());
        }
    }

    // 递归添加文件/目录到 tar
    private static void addFileToTar(TarArchiveOutputStream taos, File file, String entryName) throws IOException {
        if (file.isFile()) {
            TarArchiveEntry entry = new TarArchiveEntry(file, entryName);
            taos.putArchiveEntry(entry);

            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[4096];
                int n;
                while ((n = fis.read(buffer)) != -1) {
                    taos.write(buffer, 0, n);
                }
            }
            taos.closeArchiveEntry();
        } else if (file.isDirectory()) {
            // 添加目录 entry
            if (!entryName.endsWith("/")) entryName += "/";
            taos.putArchiveEntry(new TarArchiveEntry(file, entryName));
            taos.closeArchiveEntry();

            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    addFileToTar(taos, child, entryName + child.getName());
                }
            }
        }
    }

    /**
     * 解压 tar.gz 文件到指定目录
     * <pre>
     * - 只需 extractTarGz(tarGzPath, destDir) 即可解压。
     * - 目标目录自动递归创建，文件及目录结构完整还原。
     * - 支持任意层级的子目录和文件。
     * - 适合 Spring Boot、Java 独立项目等直接复用。
     * </pre>
     *
     * <pre>unit test: ok</pre>
     *

     * Usage
     * <pre>
     * {@code
     * public static void main(String[] args) {
     *         String tarGzPath = "/tmp/example.tar.gz";
     *         String destDir = "/tmp/unpack";
     *         try {
     *             TarGzUtils.extractTarGz(tarGzPath, destDir);
     *             System.out.println("解压完成！");
     *         } catch (IOException e) {
     *             e.printStackTrace();
     *         }
     *     }
     * }
     * </pre>
     * @param tarGzPath tar.gz 文件路径
     * @param destDir 目标目录（自动创建）
     * @throws IOException
     */
    @SneakyThrows
    public static void extractTarGz(String tarGzPath, String destDir) {
        File dest = new File(destDir);
        if (!dest.exists()) dest.mkdirs();

        try (
                FileInputStream fis = new FileInputStream(tarGzPath);
                BufferedInputStream bis = new BufferedInputStream(fis);
                GzipCompressorInputStream gis = new GzipCompressorInputStream(bis);
                TarArchiveInputStream tis = new TarArchiveInputStream(gis)
        ) {
            ArchiveEntry entry;
            while ((entry = tis.getNextEntry()) != null) {
                String entryName = entry.getName();
                File entryFile = new File(destDir, entryName);
                if (entry.isDirectory()) {
                    entryFile.mkdirs();
                } else {
                    File parent = entryFile.getParentFile();
                    if (!parent.exists()) parent.mkdirs();
                    try (OutputStream os = new FileOutputStream(entryFile)) {
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = tis.read(buffer)) != -1) {
                            os.write(buffer, 0, len);
                        }
                    }
                }
            }
        }
    }
}

