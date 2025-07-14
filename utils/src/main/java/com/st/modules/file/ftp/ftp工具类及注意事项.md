# 概述

相关工具类, 在`tsprTools/utils/src/main/java/com/st/modules/file/ftp` 包下

# 问题/多处使用ftp, 但中间被关闭,导致后续ftp无法使用, 导致异常

## 现象

如, controller, service中多处使用ftpclient, 而中间的某个步骤, 如上传、创建目录等功能中处理完会关闭connect, 导致下面的步骤中使用ftpclient没有连接. 说明该问题, 以及对应的解决方案

## 分析及方案

确实，`FTPClient` 的关闭处理若不规范，很容易导致以下问题：

> **问题现象**：
>  多个组件（如 Controller、Service、Util 等）共用同一个 `FTPClient` 实例，在其中某一步调用了 `closeQuietly(ftpClient)`，导致连接断开，后续操作抛出 `FTPConnectionClosedException` 或无效连接异常。

------

### 一、问题本质说明

#### **代码示例**

```java
ftpClient.makeDirectory("/newDir"); // 内部创建后 closeQuietly(ftpClient)
ftpClient.storeFile("/newDir/file.txt", inputStream); // 报错：连接已关闭
```

#### **问题本质**

- `ftpClient` 是有状态连接对象，`disconnect()` 一旦调用，**连接不可复用**
- 连接关闭应该是**由统一上下文或调用者负责的事情**
- 中间层工具类如 `FtpUtil.upload()` 不应私自关闭连接，否则破坏调用方逻辑控制

------

### 二、常见错误模式

| 错误模式                              | 问题说明                                 |
| ------------------------------------- | ---------------------------------------- |
| 工具方法中调用 `closeQuietly()`       | 把连接生命周期限制在方法内部，破坏调用链 |
| 多层调用但未声明连接归属权            | 上层以为连接仍可用，实则已断开           |
| 靠 `ftpClient.isConnected()` 判断复用 | 连接一旦关闭，无法恢复，只能重新创建     |

------

### 三、推荐的解决方案

#### 方案一：连接生命周期由调用方管理（最推荐）

- 工具类只处理上传、下载，不做连接关闭
- 所有 `ftpClient.logout()` / `disconnect()` 放在 controller/service 统一关闭处

##### 示例：

```java
public void handleUpload() {
    FTPClient ftpClient = ftpConnectionFactory.create(); // 创建连接
    try {
        ftpUploader.createDirectory(ftpClient, "/abc");
        ftpUploader.upload(ftpClient, "/abc/a.txt", fileStream);
    } finally {
        FtpUtils.closeQuietly(ftpClient); // 只在最后关闭连接
    }
}
```

工具类内部不要调用 `closeQuietly()`

------

#### 方案二：封装上下文（如 `FtpSession`）

封装连接 + 统一生命周期管理：

```java
public class FtpSession implements Closeable {
    private final FTPClient ftpClient;

    public FtpSession() {
        this.ftpClient = ftpConnectionFactory.create();
    }

    public void upload(...) {
        // 上传逻辑
    }

    @Override
    public void close() {
        FtpUtils.closeQuietly(ftpClient);
    }
}
```

使用方式：

```java
try (FtpSession session = new FtpSession()) {
    session.upload(...);
    session.createDir(...);
}
```

- 生命周期明确，`close()` 自动关闭
- 多操作共享连接，不提前断开

------

#### 方案三：使用连接池（如 Apache Commons Pool + FTPClient）

- 不常用，但适用于高并发场景
- 需要自行实现连接回收、健康检查

------

### 四、小结与建议

| 问题                   | 建议                                                |
| ---------------------- | --------------------------------------------------- |
| 多步操作中连接提前关闭 | 严禁工具类中调用 `disconnect()`                     |
| 谁应关闭连接？         | 连接创建方应负责关闭（如 controller、service）      |
| 如何封装生命周期？     | 可用 try-with-resources + 包装类（如 `FtpSession`） |

------

### 五、推荐结构归纳

```java
Controller/Service
    └─ 创建并管理 FTPClient 生命周期
        └─ 工具类只处理逻辑（上传/下载/建目录）
            └─ 工具类不得关闭连接
```

如你需要，我可以补充完整封装方案，包括：

- FTP连接工厂类
- FtpSession 管理类
- 工具方法标准模板（不负责关闭）

# 问题/工具类中吞异常, 出了问题无法排查

ftp问题之一, 工具类中吞异常, 出了问题无法排查. 各种场景说明, 及方案

这是一个极其常见且严重的设计问题：

> **问题现象**：FTP 工具类内部出现异常（如上传失败、目录创建失败、连接异常），却被 try-catch 吞掉，导致：
>
> - 调用方毫无感知
> - 日志不完整或无关键堆栈信息
> - 整体业务逻辑表现为“什么都没发生”，严重影响排查与维护

## 一、典型错误代码示例

```java
public static void upload(FTPClient ftpClient, String path, InputStream input) {
    try {
        ftpClient.storeFile(path, input);
    } catch (IOException e) {
        // 被吞掉了，什么也不做或仅仅打印(e.printstack)
    }
}
```

调用方：

```java
ftpUploader.upload(ftpClient, "/abc/a.txt", stream); // 调用后一切“正常”，但其实什么也没上传
```



以下是针对 FTP 工具类中**异常被吞**的典型错误代码场景进行全面穷举与分析，涵盖上传、下载、目录、连接、编码、资源释放等多个方面。

------

### 一、异常被吞的典型场景分类与错误示例

| 类别         | 操作                        | 错误示例                                                     | 可能后果                                         |
| ------------ | --------------------------- | ------------------------------------------------------------ | ------------------------------------------------ |
| **连接阶段** | 连接失败                    | `ftpClient.connect(host);` → 异常被吞                        | 无法连接，调用方误以为连接成功                   |
|              | 登录失败                    | `ftpClient.login(user, pass);` → 异常被吞                    | 登录失败后继续操作，抛出更多异常                 |
|              | 设置模式失败                | `ftpClient.enterLocalPassiveMode();` → 异常被吞              | 数据通道建立失败，导致文件操作挂起或中断         |
|              | 设置编码失败                | `ftpClient.setControlEncoding("UTF-8");` → 异常被吞          | 中文路径乱码                                     |
| **上传阶段** | 上传失败                    | `ftpClient.storeFile(path, input);` → 返回 false 或抛异常被吞 | 文件未上传，业务逻辑继续，出现“空结果”或“假成功” |
|              | 重复上传                    | 文件覆盖、权限不足 → 异常被吞                                | 调用方误以为覆盖成功，实际无变化                 |
| **下载阶段** | 获取输入流失败              | `ftpClient.retrieveFileStream(path);` → 异常被吞             | 得到 null，操作 NPE                              |
|              | 下载失败                    | `ftpClient.retrieveFile(...)` → 返回 false 被忽略            | 文件未下载，业务继续执行错误逻辑                 |
| **目录操作** | 创建目录失败                | `ftpClient.makeDirectory(path);` → 异常被吞                  | 上传目标路径不存在                               |
|              | 切换目录失败                | `ftpClient.changeWorkingDirectory(path);` → false 被吞       | 接下来所有操作均在错误目录下进行                 |
|              | 删除目录失败                | `ftpClient.removeDirectory(path);` → 异常被吞                | 清理逻辑不完整，造成遗留数据                     |
| **文件操作** | 删除文件失败                | `ftpClient.deleteFile(filePath);` → false 被吞               | 文件未删，误认为已处理                           |
|              | 判断文件存在失败            | `ftpClient.listFiles(path);` → IOException 被吞              | 文件存在判断错误，后续操作异常                   |
| **连接关闭** | 登出失败                    | `ftpClient.logout();` → 异常被吞                             | 连接未正常释放，连接池泄漏风险                   |
|              | 断开失败                    | `ftpClient.disconnect();` → 异常被吞                         | 网络连接残留，资源未释放                         |
| **异常处理** | 所有异常统一 try-catch 忽略 | `catch (IOException e) {}`                                   | 系统日志缺失，业务层误判为成功                   |

------

### 二、易被误判为“正常成功”的危险调用

| 场景       | 问题代码                                     | 表面返回成功，但实则失败                        |
| ---------- | -------------------------------------------- | ----------------------------------------------- |
| 文件上传   | `ftpClient.storeFile()` → false 被忽略       | 文件未写入，返回 true 才表示成功                |
| 下载流获取 | `retrieveFileStream()` 返回 null             | 若未判断或未捕获异常，导致 NullPointerException |
| 删除文件   | `deleteFile()` 返回 false 被忽略             | 文件依旧存在，逻辑未生效                        |
| 切换目录   | `changeWorkingDirectory()` 返回 false 被忽略 | 文件上传到了错误路径                            |

------

### 三、统一规范：哪些异常必须抛、哪些可记录

| 类型                     | 是否应抛出 | 建议处理方式                       |
| ------------------------ | ---------- | ---------------------------------- |
| 连接类（connect、login） | 是         | 明确抛出，标记为初始化失败         |
| 上传、下载类             | 是         | 抛出 `FtpException` 或返回结果封装 |
| 路径、目录类             | 是         | 明确记录日志 + 抛异常              |
| 编码设置                 | 否         | 记录日志即可（不影响连接）         |
| 登出、断开类             | 否         | `finally` 中记录日志，不抛出       |
| 日志用途的文件遍历       | 否         | 可以吞掉但记录日志                 |

------

### 四、总结：Ftp 工具类中所有需要处理的调用项（必须异常安全）

| 功能分类     | 关键方法                                       | 建议处理方式                        |
| ------------ | ---------------------------------------------- | ----------------------------------- |
| 连接         | connect、login                                 | 明确抛出                            |
| 模式设置     | enterPassiveMode、setFileType                  | 记录失败                            |
| 路径切换     | changeWorkingDirectory、makeDirectory          | 抛出或返回封装结果                  |
| 文件操作     | storeFile、retrieveFile、deleteFile、listFiles | 抛出异常或包装成 Result             |
| 关闭连接     | logout、disconnect                             | try-catch 但需记录日志              |
| 判断是否成功 | 返回 boolean 的方法                            | 必须判断是否为 true，false 视为失败 |
| 获取流       | retrieveFileStream、getInputStream             | 判空 + 异常捕获                     |

------

### 五、推荐模板代码结构（上传为例）

```java
public static void uploadFile(FTPClient ftpClient, String remotePath, InputStream input) {
    try {
        if (!ftpClient.changeWorkingDirectory(remotePath)) {
            throw new FtpException("切换目录失败：" + remotePath);
        }

        boolean success = ftpClient.storeFile("file.txt", input);
        if (!success) {
            throw new FtpException("上传失败：storeFile 返回 false");
        }
    } catch (IOException e) {
        throw new FtpException("FTP上传异常：" + e.getMessage(), e);
    }
}
```

------

### 六、小结与建议

| 问题                    | 原因                         | 建议                                    |
| ----------------------- | ---------------------------- | --------------------------------------- |
| 工具类吞异常            | 过度 try-catch 不抛出        | 明确职责，工具类记录 + 抛出             |
| 无日志                  | 没有堆栈、无定位手段         | 所有 `catch` 均需日志堆栈输出           |
| 返回值 false 未处理     | 不检查 storeFile、deleteFile | 必须检查 boolean 返回值，false 视为异常 |
| finally 中 swallow 错误 | 资源释放时忽略 logout 异常   | 可吞但必须日志，不能 silence            |

如你需要，我可提供一份完整的 FTP 工具类安全模板，支持：

- 所有操作异常封装成 `FtpException`
- 返回值检测
- 自动日志记录
- 提供 `Result<T>` 包装形式调用可选返回

是否继续？

## 二、可能出现的场景与后果

| 场景         | 问题说明                           | 后果                       |
| ------------ | ---------------------------------- | -------------------------- |
| 上传失败     | 文件权限 / 目录不存在 / 文件名非法 | 文件未上传，调用方毫无知觉 |
| 创建目录失败 | 权限不足或目录已存在异常           | 后续上传失败，但异常被吞   |
| 被动模式失败 | 网络中断 / FTP服务器设置不一致     | 操作失败但不抛出           |
| 登出失败     | `logout()` 异常吞掉                | 没及时释放资源，连接泄漏   |

## 三、正确的异常处理策略

#### 方案一：对调用方显性抛出异常（推荐）

```java
public static void upload(FTPClient ftpClient, String path, InputStream input) {
    try {
        boolean success = ftpClient.storeFile(path, input);
        if (!success) {
            throw new IOException("FTP storeFile returned false for path: " + path);
        }
    } catch (IOException e) {
        throw new RuntimeException("FTP upload failed: " + e.getMessage(), e);
    }
}
```

调用方感知失败：

```java
try {
    ftpUploader.upload(ftpClient, "/a/b.txt", stream);
} catch (RuntimeException e) {
    log.error("上传失败: ", e);
}
```

#### 好处：

- 异常堆栈完整
- 可由调用方统一处理、记录、回滚或告警

------

#### 方案二：异常转换 + 返回结果对象

```java
public class FtpResult {
    private boolean success;
    private String message;
    private Exception exception;
    // 构造器 / getter
}
public static FtpResult upload(FTPClient ftpClient, String path, InputStream input) {
    try {
        boolean success = ftpClient.storeFile(path, input);
        if (!success) {
            return new FtpResult(false, "上传失败", null);
        }
        return new FtpResult(true, "上传成功", null);
    } catch (IOException e) {
        return new FtpResult(false, "异常: " + e.getMessage(), e);
    }
}
```

调用方：

```java
FtpResult result = ftpUploader.upload(...);
if (!result.isSuccess()) {
    log.error("FTP上传失败: {}", result.getMessage(), result.getException());
}
```

------

#### 方案三：记录日志 + 重新抛出

```java
try {
    ftpClient.makeDirectory(path);
} catch (IOException e) {
    log.error("创建目录失败 path: {}", path, e);
    throw new FtpException("创建目录失败", e);
}
```

- 保证异常链不丢失
- 提供业务语义更清晰的异常（如 `FtpException`）

## 四、统一封装建议

#### 1. 定义异常类（如 `FtpException`）

```java
public class FtpException extends RuntimeException {
    public FtpException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

#### 2. 工具类中永远不应吞异常

- 要么记录日志 + 抛出
- 要么封装错误结果返回

#### 3. 接口设计上保持异常透明

- 不要内部 try-catch 后 `return false` 或什么也不做
- 让调用方知道失败，做出响应（如重试、记录、提示用户）

## 五、小结

| 设计问题             | 建议方案                            |
| -------------------- | ----------------------------------- |
| 异常被吞             | 明确记录日志，保留堆栈              |
| 工具方法中 try-catch | 只处理 `finally` 或包装异常重新抛出 |
| 业务不可恢复异常     | 立即抛出，交由上层捕获              |
| 可恢复异常           | 捕获后记录 + 封装结果返回           |

## 六、补充建议：区分“资源释放异常”与“业务失败异常”

```java
try {
    ftpUploader.upload(...);
} catch (FtpException e) {
    log.error("上传失败: {}", e.getMessage(), e);
} finally {
    FtpUtils.closeQuietly(ftpClient); // 释放异常可吞，但必须保证连接断开
}
```

如你希望，我可以提供一份完整的 `FtpUploader` 模板类，封装好连接创建、异常处理、日志记录、资源关闭等职责，适用于中大型系统的统一封装方案。

###### 