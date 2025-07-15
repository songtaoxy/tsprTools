package com.st.modules.file.ftp.main;

import com.st.modules.file.classpath.ClassPathResourcesUtils;
import com.st.modules.file.ftp.client.base.GenericClosableFtpClient;
import com.st.modules.file.ftp.config.FtpClientConfig;
import com.st.modules.file.ftp.config.FtpConfigRegistry;
import com.st.modules.file.ftp.config.FtpYamlLoader;
import com.st.modules.file.ftp.constant.constant.FilePathConst;
import com.st.modules.file.ftp.constant.constant.FtpClientKeys;
import com.st.modules.file.ftp.constant.constant.FtpPathKeys;
import com.st.modules.file.ftp.provider.FtpClientProvider;
import com.st.modules.file.ftp.utils.closer.FtpCloser;
import com.st.modules.io.closer.IOCloser;
import lombok.SneakyThrows;

import java.io.*;
import java.util.Map;

public class Main {

    /**
     * 测试基本用法.
     *
     * <p></p>
     * 关闭资源(IO, ftp连接等)方式
     * <pre>
     *  - 手动关闭
     *  - try with resouce 关闭
     * </pre>
     * @param args
     * @throws IOException
     */
    @SneakyThrows
    public static void main(String[] args) throws IOException {
        Map<String, FtpClientConfig> configs = FtpYamlLoader.loadFromClasspath(FilePathConst.FTP_CONFIG_PATH_DEV);
        FtpConfigRegistry.init(configs);

        //test case: 自动关闭 try with resource自动关闭
        useWithTry(FtpClientKeys.FTP_A, FtpPathKeys.UPLOAD);
        //test case: 手动关闭
        useWithManualClose(FtpClientKeys.FTP_A, FtpPathKeys.UPLOAD);
    }

    /**
     * <pre>
     *  - 使用try with resources 关闭
     * </pre>
     * @param clientKey
     * @param pathKey
     * @throws IOException
     */
    @SneakyThrows
    private static void useWithTry(String clientKey, String pathKey) throws IOException {
        String path = FtpConfigRegistry.getPath(clientKey, pathKey);

        // 使用try with resources 关闭
        try (
                GenericClosableFtpClient client = FtpClientProvider.connect(clientKey);
                InputStream input = ClassPathResourcesUtils.getClasspathFile("ftp/test/local.txt");
                OutputStream output = new FileOutputStream("copy-try.txt")
        ) {
            client.upload(path, "file-try.txt", input);
            client.download(path, "file-try.txt", output);
        }
    }

    /**
     * <pre>
     * - 手动关闭
     * </pre>
     * @param clientKey
     * @param pathKey
     * @throws IOException
     */
    @SneakyThrows
    private static void useWithManualClose(String clientKey, String pathKey) throws IOException {
        String path = FtpConfigRegistry.getPath(clientKey, pathKey);
        GenericClosableFtpClient client = null;
        InputStream input = null;
        OutputStream output = null;

        try {
            client = FtpClientProvider.connect(clientKey);
            input = ClassPathResourcesUtils.getClasspathFile("ftp/test/local.txt");
            output = new FileOutputStream("copy-manual.txt");

            client.upload(path, "file-manual.txt", input);
            client.download(path, "file-manual.txt", output);
        } finally { //手动关闭
            IOCloser.closeAll(input,output);
            FtpCloser.closeQuietly(client);
        }
    }
}
