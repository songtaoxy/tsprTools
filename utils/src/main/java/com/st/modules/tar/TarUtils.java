package com.st.modules.tar;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
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
     * - 支持文件与目录递归压缩
     * - 压缩结果标准 tar.gz，兼容 Linux 命令解压
     * - 适用于大文件、深层目录结构
     *
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
}

