package com.st.modules.file.ftp.client.ftp.deprecared;

import com.st.modules.file.ftp.client.ftp.helpers.FtpExistHelper;
import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;

/**

 */
public final class FtpHelper {
    private FtpHelper() {}

    public static boolean fileExists(FTPClient ftp, String filePath) throws IOException {
        return FtpExistHelper.fileExists(ftp, filePath);
    }

    public static boolean directoryExists(FTPClient ftp, String dir) throws IOException {
        return FtpExistHelper.directoryExists(ftp, dir);
    }

    public static void createDirectoryIfNotExists(FTPClient ftp, String dir) throws IOException {
        if (!directoryExists(ftp, dir)) {
            ftp.makeDirectory(dir);
        }
    }

    public static void ensureRecursiveDirectory(FTPClient ftp, String dir) throws IOException {
        String[] segments = dir.split("/");
        StringBuilder path = new StringBuilder();
        for (String segment : segments) {
            if (segment.isEmpty()) continue;
            path.append("/").append(segment);
            ftp.makeDirectory(path.toString()); // 忽略失败（如已存在）
        }
    }
}

