package com.st.modules.file.ftp.client.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

public final class SftpHelper {
    private SftpHelper() {}

    public static boolean fileExists(ChannelSftp sftp, String path) {
        try {
            SftpATTRS attrs = sftp.stat(path);
            return !attrs.isDir();
        } catch (SftpException e) {
            return false;
        }
    }

    public static boolean directoryExists(ChannelSftp sftp, String path) {
        try {
            return sftp.stat(path).isDir();
        } catch (SftpException e) {
            return false;
        }
    }

    public static boolean createDirectoryIfNotExists(ChannelSftp sftp, String dirPath) throws SftpException {

        if (dirPath == null || dirPath.trim().isEmpty()) return false;

        dirPath = dirPath.replaceAll("[/\\\\]+", "/");
        if (!dirPath.startsWith("/")) dirPath = "/" + dirPath;

        String[] folders = dirPath.split("/");
        String currentPath = "";
        for (String folder : folders) {
            if (folder.isEmpty()) continue;
            currentPath += "/" + folder;
            try {
                sftp.cd(currentPath);
            } catch (Exception e) {
                try {
                    sftp.mkdir(currentPath);
                } catch (Exception mkdirEx) {
                    return false;
                }
            }
        }
        return true;

    }

    public static void ensureRecursiveDirectory(ChannelSftp sftp, String dir) throws SftpException {
        String[] parts = dir.split("/");
        String current = "";
        for (String part : parts) {
            if (part.isEmpty()) continue;
            current += "/" + part;
            try {
                sftp.stat(current);
            } catch (SftpException e) {
                sftp.mkdir(current);
            }
        }
    }
}

