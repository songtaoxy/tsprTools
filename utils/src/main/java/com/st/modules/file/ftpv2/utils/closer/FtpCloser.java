package com.st.modules.file.ftpv2.utils.closer;

import com.st.modules.file.ftpv2.client.base.GenericClosableFtpClient;

public class FtpCloser {
    public static void closeQuietly(GenericClosableFtpClient client) {
        if (client == null) return;
        try {
            client.disconnect();
        } catch (Exception ignored) {
        }
    }
}
