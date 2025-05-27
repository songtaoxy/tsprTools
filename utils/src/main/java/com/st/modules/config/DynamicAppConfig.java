package com.st.modules.config;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 一, 使用配置类时延迟加载: 支持动态刷新、配置变更监听、环境热切换
 * <pre>
 * - 配置加载为不可变Map，并支持文件内容变化自动刷新。
 * - 支持环境切换：可手动指定appEnv，或调用API动态切换。
 * - 文件变动监听：利用WatchService监控resources目录下配置文件变更，实时reload。
 * - 线程安全：用volatile和ConcurrentHashMap实现并发安全。
 * - 无需三方依赖，全部用JDK标准库（JDK 7+）
 * </pre>
 *
 * 二, 注意:
 * <pre>
 * - 生产环境建议用绝对路径或挂载目录（如K8s ConfigMap）以保证配置变更可以触发文件监听
 * - 如需监听多文件、多环境可扩展Watcher逻辑
 * - 若资源文件在Jar包内部无法直接监听，推荐在启动脚本中将配置解压到独立目录，再用本地文件监听
 * - 切换环境和自动刷新完全线程安全，所有读取操作不阻塞
 * </pre>
 *
 * 三, Usage
 * <pre>
 * {@code
 * public class MainTest {
 *     public static void main(String[] args) throws Exception {
 *         // 获取配置
 *         System.out.println(DynamicAppConfig.get("db.url"));
 *         // 获取全部
 *         System.out.println(DynamicAppConfig.getAll());
 *         // 切换环境
 *         DynamicAppConfig.switchEnv("prod");
 *         System.out.println(DynamicAppConfig.get("db.url"));
 *         // 修改配置文件后（手动保存），get方法实时反映最新值
 *         Thread.sleep(10000);
 *         System.out.println(DynamicAppConfig.get("db.url"));
 *     }
 * }
 * }
 * </pre>
 *
 * 四, test, ref {@code com.st.modules.config.DynamicAppConfigTest}
 */
@Slf4j
public class DynamicAppConfig {

    // 配置缓存（线程安全，支持动态刷新）
    private static volatile Map<String, String> CONFIG_MAP = new ConcurrentHashMap<>();
    private static volatile String CURRENT_ENV = null;
    private static volatile Path CONFIG_PATH = null;
    private static Thread watchThread = null;

    // 初始化
    static {
        String env = getActiveEnv();
        loadConfig(env);
        startWatcher(env);
        log.info(CONFIG_MAP.toString());
    }

    // 获取当前环境
    public static String getActiveEnv() {
        // 优先读取JVM参数；也可加上System.getenv支持
        String env = System.getProperty("appEnv");
        if (env == null) env = "dev";
        return env.toLowerCase();
    }

    // 加载配置文件
    private static void loadConfig(String env) {
        String fileName = "app_" + env + ".properties";
        Properties props = new Properties();
        try (InputStream in = DynamicAppConfig.class.getClassLoader().getResourceAsStream(fileName)) {
            if (in == null) throw new RuntimeException("未找到配置文件: " + fileName);
            props.load(in);
        } catch (Exception e) {
            throw new RuntimeException("加载配置文件失败: " + fileName, e);
        }
        Map<String, String> map = new ConcurrentHashMap<>();
        for (String key : props.stringPropertyNames()) {
            map.put(key, props.getProperty(key));
        }
        CONFIG_MAP = Collections.unmodifiableMap(map);
        CURRENT_ENV = env;
        // 记录配置文件实际路径
        try {
            CONFIG_PATH = Paths.get(Objects.requireNonNull(DynamicAppConfig.class.getClassLoader().getResource(fileName)).toURI());
        } catch (Exception e) {
            CONFIG_PATH = null;
        }
    }

    // 获取配置
    public static String get(String key) {
        return CONFIG_MAP.get(key);
    }

    public static String get(String key, String def) {
        String v = CONFIG_MAP.get(key);
        return v == null ? def : v;
    }

    public static Map<String, String> getAll() {
        return CONFIG_MAP;
    }

    public static String getEnv() {
        return CURRENT_ENV;
    }

    // 切换环境（线程安全，手动调用切换）
    public static synchronized void switchEnv(String env) {
        loadConfig(env);
        restartWatcher(env);
    }

    //

    /**
     * 热加载：监控文件变化自动刷新
     * <pre>
     * - WatchService 是 Java NIO 包 (java.nio.file.WatchService) 的一个服务，用来监听文件系统变化（如文件被修改、创建、删除）
     * - watchService.take() 是 WatchService 的阻塞式获取事件的方法
     * - watchService.take() 就是“等着文件夹有变化，一有变化就立刻唤醒继续往下执行; 适合做文件变更自动触发的任务
     * - take() 方法与 poll() 区别：poll() 不阻塞，立刻返回，没事件返回 null；take() 必须等到有事件才返回
     * - WatchKey key = watchService.take(); 阻塞，直到有事件发生: 一个监听器，等着文件夹里有变化”。take() 就是“死等，有变化就醒来”
     *
     * </pre>
     * @param env
     */
    private static void startWatcher(String env) {
        if (watchThread != null && watchThread.isAlive()) return;
        watchThread = new Thread(() -> {
            try {
                if (CONFIG_PATH == null) return;
                Path dir = CONFIG_PATH.getParent();
                String fileName = CONFIG_PATH.getFileName().toString();
                WatchService watchService = FileSystems.getDefault().newWatchService();
                dir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
                while (true) {
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();
                        Path changed = (Path) event.context();
                        if (kind == StandardWatchEventKinds.ENTRY_MODIFY && changed.toString().equals(fileName)) {
                            loadConfig(env); // 刷新配置
                        }
                    }
                    // 重置监听，准备下次事件
                    key.reset();
                }
            } catch (Exception e) {
                // ignore
            }
        }, "ConfigFileWatcher");
        watchThread.setDaemon(true);
        watchThread.start();
    }

    // 重新监控
    private static void restartWatcher(String env) {
        if (watchThread != null && watchThread.isAlive()) {
            watchThread.interrupt();
        }
        startWatcher(env);
    }
}
