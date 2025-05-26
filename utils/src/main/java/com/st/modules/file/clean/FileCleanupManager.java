package com.st.modules.file.clean;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;


/**
 * <pre>
 * JVM 退出自动清理: 使用 ShutdownHook 注册清理任务 & 手工清理
 * 有些框架（如 Apache Commons IO)会在JVM 退出前注册自定义ShutdownHook来执行彻底的清理逻辑
 * - 接收 File 文件或目录
 * - 清除前统一打印: JVM退出前统一打印所有路径, 方便查看
 * - 避免重复注册
 * - 最大保留文件数: 当注册文件超过指定上限时，自动触发最早注册的文件删除
 * - 定期清理: 通过后台线程定期清除已注册路径中的失效（已不存在）项
 * - 日志文件记录(控制台+文件）(可选）, : 将注册/删除等操作写入日志文件(追加式写入)，便于审计排查
 * - 线程安全: 全部使用 ConcurrentLinkedDeque 与 ConcurrentHashMap
 * - 第三方依赖: 无第三方依赖
 * </pre>
 *
 * how to use?
 * <pre>
 * public class FileCleanupExample {
 *     public static void main(String[] args) throws Exception {
 *         File tmpFile = File.createTempFile("example", ".txt");
 *         File tmpDir = new File(System.getProperty("java.io.tmpdir"), "exampleDir");
 *         tmpDir.mkdir();
 *         File nestedFile = new File(tmpDir, "inner.txt");
 *         nestedFile.createNewFile();
 *
 *         // 注册文件和目录
 *         FileCleanupManager.register(tmpFile);
 *         FileCleanupManager.register(tmpDir);
 *
 *         // 可选立即删除
 *         // FileCleanupManager.deleteNow(tmpFile);
 *     }
 * }
 * </pre>
 *
 * unit Test
 * <pre>
 * ref {@code com.st.modules.file.clean.FileCleanupManagerTest}
 * testRegisterAndShutdownHook()	注册文件并模拟 JVM 退出时是否删除
 * testDeleteNow()	立即删除功能
 * testRegisterDirectory()	支持目录递归注册与删除
 * testMaxFileLimitAutoDelete()	超过最大文件数限制时自动删除最早注册
 * testCleanupInvalidPath()	定期清理无效路径是否生效
 * testRegisterSameFileTwice()	防止重复注册功能
 * testLogToFile()	日志记录到文件功能
 * </pre>
 */
public final class FileCleanupManager {

    private static final ConcurrentLinkedDeque<String> PATH_QUEUE = new ConcurrentLinkedDeque<>();
    // set, 避免重复注册 & 线程安全
    // 用于记录已注册路径，避免重复注册（路径统一为绝对路径）
    private static final Set<String> PATH_SET = ConcurrentHashMap.newKeySet();
    // 通过后台线程定期清除已注册路径中的失效（已不存在）项
    private static final ScheduledExecutorService CLEANER = Executors.newSingleThreadScheduledExecutor(
            r -> {
                Thread t = new Thread(r, "FileCleanupScheduler");
                t.setDaemon(true);
                return t;
            });

    static {
        // 注册 shutdown hook（只注册一次）
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log("[FileCleanup] JVM shutdown hook triggered.");
            log("[FileCleanup] Registered paths:");
            // 清除前统一打印
            for (String path : PATH_SET) {
                log(" - " + path);
            }
            for (String path : PATH_SET) {
                File file = new File(path);
                if (deleteRecursively(file, true)) {
                    // 删除成功后移除注册表, 保证状态一致性
                    PATH_SET.remove(path);
                    PATH_QUEUE.remove(path);
                }
            }
        }, "FileCleanupShutdownHook"));

        // 启动定期清理任务（清除不存在的路径）
        CLEANER.scheduleAtFixedRate(() -> {
            for (String path : new ArrayList<>(PATH_SET)) {
                File f = new File(path);
                if (!f.exists()) {
                    PATH_SET.remove(path);
                    PATH_QUEUE.remove(path);
                    log("[FileCleanup] Removed invalid path (not exists): " + path);
                }
            }
        }, FileCleanupConfig.cleanupIntervalMillis, FileCleanupConfig.cleanupIntervalMillis, TimeUnit.MILLISECONDS);
    }

    // 工具类不允许实例化
    private FileCleanupManager() {}

    /**
     * 注册一个文件或目录路径，用于 JVM 退出时删除。
     */
    public static void register(String path) {
        if (path == null || path.trim().isEmpty()) {
            log("Ignored empty path for registration.");
            return;
        }
        register(new File(path));
    }

    /**
     * <pre>
     *  - 可以独立被调用
     *  - 可以被{@code com.st.modules.file.clean.FileCleanupManager#register(java.lang.String)}调用
     * </pre>
     * @param file
     */
    public static void register(File file) {
        if (file == null) return;
        String absPath = file.getAbsolutePath();

        if (PATH_SET.add(absPath)) {
            PATH_QUEUE.addLast(absPath);
            log("[FileCleanup] Registered for cleanup: " + absPath);

            if (FileCleanupConfig.maxFiles > 0 && PATH_SET.size() > FileCleanupConfig.maxFiles) {
                // 超出最大数量，清理最旧文件
                String oldPath = PATH_QUEUE.pollFirst();
                if (oldPath != null) {
                    File oldFile = new File(oldPath);

                    //用于 JVM 退出时的 Shutdown Hook 清理; 只在 delete 失败时打印错误; 减少 JVM 退出时的控制台干扰
//                    deleteRecursively(oldFile, true);
                    boolean deleted = deleteRecursively(oldFile, false);
                    if (deleted) {
                        PATH_SET.remove(oldPath);
                        log("[FileCleanup] Auto-deleted due to maxFiles limit: " + oldPath);
                    } else {
                        // 放回队尾，避免丢失
                        PATH_QUEUE.addFirst(oldPath);
                        PATH_SET.add(oldPath);
                        log("[FileCleanup] Failed to delete (maxFiles cleanup), preserved: " + oldPath);
                    }
                }
            }
        }
    }


    /**
     * 立即删除一个文件或目录路径。
     */
    public static void deleteNow(String path) {
        if (path == null || path.trim().isEmpty()) {
            log("Ignored empty path for immediate deletion.");
            return;
        }
        deleteNow(new File(path));
    }

    /**
     * <pre>
     * - 支持立即删除; 测试时使用
     * - 前提: 先调用 {@code com.st.modules.file.clean.FileCleanupManager#register(java.io.File)}
     *
     * - 可以被独立调用
     * - 可以被{@code com.st.modules.file.clean.FileCleanupManager#deleteNow(java.lang.String) }调用
     * </pre>
     * @param file
     */
    public static void deleteNow(File file) {
        if (file == null || !file.exists()) return;
        String absPath = file.getAbsolutePath();
        log("[FileCleanup] Deleting now: " + absPath);
        // 手动调用 deleteNow; 每个文件/目录删除结果都打印; 方便调试和手动清理时观察
        boolean deleted = deleteRecursively(file, false);
        if (deleted) {
            PATH_SET.remove(absPath);
            PATH_QUEUE.remove(absPath);
        }
    }

    /**
     * 可接收文件或目录: 如果是目录, 则递归删除
     * @param file
     * @param quiet
     * @return
     */
    private static boolean deleteRecursively(File file, boolean quiet) {
        if (!file.exists()) return true;
        boolean success = true;

        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    success &= deleteRecursively(child, quiet);
                }
            }
        }

        boolean deleted = file.delete();
        success &= deleted;

        if (!quiet) {
            if (deleted) log("[FileCleanup] Deleted: " + file.getAbsolutePath());
            else log("[FileCleanup] Failed to delete: " + file.getAbsolutePath());
        } else if (!deleted) {
            log("[FileCleanup] Failed to delete (shutdown): " + file.getAbsolutePath());
        }

        return success;
    }


    /**
     * <pre>
     * 日志文件记录（可选）: 将注册/删除等操作写入日志文件，便于审计排查
     * </pre>
     * @param msg
     */
    private static void log(String msg) {
        String line = "[" + new Date() + "] " + msg;
        System.out.println(line);
        if (FileCleanupConfig.logFile != null) {
            try (FileWriter fw = new FileWriter(FileCleanupConfig.logFile, true);
                 BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(line);
                bw.newLine();
            } catch (IOException e) {
                System.err.println("[FileCleanup] Failed to write log file: " + e.getMessage());
            }
        }
    }
}
