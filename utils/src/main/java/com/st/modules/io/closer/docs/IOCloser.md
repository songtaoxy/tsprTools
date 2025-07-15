# 问题

input, output关闭, 封装成独立的、通用的工具类

# 基本

下面是将 `InputStream` 与 `OutputStream` 的关闭逻辑封装为**独立的通用工具类 `IOCloser`**，与 `FtpCloser` 职责分离，专注处理 Java IO 资源的关闭，统一处理异常、避免冗余 `try-catch`。

------

### 一、工具类定义

```java
package com.st.modules.file.common.util;

import java.io.Closeable;
import java.io.IOException;

public final class IOCloser {

    private IOCloser() {}

    public static void closeQuietly(Closeable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (IOException ignored) {
            // 可加日志记录，如 log.warn("关闭流失败", ignored);
        }
    }

    public static void closeQuietly(Closeable... closeables) {
        if (closeables == null) return;
        for (Closeable c : closeables) {
            closeQuietly(c);
        }
    }
}
```

------

### 二、使用示例：在手动关闭中使用

```java
finally {
    IOCloser.closeQuietly(input, output);
    FtpCloser.closeQuietly(client);
}
```

------

### 三、与 `FtpCloser` 区分职责

| 工具类      | 适用对象                                             | 是否标准 Java 接口 | 说明                       |
| ----------- | ---------------------------------------------------- | ------------------ | -------------------------- |
| `IOCloser`  | `InputStream` / `OutputStream` / `Reader` / `Writer` | 是 (`Closeable`)   | 所有基于 IO 的流资源       |
| `FtpCloser` | 自定义 `ClosableFtpClient` 或 FTP 客户端连接对象     | 否（自定义类型）   | 封装 FTP/SFTP 连接关闭逻辑 |

------

### 四、是否建议整合？

不建议合并。两类资源的生命周期、异常处理关注点不同，合并反而降低可读性与职责清晰度。

------

如你希望将 `FtpCloser` 也基于 `Closeable` 接口统一封装，可扩展为：

```java
public static void closeQuietly(Object obj) {
    if (obj instanceof Closeable) {
        closeQuietly((Closeable) obj);
    } else if (obj instanceof ClosableFtpClient) {
        ((ClosableFtpClient) obj).close();
    }
}
```

是否需要我将 `FtpCloser` 与 `IOCloser` 提供一个统一接口层，以便统一资源管理？

# 不要吞异常, 上抛

问题: 上面的代码吞了异常

```java

public static void closeQuietly(Closeable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (IOException ignored) {
            // 可加日志记录，如 log.warn("关闭流失败", ignored);
        }
    } 
```

你提出“不吞异常，并上抛”的要求是非常合理的，特别在关键资源如文件写入、网络传输时，异常信息对于故障定位极为重要。

下面是修改后的 `IOCloser` 工具类，实现逻辑如下：

- 不再吞掉 `IOException`；
- 使用自定义异常 `IOCloseException` 包装原始异常并上抛；
- 方法签名中声明 `throws`，要求调用方显式处理。

------

### 一、自定义异常类 `IOCloseException`

```java
package com.st.modules.file.common.exception;

import java.io.IOException;

public class IOCloseException extends RuntimeException {
    public IOCloseException(String message, IOException cause) {
        super(message, cause);
    }
}
```

------

### 二、修改后的 `IOCloser` 工具类

```java
package com.st.modules.file.common.util;

import com.st.modules.file.common.exception.IOCloseException;

import java.io.Closeable;
import java.io.IOException;

public final class IOCloser {

    private IOCloser() {}

    public static void close(Closeable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (IOException e) {
            throw new IOCloseException("关闭资源失败: " + closeable.getClass().getName(), e);
        }
    }

    public static void closeAll(Closeable... closeables) {
        if (closeables == null) return;
        for (Closeable c : closeables) {
            close(c);
        }
    }
}
```

------

### 三、使用方式示例

```java
finally {
    IOCloser.closeAll(input, output);
    FtpCloser.close(client);
}
```

调用方将感知 `IOCloseException`，不再丢失堆栈与异常根因。

------

### 四、是否保留 `closeQuietly`？

可选：

- 若你项目中部分调用场景确实需要吞异常（如清理缓存文件、临时日志），则仍可保留 `closeQuietly`；
- 否则，为统一异常行为，建议直接替换为显式抛出版本。

是否需要我扩展该工具类支持 `Reader`、`Writer`、`InputStreamReader` 等类型资源？

# 扩展: 支持 `Reader`、`Writer`、`InputStreamReader` 等类型资源

扩展该工具类支持 Reader、Writer、InputStreamReader 等类型资源

以下是扩展后的 `IOCloser` 工具类，支持关闭 Java IO 相关所有常见类型资源，包括：

- `InputStream`、`OutputStream`
- `Reader`、`Writer`
- `InputStreamReader`、`OutputStreamWriter`
- 及任何实现了 `java.io.Closeable` 的类

并明确：

- 不再吞异常；
- 封装成统一的 `IOCloseException` 上抛；
- 所有资源类型皆通过 `Closeable` 统一处理。

------

### 一、自定义异常（已定义）

```java
public class IOCloseException extends RuntimeException {
    public IOCloseException(String message, IOException cause) {
        super(message, cause);
    }
}
```

------

### 二、扩展后的 `IOCloser` 工具类

```java
package com.st.modules.file.common.util;

import com.st.modules.file.common.exception.IOCloseException;

import java.io.Closeable;
import java.io.IOException;

public final class IOCloser {

    private IOCloser() {}

    public static void close(Closeable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (IOException e) {
            throw new IOCloseException("关闭资源失败: " + closeable.getClass().getName(), e);
        }
    }

    public static void closeAll(Closeable... closeables) {
        if (closeables == null) return;
        for (Closeable c : closeables) {
            close(c);
        }
    }

    public static void closeReader(java.io.Reader reader) {
        close(reader);
    }

    public static void closeWriter(java.io.Writer writer) {
        close(writer);
    }

    public static void closeInputStream(java.io.InputStream inputStream) {
        close(inputStream);
    }

    public static void closeOutputStream(java.io.OutputStream outputStream) {
        close(outputStream);
    }

    public static void closeInputStreamReader(java.io.InputStreamReader reader) {
        close(reader);
    }

    public static void closeOutputStreamWriter(java.io.OutputStreamWriter writer) {
        close(writer);
    }
}
```

------

### 三、使用示例

```java
try {
    InputStream in = new FileInputStream("xxx.txt");
    OutputStream out = new FileOutputStream("yyy.txt");
    Reader reader = new InputStreamReader(in);
    Writer writer = new OutputStreamWriter(out);

    // 处理逻辑
} finally {
    IOCloser.closeAll(reader, writer);
    IOCloser.closeInputStream(in);
    IOCloser.closeOutputStream(out);
}
```

------

### 四、总结说明

| 方法名                        | 类型             | 是否推荐使用 | 说明                               |
| ----------------------------- | ---------------- | ------------ | ---------------------------------- |
| `close(Closeable)`            | 通用             | 是           | 任意 `Closeable` 对象均可使用      |
| `closeAll(Closeable...)`      | 通用批量         | 是           | 批量关闭，避免多次写法冗余         |
| `closeInputStream` 等单项方法 | 语义化、明确意图 | 可选         | 更清晰表达资源类型，适用于复杂代码 |

是否还需扩展支持 `Flushable`（如 `BufferedWriter.flush()`）场景，或进一步封装为 try-with-resource 风格回调？

# 扩展: 支持 `Flushable`,  及try-with-resource 风格回调

扩展支持 Flushable（如 BufferedWriter.flush()）场景，并进一步封装为 try-with-resource 风格回调

下面是对 `IOCloser` 的进一步增强：

1. **支持 `Flushable` 的刷新操作**（如 `BufferedWriter`、`OutputStreamWriter` 等）
2. **封装 try-with-resources 风格的回调执行函数**
    实现统一管理资源的创建、使用与关闭，调用方只关注业务逻辑，无需手动写 try-finally

------

### 一、自定义异常补充

已有：

```java
public class IOCloseException extends RuntimeException {
    public IOCloseException(String message, IOException cause) {
        super(message, cause);
    }
}
```

新增（用于 flush 失败）：

```java
public class IOFlushException extends RuntimeException {
    public IOFlushException(String message, IOException cause) {
        super(message, cause);
    }
}
```

------

### 二、增强后的 `IOCloser` 工具类

```java
package com.st.modules.file.common.util;

import com.st.modules.file.common.exception.IOCloseException;
import com.st.modules.file.common.exception.IOFlushException;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.util.function.Consumer;

public final class IOCloser {

    private IOCloser() {}

    public static void close(Closeable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (IOException e) {
            throw new IOCloseException("关闭失败: " + closeable.getClass().getName(), e);
        }
    }

    public static void closeAll(Closeable... closeables) {
        if (closeables == null) return;
        for (Closeable c : closeables) {
            close(c);
        }
    }

    public static void flush(Flushable flushable) {
        if (flushable == null) return;
        try {
            flushable.flush();
        } catch (IOException e) {
            throw new IOFlushException("刷新失败: " + flushable.getClass().getName(), e);
        }
    }

    public static void flushAll(Flushable... flushables) {
        if (flushables == null) return;
        for (Flushable f : flushables) {
            flush(f);
        }
    }

    public static <T extends Closeable> void use(T resource, Consumer<T> consumer) {
        try (T res = resource) {
            consumer.accept(res);
        } catch (IOException e) {
            throw new IOCloseException("资源处理失败: " + resource.getClass().getName(), e);
        }
    }
}
```

------

### 三、使用示例（1）：统一关闭 + 刷新

```java
BufferedWriter writer = null;
try {
    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("out.txt")));
    writer.write("Hello");

    IOCloser.flush(writer);  // 主动 flush
} finally {
    IOCloser.close(writer);
}
```

------

### 四、使用示例（2）：回调式资源使用（模拟 try-with-resources）

```java
IOCloser.use(new FileOutputStream("out.txt"), out -> {
    try {
        out.write("data".getBytes());
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
});
```

------

### 五、设计说明

| 能力           | 方法                  | 是否抛异常 | 场景举例                       |
| -------------- | --------------------- | ---------- | ------------------------------ |
| 关闭资源       | `close`、`closeAll`   | 是         | `InputStream`、`Reader` 等     |
| 刷新资源       | `flush`、`flushAll`   | 是         | `BufferedWriter`、`Writer`     |
| 回调执行资源块 | `use(T, Consumer<T>)` | 是         | 模拟 `try-with-resources` 风格 |

------

是否需要进一步支持：

- `Function<T, R>` 风格的 `use()` 返回结果？
- 多资源（输入 + 输出）组合 `use2(input, output, BiConsumer)` 的复合调用？

# 扩展: 多资源（输入 + 输出）组合 use2(input, output, BiConsumer) 的复合调用

多资源（输入 + 输出）组合 use2(input, output, BiConsumer) 的复合调用

下面是对 `IOCloser` 工具类的进一步扩展：支持多资源（输入 + 输出）组合的资源回调，通过 `BiConsumer` 实现业务处理，自动管理资源关闭，提升代码整洁性与安全性。

------

### 一、方法签名与实现

```java
public static <I extends Closeable, O extends Closeable>
void use2(I input, O output, BiConsumer<I, O> consumer) {
    try (I in = input; O out = output) {
        consumer.accept(in, out);
    } catch (IOException e) {
        throw new IOCloseException("资源处理失败: " + inOutNames(input, output), e);
    }
}

private static String inOutNames(Object input, Object output) {
    return (input != null ? input.getClass().getName() : "null") +
           " & " +
           (output != null ? output.getClass().getName() : "null");
}
```

------

### 二、使用示例

#### 示例 1：文件拷贝

```java
IOCloser.use2(
    new FileInputStream("input.txt"),
    new FileOutputStream("output.txt"),
    (in, out) -> {
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
        IOCloser.flush(out); // 主动刷新（若需要）
    }
);
```

#### 示例 2：网络下载流处理（简化版）

```java
IOCloser.use2(
    socket.getInputStream(),
    new FileOutputStream("result.txt"),
    (in, out) -> {
        byte[] buf = new byte[4096];
        int len;
        while ((len = in.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
        IOCloser.flush(out);
    }
);
```

------

### 三、特性说明

| 项目         | 说明                                     |
| ------------ | ---------------------------------------- |
| 多资源封装   | 同时接收 `input` 与 `output` 两个资源    |
| 类型泛型     | 泛型约束为 `Closeable` 类型              |
| 安全关闭     | 自动关闭 `in.close()` 和 `out.close()`   |
| 异常上抛     | 包装为 `IOCloseException` 不中断流程信息 |
| 明确调用语义 | 避免调用者手写繁琐 try-finally 代码块    |

------

### 四、可选拓展建议

- 若需要返回处理结果，可扩展为 `BiFunction<I, O, R>`，示例：`<R> R use2(I, O, BiFunction<I, O, R>)`
- 若你有三资源（如 `Reader`, `Writer`, `Logger`）也可进一步拓展为 `use3(...)`

是否需要我一起提供 `use3(...)` 三资源调用模板？



