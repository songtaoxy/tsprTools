package com.st.modules.file.clean;

import com.st.modules.constant.FileConst;
import com.st.modules.file.FileUtils;

import java.io.File;
import java.io.IOException;

public final class FileCleanupConfig {
    // 最大保留文件数: 当注册文件超过指定上限时，自动触发最早注册的文件删除
    public static int maxFiles = 1000; // 0 表示无限制
    // 通过后台线程定期清除已注册路径中的失效（已不存在）项
    public static long cleanupIntervalMillis = 60 * 60 * 1000; // 定期清理间隔（毫秒）
    // 日志文件记录（可选）: 将注册/删除等操作写入日志文件，便于审计排查

    // 日志文件（为 null 表示仅控制台输出）
    // public static File logFile = null;
    public static File logFile;

    static {
        try {
            logFile = FileUtils.createFileIfNotExists(FileConst.appDir+"/var/log/jvm_cleanFile_log.log");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

