package com.st.modules.file.tar;


import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

import java.io.*;

/**
 * GZIP 压缩/解压工具类（用于 .gz 格式，单文件压缩）
 * <p>
 * 注意
 * <pre>
 * - 不适用于 tar.gz（请使用 TarUtils）
 * - .gz 文件只能压缩/解压单个文件，无法保留目录结构或多文件信息
 * - 如需多文件归档 + 压缩，请使用 .tar.gz，并配合 TarUtils
 * - 方法内已自动处理输出目录创建和异常提示
 * </pre>
 */
public class GzipUtils {

    private static final int BUFFER_SIZE = 4096;

    /**
     * 将单个文件压缩成 .gz 格式
     * <p></p>
     * Usage
     * <pre>
     * {@code
     * File source = new File("/tmp/test.txt");
     * File gzFile = new File("/tmp/test.txt.gz");
     *
     *  GzipUtils.compressToGz(source, gzFile);
     * }
     * </pre>
     *
     * @param sourceFile 要压缩的原始文件（必须存在且是普通文件）
     * @param gzFile     压缩后的 .gz 文件（父目录自动创建）
     * @throws IOException IO 异常或文件不存在时抛出
     */
    public static void compressToGz(File sourceFile, File gzFile) throws IOException {
        if (!sourceFile.exists() || !sourceFile.isFile()) {
            throw new FileNotFoundException("源文件不存在或不是普通文件: " + sourceFile.getAbsolutePath());
        }

        File parent = gzFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (
                FileInputStream fis = new FileInputStream(sourceFile);
                FileOutputStream fos = new FileOutputStream(gzFile);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                GzipCompressorOutputStream gzos = new GzipCompressorOutputStream(bos)
        ) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                gzos.write(buffer, 0, len);
            }
        }
    }


    /**
     * 解压 .gz 文件到指定目录中，保留原始文件名（去掉 .gz 后缀）
     * <p></p>
     * Usage
     * <pre>
     * {@code
     *  String gzPath = "/tmp/log.gz";
     *  String outputDir = "/tmp/logs";
     *  GzipUtils.decompressGz(gzPath, outputDir);
     * }
     * </pre>
     * @param gzFilePath     .gz 文件的完整路径（必须存在，且以 .gz 结尾）
     * @param targetDirPath  解压目标目录路径（自动创建）
     * @throws IOException   文件不存在、格式不合法或 IO 错误时抛出
     */
    public static void decompressGz(String gzFilePath, String targetDirPath) throws IOException {
        if (gzFilePath == null || !gzFilePath.toLowerCase().endsWith(".gz")) {
            throw new IllegalArgumentException("无效的 .gz 文件路径: " + gzFilePath);
        }

        File gzFile = new File(gzFilePath);
        if (!gzFile.exists() || !gzFile.isFile()) {
            throw new FileNotFoundException("待解压文件不存在: " + gzFilePath);
        }

        File targetDir = new File(targetDirPath);
        if (!targetDir.exists()) {
            if (!targetDir.mkdirs()) {
                throw new IOException("无法创建目标目录: " + targetDirPath);
            }
        }

        // 构造解压后输出文件名：去除 .gz 后缀
        String fileName = gzFile.getName();
        String originalName = fileName.substring(0, fileName.length() - 3); // 移除 ".gz"
        File outputFile = new File(targetDir, originalName);

        decompressGz(gzFile, outputFile);
    }




    /**
     * 解压 .gz 文件为原始文件
     * <p></p>
     * Usage
     * <pre>
     * {@code
     *  File gzFile = new File("/tmp/test.txt.gz");
     *  File output = new File("/tmp/test_restored.txt");
     *  GzipUtils.decompressGz(gzFile, output);
     * }
     * </pre>
     *
     * @param gzFile        要解压的 .gz 文件（必须存在）
     * @param outputFile    解压后的目标文件（父目录自动创建）
     * @throws IOException  IO 异常或文件不合法时抛出
     */
    public static void decompressGz(File gzFile, File outputFile) throws IOException {
        if (!gzFile.exists() || !gzFile.isFile()) {
            throw new FileNotFoundException("待解压文件不存在或不是普通文件: " + gzFile.getAbsolutePath());
        }

        File parent = outputFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (
                FileInputStream fis = new FileInputStream(gzFile);
                BufferedInputStream bis = new BufferedInputStream(fis);
                GzipCompressorInputStream gis = new GzipCompressorInputStream(bis);
                FileOutputStream fos = new FileOutputStream(outputFile)
        ) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ((len = gis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
        }
    }
}

