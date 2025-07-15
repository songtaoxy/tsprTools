package com.st.modules.file.ftp.client.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;

public final class FtpHelper {
    private FtpHelper() {}

    public static boolean fileExists(FTPClient ftp, String dir, String fileName) throws IOException {
        if (!ftp.changeWorkingDirectory(dir)) return false;
        FTPFile[] files = ftp.listFiles(fileName);
        return files != null && files.length > 0;
    }

    public static boolean directoryExists(FTPClient ftp, String dir) throws IOException {
        return ftp.changeWorkingDirectory(dir);
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

