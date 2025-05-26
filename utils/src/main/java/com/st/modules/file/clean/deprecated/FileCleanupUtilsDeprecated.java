package com.st.modules.file.clean.deprecated;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The utils has deprecated; The alternative ref  {@link com.st.modules.file.clean.FileCleanupManager}
 * <pre>
 * - file.deleteOnExit() 删除文件	✅ 支持
 * - file.deleteOnExit() 删除空目录	✅ 支持
 * - file.deleteOnExit() 删除非空目录	❌ 不支持（delete() 失败）
 * - 想清除整个目录	✅ 需要手动递归注册子文件或添加自定义 ShutdownHook
 * </pre>
 */
public final class FileCleanupUtilsDeprecated {

    // 使用线程安全的集合防止重复注册
    private static final Set<String> REGISTERED_PATHS = ConcurrentHashMap.newKeySet();

    private FileCleanupUtilsDeprecated() {
        // 工具类不允许实例化
    }

    /**
     * 注册文件，在 JVM 退出时清除，并在注册时打印日志
     * @param file 要注册的文件
     */
    public static void deleteDelay(File file) {
        if (file == null) {
            return;
        }

        String absolutePath = file.getAbsolutePath();

        // 避免重复注册
        if (REGISTERED_PATHS.add(absolutePath)) {
            System.out.println("[FileCleanup] Registered file for cleanup: " + absolutePath);
            file.deleteOnExit();
        }
    }

    /**
     * 可选功能：立即删除并打印
     * <pre>
     * - 调用此方法的前提: 先调用{@code }
     * </pre>
     * @param file 要立即删除的文件
     */
    public static void deleteNow(File file) {
        if (file == null) {
            return;
        }

        String absolutePath = file.getAbsolutePath();
        System.out.println("[FileCleanup] Deleting file immediately: " + absolutePath);
        if (!file.delete()) {
            System.err.println("[FileCleanup] Failed to delete file: " + absolutePath);
        } else {
            REGISTERED_PATHS.remove(absolutePath);
        }
    }
}
