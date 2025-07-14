package com.st.modules.file.ftpv2.client.sftp;

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

    public static void createDirectoryIfNotExists(ChannelSftp sftp, String path) throws SftpException {
        if (!directoryExists(sftp, path)) {
            sftp.mkdir(path);
        }
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

