package com.st.modules.file.ftpv2.main;

import com.st.modules.file.ftpv2.client.base.GenericClosableFtpClient;
import com.st.modules.file.ftpv2.config.FtpClientConfig;
import com.st.modules.file.ftpv2.config.FtpConfigRegistry;
import com.st.modules.file.ftpv2.config.FtpYamlLoader;
import com.st.modules.file.ftpv2.provider.FtpClientProvider;
import com.st.modules.file.ftpv2.utils.closer.FtpCloser;
import lombok.SneakyThrows;

import java.io.*;
import java.util.Map;

public class Main {

    @SneakyThrows
    public static void main(String[] args) throws IOException {
        String yamlPath = "ftp" + File.separator + "ftp-config.yaml";
        Map<String, FtpClientConfig> configs = FtpYamlLoader.loadFromClasspath(yamlPath);
        FtpConfigRegistry.init(configs);

        GenericClosableFtpClient client = FtpClientProvider.connect("ftpA");
        String path = FtpConfigRegistry.getPath("ftpA", "upload");


        try {
            String testFilePath = "ftp" + File.separator + "test" + File.separator + "local.txt";
            InputStream input = Main.class.getClassLoader().getResourceAsStream(testFilePath);
            client.upload(path, "file.txt", input);
            client.download(path, "file.txt", new FileOutputStream("copy.txt"));
        } finally {
            FtpCloser.closeQuietly(client);
        }
    }

}
