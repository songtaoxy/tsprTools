# Stopwatch 如何获取开始时间, 截止时间

### Stopwatch 获取开始时间与截止时间方式概述  

在 Java 中常用的计时手段主要有两类：  
1. 原生 API（`System.currentTimeMillis`、`System.nanoTime`）  
2. 第三方工具（如 Guava 的 `Stopwatch`）  

下面我给你分别说明：  

---

### 1. 使用 Guava `Stopwatch`  
Guava 提供了一个简单的 `Stopwatch` 类，非常适合计时。  

```java
import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

public class StopwatchDemo {
    public static void main(String[] args) throws InterruptedException {
        // 创建并启动
        Stopwatch stopwatch = Stopwatch.createStarted();

        // 记录开始时间
        long startTime = System.currentTimeMillis();

        Thread.sleep(1500); // 模拟耗时操作

        // 停止计时
        stopwatch.stop();

        // 记录截止时间
        long endTime = System.currentTimeMillis();

        System.out.println("开始时间: " + startTime);
        System.out.println("截止时间: " + endTime);

        // 获取耗时
        System.out.println("耗时: " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms");
    }
}
```

#### 输出示例
```
开始时间: 1724828400000
截止时间: 1724828401501
耗时: 1501 ms
```

---

### 2. 使用原生 API（不依赖 Guava）
```java
public class NativeStopwatchDemo {
    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis();

        Thread.sleep(1200); // 模拟任务

        long endTime = System.currentTimeMillis();

        System.out.println("开始时间: " + startTime);
        System.out.println("截止时间: " + endTime);
        System.out.println("耗时: " + (endTime - startTime) + " ms");
    }
}
```

---

### 3. 小结
- 如果你需要简单记录 **开始时间/截止时间**，直接用 `System.currentTimeMillis()` 就够。  
- 如果你需要频繁计时、可读性更高，推荐用 Guava `Stopwatch`。  
- `Stopwatch` 本身没有直接的“开始时间”和“截止时间”字段，它更关注 **耗时**；你要获取时间戳，需要额外用 `System.currentTimeMillis()` 记录。  

---

要不要我帮你写一个 **封装好的工具类**，让你能直接 `StopwatchUtil.start()` → `StopwatchUtil.stop()` 就能同时获取开始时间、截止时间和耗时？

# 工具类

### Stopwatch 封装工具类

下面给出一个封装好的工具类 `StopwatchUtil`，它结合了 Guava `Stopwatch` 和原生 `System.currentTimeMillis`，可以同时获取 **开始时间、截止时间和耗时**。

```java
import com.google.common.base.Stopwatch;
import java.util.concurrent.TimeUnit;

/**
 * Stopwatch 工具类
 * 功能:
 * 1. 记录开始时间与截止时间
 * 2. 提供耗时统计（毫秒、秒、纳秒）
 * 3. 可多次启动与停止
 *
 * 使用示例:
 *   StopwatchUtil sw = StopwatchUtil.start();
 *   // do something
 *   sw.stop();
 *   System.out.println("开始时间: " + sw.getStartTime());
 *   System.out.println("截止时间: " + sw.getEndTime());
 *   System.out.println("耗时(ms): " + sw.elapsedMillis());
 */
public class StopwatchUtil {

    private final Stopwatch stopwatch;
    private long startTimeMillis;
    private long endTimeMillis;

    private StopwatchUtil() {
        this.stopwatch = Stopwatch.createUnstarted();
    }

    /** 开始计时 */
    public static StopwatchUtil start() {
        StopwatchUtil util = new StopwatchUtil();
        util.startTimeMillis = System.currentTimeMillis();
        util.stopwatch.start();
        return util;
    }

    /** 停止计时 */
    public void stop() {
        this.stopwatch.stop();
        this.endTimeMillis = System.currentTimeMillis();
    }

    /** 获取开始时间戳 (毫秒) */
    public long getStartTime() {
        return startTimeMillis;
    }

    /** 获取截止时间戳 (毫秒) */
    public long getEndTime() {
        return endTimeMillis;
    }

    /** 获取耗时 (毫秒) */
    public long elapsedMillis() {
        return stopwatch.elapsed(TimeUnit.MILLISECONDS);
    }

    /** 获取耗时 (秒) */
    public long elapsedSeconds() {
        return stopwatch.elapsed(TimeUnit.SECONDS);
    }

    /** 获取耗时 (纳秒) */
    public long elapsedNanos() {
        return stopwatch.elapsed(TimeUnit.NANOSECONDS);
    }
}
```

------

### 使用示例

```java
public class TestStopwatchUtil {
    public static void main(String[] args) throws InterruptedException {
        StopwatchUtil sw = StopwatchUtil.start();

        Thread.sleep(1350); // 模拟耗时任务

        sw.stop();

        System.out.println("开始时间: " + sw.getStartTime());
        System.out.println("截止时间: " + sw.getEndTime());
        System.out.println("耗时(ms): " + sw.elapsedMillis());
        System.out.println("耗时(s): " + sw.elapsedSeconds());
    }
}
```

#### 输出示例

```
开始时间: 1724829000000
截止时间: 1724829001351
耗时(ms): 1351
耗时(s): 1
```

------

要不要我帮你扩展一下这个工具类，加上 **自动打印日志功能**（例如 `StopwatchUtil.print("任务X")` 自动打印开始时间、结束时间和耗时）？这样用起来更方便，适合在项目里直接嵌日志。

## 优化: 开始时间, 截止 格式化 yyyy-MM-dd HH:MM:ss SSS

好的，我来帮你把 `StopwatchUtil` 优化一下，支持把 **开始时间、截止时间** 格式化成 `yyyy-MM-dd HH:mm:ss SSS`。  

注意：`SimpleDateFormat` 中  
- `HH` 表示 24 小时制的小时  
- `mm` 表示分钟  
- `ss` 表示秒  
- `SSS` 表示毫秒  
如果写成 `HH:MM:ss` 就会错了（`MM` 是月份）。  

---

### 优化版 StopwatchUtil

```java
import com.google.common.base.Stopwatch;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Stopwatch 工具类
 * 功能:
 * 1. 记录开始时间与截止时间 (支持格式化字符串)
 * 2. 提供耗时统计（毫秒、秒、纳秒）
 * 3. 可多次启动与停止
 */
public class StopwatchUtil {

    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss SSS";
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat(DEFAULT_PATTERN);

    private final Stopwatch stopwatch;
    private long startTimeMillis;
    private long endTimeMillis;

    private StopwatchUtil() {
        this.stopwatch = Stopwatch.createUnstarted();
    }

    /** 开始计时 */
    public static StopwatchUtil start() {
        StopwatchUtil util = new StopwatchUtil();
        util.startTimeMillis = System.currentTimeMillis();
        util.stopwatch.start();
        return util;
    }

    /** 停止计时 */
    public void stop() {
        this.stopwatch.stop();
        this.endTimeMillis = System.currentTimeMillis();
    }

    /** 获取开始时间戳 (毫秒) */
    public long getStartTime() {
        return startTimeMillis;
    }

    /** 获取截止时间戳 (毫秒) */
    public long getEndTime() {
        return endTimeMillis;
    }

    /** 获取格式化的开始时间 */
    public String getStartTimeFormatted() {
        return FORMATTER.format(new Date(startTimeMillis));
    }

    /** 获取格式化的截止时间 */
    public String getEndTimeFormatted() {
        return FORMATTER.format(new Date(endTimeMillis));
    }

    /** 获取耗时 (毫秒) */
    public long elapsedMillis() {
        return stopwatch.elapsed(TimeUnit.MILLISECONDS);
    }

    /** 获取耗时 (秒) */
    public long elapsedSeconds() {
        return stopwatch.elapsed(TimeUnit.SECONDS);
    }

    /** 获取耗时 (纳秒) */
    public long elapsedNanos() {
        return stopwatch.elapsed(TimeUnit.NANOSECONDS);
    }
}
```

---

### 使用示例

```java
public class TestStopwatchUtil {
    public static void main(String[] args) throws InterruptedException {
        StopwatchUtil sw = StopwatchUtil.start();

        Thread.sleep(1250); // 模拟耗时任务

        sw.stop();

        System.out.println("开始时间: " + sw.getStartTimeFormatted());
        System.out.println("截止时间: " + sw.getEndTimeFormatted());
        System.out.println("耗时(ms): " + sw.elapsedMillis());
    }
}
```

#### 输出示例
```
开始时间: 2025-08-28 13:05:12 123
截止时间: 2025-08-28 13:05:13 374
耗时(ms): 1251
```

---

要不要我再帮你加一个 **自动打印工具方法**，比如 `sw.print("任务名称")` 就能直接把格式化的开始/截止时间和耗时一起输出？

