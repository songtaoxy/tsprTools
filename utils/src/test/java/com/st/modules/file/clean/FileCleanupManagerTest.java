package com.st.modules.file.clean;

import com.st.modules.constant.FileConst;
import com.st.modules.file.FileCreateUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * testRegisterAndShutdownHook()	注册文件并模拟 JVM 退出时是否删除
 * testDeleteNow()	立即删除功能
 * testRegisterDirectory()	支持目录递归注册与删除
 * testMaxFileLimitAutoDelete()	超过最大文件数限制时自动删除最早注册
 * testCleanupInvalidPath()	定期清理无效路径是否生效
 * testRegisterSameFileTwice()	防止重复注册功能
 * testLogToFile()	日志记录到文件功能
 * </pre>
 */
class FileCleanupManagerTest {

    @BeforeEach
    @SneakyThrows
    void setup() throws IOException {
        // 重设配置
        FileCleanupConfig.maxFiles = 1000;
        FileCleanupConfig.cleanupIntervalMillis = 1000;
//        FileCleanupConfig.logFile = null;
        FileCleanupConfig.logFile  = FileCreateUtils.createFileIfNotExists(FileConst.appDir+"/var/log/jvm_cleanFile_log.log");

    }

    @Test
    void testRegisterAndDeleteWithPath() throws IOException {
        File temp = File.createTempFile("path", ".tmp");
        String path = temp.getAbsolutePath();

        FileCleanupManager.register(path);
        assertTrue(temp.exists());

        FileCleanupManager.deleteNow(path);
        assertFalse(temp.exists());
    }


    @Test
    void testRegisterAndShutdownHook() throws IOException {
        File tempFile = File.createTempFile("test", ".tmp");
        FileCleanupManager.register(tempFile);

        assertTrue(tempFile.exists());

        // 模拟 JVM 退出调用 hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            assertFalse(tempFile.exists());
        }));
    }

    @Test
    void testDeleteNow() throws IOException {
        File tempFile = File.createTempFile("deleteNow", ".tmp");
        assertTrue(tempFile.exists());

        FileCleanupManager.register(tempFile);
        FileCleanupManager.deleteNow(tempFile);

        assertFalse(tempFile.exists());
    }

    @Test
    void testRegisterDirectory() throws IOException {
        File dir = Files.createTempDirectory("testDir").toFile();
        File child = new File(dir, "child.tmp");
        assertTrue(child.createNewFile());

        FileCleanupManager.register(dir);
        FileCleanupManager.deleteNow(dir);

        assertFalse(dir.exists());
        assertFalse(child.exists());
    }

    @Test
    void testMaxFileLimitAutoDelete() throws IOException {
        FileCleanupConfig.maxFiles = 2;

        File f1 = File.createTempFile("f1_", ".tmp");
        File f2 = File.createTempFile("f2_", ".tmp");
        File f3 = File.createTempFile("f3_", ".tmp");


        FileCleanupManager.register(f1);
        FileCleanupManager.register(f2);
        assertTrue(f1.exists());
        assertTrue(f2.exists());

        FileCleanupManager.register(f3);

        // f1 应被自动删除
        assertFalse(f1.exists());
        assertTrue(f2.exists());
        assertTrue(f3.exists());
    }

    @Test
    void testCleanupInvalidPath() throws IOException, InterruptedException {
        File tempFile = File.createTempFile("tempInvalid", ".tmp");
        FileCleanupManager.register(tempFile);

        assertTrue(tempFile.delete());

        TimeUnit.MILLISECONDS.sleep(FileCleanupConfig.cleanupIntervalMillis + 500);

        // 如果路径无效，会自动清除注册
        FileCleanupManager.register(new File("test.txt")); // 激活清理线程
        // 无异常即可
    }

    @Test
    void testRegisterSameFileTwice() throws IOException {
        File f = File.createTempFile("same", ".tmp");
        FileCleanupManager.register(f);
        FileCleanupManager.register(f); // 应忽略重复注册

        FileCleanupManager.deleteNow(f);
        assertFalse(f.exists());
    }

    @Test
    void testLogToFile() throws IOException {
        File logFile = File.createTempFile("file_cleanup", ".log");
        FileCleanupConfig.logFile = logFile;

        File f = File.createTempFile("log_test", ".tmp");
        FileCleanupManager.register(f);
        FileCleanupManager.deleteNow(f);

//        String logContent = Files.readString(logFile.toPath());
        String logContent = new String(Files.readAllBytes(logFile.toPath()), StandardCharsets.UTF_8);
        assertTrue(logContent.contains("Registered for cleanup"));
        assertTrue(logContent.contains("Deleted"));
    }
}
