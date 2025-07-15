# 读取

## 概述

```java
InputStream in = ClassPathResourcesUtils.class.getClassLoader().getResourceAsStream(relativePath);
```

## 工具类

ClassPathResourcesUtils.java

```java
package com.st.modules.file.classpath;

import java.io.InputStream;

public class ClassPathResourcesUtils {

    /**
     * 从 classpath 中加载指定相对路径的文件，返回其输入流。
     * <p>
     * 该方法适用于加载位于类路径中的资源文件（如配置、模板、测试数据等），
     * 文件必须存在于 /resources 或 /META-INF 下的编译输出目录中。
     * </p>
     *
     * @param relativePath classpath 下的相对路径（例如 "ftp/test/data.txt"）
     * @return 对应文件的 InputStream，用于读取资源内容
     * @throws IllegalArgumentException 如果找不到对应资源，抛出异常提示路径无效
     * @implNote 返回的 InputStream 由调用方负责关闭；若资源不存在将返回 null，请显式处理
     * @see ClassLoader#getResourceAsStream(String)
     */
    public static InputStream getClasspathFile(String relativePath) {
        InputStream in = ClassPathResourcesUtils.class.getClassLoader().getResourceAsStream(relativePath);
        if (in == null) {
            throw new IllegalArgumentException("找不到 classpath 文件: " + relativePath);
        }
        return in;
    }

}

```



# 写入

能写入吗? 如何写入? 

在 Java 中，**`Classpath` 下的资源是只读的**，无法通过 `getResourceAsStream()` 获取 `OutputStream` 来直接写入 `classpath` 中的文件。原因如下：

------

### 一、为什么不能向 classpath 写入

1. **Classpath 是编译输出或 JAR 内容**
   - 如 `target/classes/` 或打包后的 `.jar` 文件；
   - 这些文件在运行时由类加载器只读加载，无法动态写入。
2. **资源位置不明确**
   - `getResourceAsStream()` 读取的是打包或 classpath 中的资源，实际可能在文件系统、JAR 包、网络等；
   - 即使你在开发模式（未打包）下能定位到物理路径，打包后也不可写入。

------

### 二、若确实需要写入，可考虑以下方案

#### 方案 1：通过 `getResource()` 获取 `File`，确认是本地文件再写入

```java
URL url = YourClass.class.getClassLoader().getResource("ftp/test/data.txt");
if (url != null && url.getProtocol().equals("file")) {
    File file = new File(url.toURI());
    try (OutputStream out = new FileOutputStream(file)) {
        out.write("new data".getBytes());
    }
}
```

限制：
 仅在开发阶段有效（未打包），一旦进入 JAR，URL 将变成 `jar:file:/...`，不可写。

------

#### 方案 2：将文件写入外部路径（如 `tmp/`、`/data/` 等）

```java
File outFile = new File("tmp/out.txt"); // 相对路径
try (OutputStream out = new FileOutputStream(outFile)) {
    out.write("data".getBytes());
}
```

或指定绝对路径：

```java
String path = Paths.get(System.getProperty("user.dir"), "data", "output.txt").toString();
```

------

#### 方案 3：打包时保留“模板”或“空白文件”，运行时复制到可写目录

```java
// 读取 classpath 中的模板文件
InputStream template = getClass().getClassLoader().getResourceAsStream("template/blank.txt");

// 写入到系统 tmp 目录或 data 目录中
Files.copy(template, Paths.get("/data/my-blank-copy.txt"), StandardCopyOption.REPLACE_EXISTING);
```

------

### 三、最佳实践建议

| 场景                         | 建议写入位置         |
| ---------------------------- | -------------------- |
| 配置 / 临时数据 / 生成文件   | `user.dir`、`/tmp`   |
| 用户上传、缓存、导出等文件   | 自定义目录如 `/data` |
| classpath 中资源（只读模板） | 读取 → 复制到外部    |

------

### 四、结论

**无法也不应直接向 classpath 写入文件**，否则会因路径不可写或 JAR 文件不可修改导致运行错误。请改为写入外部路径或复制模板后操作。

是否需要我封装一个 `ClasspathTemplateWriter.copyTo()` 工具方法，自动从 classpath 复制到指定目录并写入？