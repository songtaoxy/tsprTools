package com.st.modules.file.transfer;

import java.io.*;
import java.nio.file.*;
import java.util.Base64;

/**
 * <pre>
 *  - 通用文件传输工具类, 无乱码安全传输
 *  - 支持文件 -》byte[] -〉编码: Base64字符串 -> 解码: byte[] -> 文件
 *  - 工具类方法统一处理，100%防乱码、防损坏; 同样适用于文本、图片、音频、PDF、Office文档等所有格式
 *  ---------------------------------------------------------------------------------------
 *  - Java开发中, 系统之间需要传输文件,因为没有ftp等平台, 因此直接在接口中传输文件.
 *  - 即, 一个系统a读取文件到内存, 发送给系统b, 系统b获取到内容, 写到文件中. 抽取完整的工具类, 同时, 要百分百避免乱码问题
 * </pre>
 *
 * <pre>
 * 设计说明
 * - 文件通过接口（HTTP/REST等）传输，不依赖FTP/SFTP
 * - 关键原则：文件要以Base64字符串或byte数组传递，不要用直接转字符串
 * - Base64保证所有文件类型（文本/图片/二进制）都不会乱码，也不会损坏
 * - 工具类同时支持：文件转Base64，Base64转文件，流式高效处理，异常包装
 * </pre>
 *
 * <pre>
 * 为什么不会乱码
 * - Base64 是专为二进制转文本而设计，所有文件内容无论是什么格式.
 * - 经Base64编码后都是可安全传输的标准字符（A-Z, a-z, 0-9, +, /, =）,绝不会产生乱码,也不会丢失任何信息
 * - 恢复时100%可还原原始二进制文件
 * </pre>
 *
 * 一, 小文件或简单场景：直接转Base64字符串，作为JSON字段传输
 * <pre>
 *     {@code
 *     // 发送端
 *     // 通过接口POST base64内容到系统B
 *     String base64 = FileTransferUtils.fileToBase64("a.docx");
 *
 *     // 接收端: 接收到Base64字符串，恢复为文件
 *     // 假设收到的base64变量为请求字段
 *     FileTransferUtils.base64ToFile(base64, "b.docx");
 *     }
 * </pre>
 *
 * 二, 大文件接口流式传输（如multipart或HTTP流）
 * <pre>
 *     {@code
 *     // 发送端
 *     // 直接用fileToBase64Stream读取文件，写到HTTP响应流
 *     FileTransferUtils.fileToBase64Stream("a-large.zip", httpResponse.getOutputStream());
 *
 *     // 接收端
 *     // 接收到Base64流（如HTTP流/POST Body），恢复为文件
 *     FileTransferUtils.base64StreamToFile(httpRequest.getInputStream(), "b-large.zip");
 *     }
 * </pre>
 */
public class FileTransferUtils {

    /**
     * 文件转Base64字符串（适合小文件/接口传输）
     * @param filePath 文件路径
     * @return Base64字符串
     */
    public static String fileToBase64(String filePath) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(filePath));
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            throw new RuntimeException("文件转Base64失败：" + filePath, e);
        }
    }

    /**
     * Base64字符串写回文件（适合接口接收后落盘）
     * @param base64   Base64内容
     * @param filePath 目标文件
     */
    public static void base64ToFile(String base64, String filePath) {
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            Files.write(Paths.get(filePath), bytes);
        } catch (IOException e) {
            throw new RuntimeException("Base64转文件失败：" + filePath, e);
        }
    }

    /**
     * 流式处理大文件转Base64，避免内存溢出
     * @param srcFile      源文件路径
     * @param outBase64    输出Base64流（如接口直接返回流）
     */
    public static void fileToBase64Stream(String srcFile, OutputStream outBase64) {
        try (InputStream in = Files.newInputStream(Paths.get(srcFile));
             // base64Out是一个经过Base64编码的输出流，write时会自动把二进制转换为Base64字符并输出
             OutputStream base64Out = Base64.getEncoder().wrap(outBase64)) {
            byte[] buf = new byte[8192];
            int len;
            // 边读边写
            while ((len = in.read(buf)) != -1) {  // 输入流, 将内容读到buf
                base64Out.write(buf, 0, len);// 输出流, 将buf数据写出去
            }
        } catch (IOException e) {
            throw new RuntimeException("文件流转Base64失败：" + srcFile, e);
        }
    }

    /**
     * Base64流转文件，适合接收大文件
     * @param inBase64    输入Base64流
     * @param destFile    目标文件
     */
    public static void base64StreamToFile(InputStream inBase64, String destFile) {
        try (InputStream base64In = Base64.getDecoder().wrap(inBase64);
             OutputStream out = Files.newOutputStream(Paths.get(destFile))) {
            byte[] buf = new byte[8192];
            int len;
            while ((len = base64In.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
        } catch (IOException e) {
            throw new RuntimeException("Base64流转文件失败：" + destFile, e);
        }
    }
}

