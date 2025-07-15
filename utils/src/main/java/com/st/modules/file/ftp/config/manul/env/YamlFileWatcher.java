package com.st.modules.file.ftp.config.manul.env;

import java.nio.file.*;

public class YamlFileWatcher {
    public static void watch(Path filePath, Runnable onChange) {
        Thread thread = new Thread(() -> {
            try {
                WatchService watcher = FileSystems.getDefault().newWatchService();
                Path dir = filePath.getParent();
                String filename = filePath.getFileName().toString();
                dir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
                while (true) {
                    WatchKey key = watcher.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY
                                && filename.equals(event.context().toString())) {
                            onChange.run();
                        }
                    }
                    key.reset();
                }
            } catch (Exception e) {
                // 可加入日志
            }
        });
        thread.setDaemon(true);
        thread.setName("FtpConfigWatcher");
        thread.start();
    }
}

