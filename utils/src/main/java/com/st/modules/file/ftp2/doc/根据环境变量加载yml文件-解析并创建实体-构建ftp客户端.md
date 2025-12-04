# 目标

- 根据jvm参数 -DappEnv= dev | test |prod, 加载对应的配置: config_dev.yml, config_test.yml...
- 第一次使用时, 触发: 
  - 加载yaml
  - 解析出对应的实体
- 具体业务场景, 创建ftp客户端, 使用后关闭

# yml配置

YAML 配置多组 FTP 信息示例

### YAML 配置多组 FTP 信息示例

在实际项目中（如 Spring Boot 配置中心、或单独的 YAML 配置文件），除了常规的 FTP 连接信息（`host`、`port`、`user`、`password`），你可以增加一些业务标识字段，例如 `code`（唯一编码）、`name`（显示名）、`desc`（描述），方便后续根据业务场景进行管理和选择。

下面给出一个规范化的 YAML 配置示例：

```yaml
ftp:
  servers:
    - code: FGLS
      name: 辅助系统 FTP
      desc: 用于上传财务相关文件
      host: ftp.fgls.com
      port: 21
      user: fgls_user
      password: fgls_pwd
      passiveMode: true

    - code: NC
      name: NC 财务 FTP
      desc: 对接 NC 系统文件交换
      host: ftp.nc.com
      port: 21
      user: nc_user
      password: nc_pwd
      passiveMode: false

    - code: REPORT
      name: 报表平台 FTP
      desc: 每日报表推送
      host: ftp.report.com
      port: 22
      user: report_user
      password: report_pwd
      passiveMode: true
```

### 说明

1. `code`: 系统或业务唯一编码，后续可以用来在代码里快速选择对应 FTP。
2. `name`: 便于运维或开发人员识别的中文或英文名称。
3. `desc`: 详细描述，方便文档化或多人协作。
4. `host/port/user/password`: 标准 FTP 连接信息。
5. `passiveMode`: 可选字段，标识是否使用被动模式（true/false）。

# Spring Boot 中读取方式

定义一个配置类，使用 `@ConfigurationProperties` 自动绑定：

```java
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "ftp")
public class FtpProperties {

    private List<FtpServer> servers;

    @Data
    public static class FtpServer {
        private String code;
        private String name;
        private String desc;
        private String host;
        private int port;
        private String user;
        private String password;
        private boolean passiveMode;
    }
}
```

然后在业务代码中，可以根据 `code` 获取对应的 FTP 配置：

```java
@Autowired
private FtpProperties ftpProperties;

public FtpProperties.FtpServer getFtpByCode(String code) {
    return ftpProperties.getServers()
            .stream()
            .filter(s -> s.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("未找到 FTP 配置: " + code));
}
```

这样就能在项目里根据业务场景灵活选择对应的 FTP 配置。

要不要我再帮你把这个配置和 `Map<String, FtpServer>` 的形式自动转换，方便按 `code` 直接取？

# 通用方式

不使用自动绑定. 写一套通用加载逻辑，在任何java中都适用. 

### 目标

在不依赖 Spring Boot 自动绑定的前提下，提供一套通用 YAML 加载与取数逻辑，适用于任意 Java 程序：读取 YAML 为 `Map` 结构、支持文件路径与类路径、支持用“点路径”读取子树、将通用结构安全转换为业务对象，并提供基于 `code` 的 FTP 配置索引与校验

### 最小 YAML 示例（说明用）

```
ftp:
  servers:
    - code: FGLS
      name: 辅助系统 FTP
      desc: 用于上传财务相关文件
      host: ftp.fgls.com
      port: 21
      user: u1
      password: p1
      passiveMode: true
    - code: NC
      name: NC 财务 FTP
      desc: 对接 NC 系统文件交换
      host: ftp.nc.com
      port: 21
      user: u2
      password: p2
      passiveMode: false
```

### 通用加载与转换工具源码

```java
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * 概述
 * 1 加载 YAML 为 Map 结构
 * 2 支持从文件路径或类路径读取
 * 3 提供基于点路径的子树提取与安全取值
 * 4 不依赖 Spring, 纯 JDK8 可用
 * 功能清单
 * 1 loadFromFile 读取文件
 * 2 loadFromClasspath 读取类路径
 * 3 load 读取输入流
 * 4 getByPath 使用 a.b.c 形式读取子树
 * 5 reqStr reqInt reqBool 等必填项读取并校验
 * 使用示例
 * 见 main 方法与 FtpRegistry 示例
 * 注意事项
 * 1 依赖 SnakeYAML 进行解析 请在构建工具中加入 snakeyaml 依赖
 * 2 避免打印密码等敏感信息
 * 入参与出参与异常说明
 * 参见各方法 Javadoc
 */
public final class YamlLoader {

    private YamlLoader() {}

    /**
     * 从文件系统路径加载 YAML
     * @param filePath 文件绝对或相对路径
     * @return 根节点 Map
     * @throws RuntimeException 读取失败或解析失败
     */
    public static Map<String, Object> loadFromFile(String filePath) {
        try (InputStream in = new FileInputStream(filePath)) {
            return load(in);
        } catch (Exception e) {
            throw new RuntimeException("Load YAML from file failed: " + filePath, e);
        }
    }

    /**
     * 从类路径加载 YAML
     * @param classpath 类路径 如 config/app.yml
     * @return 根节点 Map
     * @throws RuntimeException 资源不存在或解析失败
     */
    public static Map<String, Object> loadFromClasspath(String classpath) {
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(classpath)) {
            if (in == null) {
                throw new RuntimeException("Classpath resource not found: " + classpath);
            }
            return load(in);
        } catch (Exception e) {
            throw new RuntimeException("Load YAML from classpath failed: " + classpath, e);
        }
    }

    /**
     * 从输入流加载 YAML
     * @param input 输入流 调用方负责关闭或传入 try with resources
     * @return 根节点 Map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> load(InputStream input) {
        Yaml yaml = new Yaml(new SafeConstructor());
        Object obj = yaml.load(input);
        if (obj == null) {
            return new LinkedHashMap<String, Object>();
        }
        if (!(obj instanceof Map)) {
            throw new RuntimeException("Root of YAML must be a mapping object");
        }
        return (Map<String, Object>) obj;
    }

    /**
     * 使用点路径获取子树 如 ftp.servers
     * @param root 根 Map
     * @param path 以 . 分隔的路径
     * @return 子对象 可为 Map List 或值
     */
    public static Object getByPath(Map<String, Object> root, String path) {
        String[] parts = path.split("\\.");
        Object cur = root;
        for (String p : parts) {
            if (!(cur instanceof Map)) return null;
            @SuppressWarnings("unchecked")
            Map<String, Object> m = (Map<String, Object>) cur;
            cur = m.get(p);
            if (cur == null) return null;
        }
        return cur;
    }

    /**
     * 从 Map 读取必填字符串字段
     * @param map 源 Map
     * @param key 键
     * @return 非空字符串
     */
    public static String reqStr(Map<String, Object> map, String key) {
        Object v = map.get(key);
        if (v == null) throw new IllegalArgumentException("Missing required field: " + key);
        String s = String.valueOf(v).trim();
        if (s.isEmpty()) throw new IllegalArgumentException("Empty required field: " + key);
        return s;
    }

    /**
     * 从 Map 读取必填整数字段
     * @param map 源 Map
     * @param key 键
     * @return 整数
     */
    public static int reqInt(Map<String, Object> map, String key) {
        Object v = map.get(key);
        if (v == null) throw new IllegalArgumentException("Missing required field: " + key);
        if (v instanceof Number) return ((Number) v).intValue();
        try {
            return Integer.parseInt(String.valueOf(v).trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid int for field: " + key + " value=" + v);
        }
    }

    /**
     * 从 Map 读取必填布尔字段
     * @param map 源 Map
     * @param key 键
     * @return 布尔
     */
    public static boolean reqBool(Map<String, Object> map, String key) {
        Object v = map.get(key);
        if (v == null) throw new IllegalArgumentException("Missing required field: " + key);
        if (v instanceof Boolean) return (Boolean) v;
        String s = String.valueOf(v).trim().toLowerCase(Locale.ROOT);
        if ("true".equals(s)) return true;
        if ("false".equals(s)) return false;
        throw new IllegalArgumentException("Invalid boolean for field: " + key + " value=" + v);
    }
}

/**
 * FTP 服务器配置模型
 */
class FtpServer {
    private String code;
    private String name;
    private String desc;
    private String host;
    private int port;
    private String user;
    private String password;
    private boolean passiveMode;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDesc() { return desc; }
    public void setDesc(String desc) { this.desc = desc; }
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public boolean isPassiveMode() { return passiveMode; }
    public void setPassiveMode(boolean passiveMode) { this.passiveMode = passiveMode; }

    @Override
    public String toString() {
        return "FtpServer{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", user='" + user + '\'' +
                ", passiveMode=" + passiveMode +
                '}';
    }
}

/**
 * 概述
 * 1 将通用 Map 结构转换为 FtpServer 列表
 * 2 按 code 建索引 Map 便于快速查找
 * 3 包含基础校验与重复 code 检测
 * 功能清单
 * 1 fromRoot 构建注册表
 * 2 getByCode 查找
 * 3 listAll 列表
 * 使用示例
 * 见 main
 * 注意事项
 * 1 不打印 password
 * 入参与出参与异常说明
 * 参见各方法 Javadoc
 */
class FtpRegistry {

    private final Map<String, FtpServer> byCode;
    private final List<FtpServer> all;

    private FtpRegistry(Map<String, FtpServer> byCode, List<FtpServer> all) {
        this.byCode = byCode;
        this.all = all;
    }

    /**
     * 从 YAML 根 Map 构建注册表
     * @param root 根 Map
     * @param serversPath 服务器数组的点路径 如 ftp.servers
     * @return 注册表
     * @throws IllegalArgumentException 结构不合法或缺少必填项
     */
    @SuppressWarnings("unchecked")
    public static FtpRegistry fromRoot(Map<String, Object> root, String serversPath) {
        Object obj = YamlLoader.getByPath(root, serversPath);
        if (obj == null) throw new IllegalArgumentException("Path not found: " + serversPath);
        if (!(obj instanceof List)) throw new IllegalArgumentException("Path is not a list: " + serversPath);

        List<?> list = (List<?>) obj;
        List<FtpServer> servers = new ArrayList<FtpServer>(list.size());
        Map<String, FtpServer> idx = new LinkedHashMap<String, FtpServer>();

        for (Object elem : list) {
            if (!(elem instanceof Map)) {
                throw new IllegalArgumentException("Server item must be a map: " + elem);
            }
            Map<String, Object> m = (Map<String, Object>) elem;

            FtpServer s = new FtpServer();
            s.setCode(YamlLoader.reqStr(m, "code"));
            s.setName(YamlLoader.reqStr(m, "name"));
            s.setDesc(String.valueOf(m.getOrDefault("desc", "")));
            s.setHost(YamlLoader.reqStr(m, "host"));
            s.setPort(YamlLoader.reqInt(m, "port"));
            s.setUser(YamlLoader.reqStr(m, "user"));
            s.setPassword(YamlLoader.reqStr(m, "password"));
            s.setPassiveMode(m.containsKey("passiveMode") ? YamlLoader.reqBool(m, "passiveMode") : false);

            if (idx.containsKey(s.getCode())) {
                throw new IllegalArgumentException("Duplicate code: " + s.getCode());
            }
            idx.put(s.getCode(), s);
            servers.add(s);
        }
        return new FtpRegistry(idx, servers);
    }

    /**
     * 根据 code 获取
     * @param code 唯一编码
     * @return FtpServer
     * @throws NoSuchElementException 未找到
     */
    public FtpServer getByCode(String code) {
        FtpServer s = byCode.get(code);
        if (s == null) throw new NoSuchElementException("FTP not found: " + code);
        return s;
    }

    /**
     * 全量列表
     * @return 不可变列表
     */
    public List<FtpServer> listAll() {
        return Collections.unmodifiableList(all);
    }
}

/**
 * 使用示例 与自检
 */
class DemoMain {
    /**
     * 概述
     * 1 演示从类路径读取 config.yml
     * 2 构建 FtpRegistry 并按 code 取配置
     * 功能清单
     * 1 加载 2 构建 3 查询 4 打印
     * 使用示例
     * 直接运行 main 方法 需确保类路径存在 config.yml
     * 注意事项
     * 1 仅演示 不要在生产日志打印密码
     * 入参与出参与异常说明
     * args 忽略 无返回 主动抛出运行时异常便于定位
     */
    public static void main(String[] args) {
        Map<String, Object> root = YamlLoader.loadFromClasspath("config.yml");
        FtpRegistry registry = FtpRegistry.fromRoot(root, "ftp.servers");
        FtpServer f1 = registry.getByCode("FGLS");
        System.out.println("Loaded server: " + f1);
        System.out.println("All servers count: " + registry.listAll().size());
    }
}
```

### 设计要点与扩展

1. 通用性优先: `YamlLoader` 只关心 Map 结构与基础取值校验 适用于任何 YAML 配置场景
2. 渐进式绑定: 运行时先拿到通用 `Map` 再按需转换为你的业务对象 避免强耦合
3. 安全校验: `reqStr reqInt reqBool` 统一校验并给出明确错误信息 失败即早失败
4. 灵活路径: 通过 `getByPath(root,"a.b.c")` 可轻松定位任意子树 便于多模块复用
5. 可加环境覆盖: 若需要支持环境变量或系统属性覆盖 可在 `load` 后增加一层 merge 逻辑 按键名匹配替换
6. 可加加密与脱敏: 对 `password` 等字段可接入解密器接口 以及日志脱敏策略

### 依赖提示

实现依赖 SnakeYAML 建议在你的构建中添加相应依赖 若不能使用依赖 请告知 我可以给出基于 JSON 简易语法或自定义轻量解析器的替代方案

## 优化: 进行优化： 动态代码块加载配置，避免显示构建.

###### 

### 目标
在保持通用 YAML 加载的前提下，提供“动态视图化绑定”的方案：以接口为载体，通过 JDK 动态代理把 `Map` 结构“即取即用”映射为类型化对象，避免显式 new 对象与字段拷贝；支持点路径、基础类型自动转换、列表绑定与按 `code` 懒索引

### 设计要点
1. 面向接口的配置视图: 定义 `IFtpServer` 等只含 getter 的接口，动态代理从底层 `Map` 读取并转换类型，零显式构建
2. 一行完成绑定: `DynamicConfig.intoList(root,"ftp.servers", IFtpServer.class)` 返回 `List<IFtpServer>`，元素为懒取值代理
3. 类型安全与转换: 支持 `String int boolean` 等常见类型自动转换与缺失校验
4. 支持索引与懒加载: `DynamicConfig.indexBy(servers, IFtpServer::getCode)` 快捷得到 `Map<String,IFtpServer>`
5. 可热加载扩展: 重新 `load` YAML 后，用同路径再次 `intoList` 即可获得新视图，无需重建实体

### 源码
```java
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;

/**
 * 概述
 * 1 解析 YAML 为 Map
 * 2 支持文件路径与类路径
 * 3 提供点路径查询
 * 功能清单
 * 1 loadFromFile 加载文件
 * 2 loadFromClasspath 加载类路径
 * 3 load 解析输入流
 * 4 getByPath 点路径取子树
 * 使用示例
 * 参考 DemoMain
 * 注意事项
 * 需引入 snakeyaml 依赖
 * 入参与出参与异常说明
 * 读取失败抛 RuntimeException 根为 Map 解析失败抛 RuntimeException
 */
final class YamlLoader {
    private YamlLoader() {}
    public static Map<String, Object> loadFromFile(String filePath) {
        try (InputStream in = new FileInputStream(filePath)) { return load(in); }
        catch (Exception e) { throw new RuntimeException("Load YAML from file failed: " + filePath, e); }
    }
    public static Map<String, Object> loadFromClasspath(String classpath) {
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(classpath)) {
            if (in == null) throw new RuntimeException("Classpath resource not found: " + classpath);
            return load(in);
        } catch (Exception e) { throw new RuntimeException("Load YAML from classpath failed: " + classpath, e); }
    }
    @SuppressWarnings("unchecked")
    public static Map<String, Object> load(InputStream input) {
        Object obj = new Yaml(new SafeConstructor()).load(input);
        if (obj == null) return new LinkedHashMap<String, Object>();
        if (!(obj instanceof Map)) throw new RuntimeException("Root of YAML must be a mapping object");
        return (Map<String, Object>) obj;
    }
    @SuppressWarnings("unchecked")
    public static Object getByPath(Map<String, Object> root, String path) {
        Object cur = root;
        for (String p : path.split("\\.")) {
            if (!(cur instanceof Map)) return null;
            cur = ((Map<String, Object>) cur).get(p);
            if (cur == null) return null;
        }
        return cur;
    }
}

/**
 * 概述
 * 1 将 Map 视图动态绑定为接口 T 的只读代理
 * 2 支持 List 绑定与索引构建
 * 3 基础类型自动转换与缺失校验
 * 功能清单
 * 1 into 子树转接口视图
 * 2 intoList 子树列表转接口视图列表
 * 3 indexBy 按键建立索引
 * 使用示例
 * 参考 DemoMain
 * 注意事项
 * 接口方法需为 getter 或 isXxx 形式 不支持有参方法
 * 入参与出参与异常说明
 * 取值缺失或类型不匹配抛 IllegalArgumentException
 */
final class DynamicConfig {
    private DynamicConfig() {}
    public static <T> T into(Map<String, Object> root, String path, Class<T> view) {
        Object node = YamlLoader.getByPath(root, path);
        if (!(node instanceof Map)) throw new IllegalArgumentException("Path not a map " + path);
        @SuppressWarnings("unchecked")
        Map<String, Object> m = (Map<String, Object>) node;
        return bindMap(m, view);
    }
    public static <T> List<T> intoList(Map<String, Object> root, String path, Class<T> view) {
        Object node = YamlLoader.getByPath(root, path);
        if (!(node instanceof List)) throw new IllegalArgumentException("Path not a list " + path);
        List<?> raw = (List<?>) node;
        List<T> out = new ArrayList<T>(raw.size());
        for (Object o : raw) {
            if (!(o instanceof Map)) throw new IllegalArgumentException("List item not a map " + o);
            @SuppressWarnings("unchecked")
            Map<String, Object> m = (Map<String, Object>) o;
            out.add(bindMap(m, view));
        }
        return Collections.unmodifiableList(out);
    }
    public static <K, V> Map<K, V> indexBy(Collection<V> list, Function<V, K> keyFn) {
        Map<K, V> map = new LinkedHashMap<K, V>(Math.max(16, list.size() * 2));
        for (V v : list) {
            K k = keyFn.apply(v);
            if (k == null) throw new IllegalArgumentException("Index key is null");
            if (map.containsKey(k)) throw new IllegalArgumentException("Duplicate index key " + k);
            map.put(k, v);
        }
        return Collections.unmodifiableMap(map);
    }
    @SuppressWarnings("unchecked")
    private static <T> T bindMap(Map<String, Object> data, Class<T> view) {
        if (!view.isInterface()) throw new IllegalArgumentException("View must be interface " + view);
        InvocationHandler h = new ViewHandler(data, view);
        return (T) Proxy.newProxyInstance(view.getClassLoader(), new Class[]{view}, h);
    }
    private static final class ViewHandler implements InvocationHandler {
        private final Map<String, Object> data;
        private final Class<?> view;
        ViewHandler(Map<String, Object> data, Class<?> view) { this.data = data; this.view = view; }
        @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            if (method.getParameterCount() == 0) {
                if (name.equals("toString")) return "DynamicView(" + view.getSimpleName() + ")";
                if (name.equals("hashCode")) return System.identityHashCode(proxy);
                if (name.equals("equals")) return proxy == args[0];
                String key = toPropertyKey(name);
                if (key == null) throw new UnsupportedOperationException("Not a getter " + name);
                Object raw = data.get(key);
                if (raw == null) return defaultValueFor(method.getReturnType(), key);
                return convert(raw, method.getReturnType(), key);
            }
            throw new UnsupportedOperationException("Method not supported " + name);
        }
        private static String toPropertyKey(String methodName) {
            if (methodName.startsWith("get") && methodName.length() > 3) return decap(methodName.substring(3));
            if (methodName.startsWith("is") && methodName.length() > 2) return decap(methodName.substring(2));
            return null;
        }
        private static String decap(String s) { return Character.toLowerCase(s.charAt(0)) + s.substring(1); }
        private static Object defaultValueFor(Class<?> type, String key) {
            if (type.isPrimitive())
                throw new IllegalArgumentException("Missing required field " + key);
            return null;
        }
        private static Object convert(Object raw, Class<?> target, String key) {
            if (raw == null) return null;
            if (target.isInstance(raw)) return raw;
            if (target == String.class) return String.valueOf(raw);
            if (target == int.class || target == Integer.class) return toInt(raw, key);
            if (target == boolean.class || target == Boolean.class) return toBool(raw, key);
            if (target == long.class || target == Long.class) return toLong(raw, key);
            if (target == double.class || target == Double.class) return toDouble(raw, key);
            throw new IllegalArgumentException("Unsupported return type " + target.getName() + " for key " + key);
        }
        private static int toInt(Object v, String key) {
            if (v instanceof Number) return ((Number) v).intValue();
            try { return Integer.parseInt(String.valueOf(v).trim()); }
            catch (Exception e) { throw new IllegalArgumentException("Invalid int for " + key + " value=" + v); }
        }
        private static long toLong(Object v, String key) {
            if (v instanceof Number) return ((Number) v).longValue();
            try { return Long.parseLong(String.valueOf(v).trim()); }
            catch (Exception e) { throw new IllegalArgumentException("Invalid long for " + key + " value=" + v); }
        }
        private static double toDouble(Object v, String key) {
            if (v instanceof Number) return ((Number) v).doubleValue();
            try { return Double.parseDouble(String.valueOf(v).trim()); }
            catch (Exception e) { throw new IllegalArgumentException("Invalid double for " + key + " value=" + v); }
        }
        private static boolean toBool(Object v, String key) {
            if (v instanceof Boolean) return (Boolean) v;
            String s = String.valueOf(v).trim().toLowerCase(Locale.ROOT);
            if ("true".equals(s)) return true;
            if ("false".equals(s)) return false;
            throw new IllegalArgumentException("Invalid boolean for " + key + " value=" + v);
        }
    }
}

/**
 * 概述
 * 定义 FTP 配置的接口视图 仅含 getter 与 isXxx
 * 功能清单
 * 提供访问 code name desc host port user password passiveMode
 * 使用示例
 * 参考 DemoMain
 * 注意事项
 * 方法名需与 YAML 键名的驼峰形式一致
 * 入参与出参与异常说明
 * 无入参 无返回异常
 */
interface IFtpServer {
    String getCode();
    String getName();
    String getDesc();
    String getHost();
    int getPort();
    String getUser();
    String getPassword();
    boolean isPassiveMode();
}

/**
 * 概述
 * 1 演示从类路径加载 config.yml
 * 2 将 ftp.servers 动态绑定为 IFtpServer 列表
 * 3 按 code 构建索引并读取
 * 功能清单
 * 加载 绑定 索引 读取
 * 使用示例
 * 运行 main 需在类路径放置 config.yml
 * 注意事项
 * 切勿在生产日志打印密码
 * 入参与出参与异常说明
 * 无入参 主动抛异常便于排障
 */
public class DemoMain {
    public static void main(String[] args) {
        Map<String, Object> root = YamlLoader.loadFromClasspath("config.yml");
        List<IFtpServer> servers = DynamicConfig.intoList(root, "ftp.servers", IFtpServer.class);
        Map<String, IFtpServer> byCode = DynamicConfig.indexBy(servers, IFtpServer::getCode);
        IFtpServer f = byCode.get("FGLS");
        if (f == null) throw new NoSuchElementException("FTP not found FGLS");
        System.out.println("Host=" + f.getHost() + " Port=" + f.getPort() + " Passive=" + f.isPassiveMode());
    }
}
```

### 使用说明
1. 定义接口视图: 以 getter 与 `isXxx` 命名，对应 YAML 键名的驼峰形式，例如 `passiveMode`
2. 绑定列表: `DynamicConfig.intoList(root, "ftp.servers", IFtpServer.class)` 即得只读代理列表，零显式对象构建
3. 构建索引: `DynamicConfig.indexBy(list, IFtpServer::getCode)` 获取 `Map<String,IFtpServer>`，重复键将抛出异常
4. 动态刷新: 当文件变化时重新 `YamlLoader.load...` 并再次 `intoList` 即可获得最新视图，无需迁移数据或复制字段

### 扩展建议
1. 可选默认值: 在 `ViewHandler.defaultValueFor` 处支持注解 `@Default("xx")` 提供缺省值，避免缺失即异常
2. 嵌套对象: 返回类型为接口时，可在 `convert` 中识别 `Map` 并递归绑定为子接口视图
3. 环境覆盖: 解析后合并 `System.getProperties` 或 `System.getenv` 对应键的覆盖值，实现多环境注入
4. 脱敏代理: 对 `getPassword` 等方法返回前做脱敏或透明解密，满足安全合规场景

## 重构: 根据入参

重构： java启动时，会传入 appEnv=dev，appEnv=test等环境参数. 在静态代码块中，或其他合适条件中，根据参数，得到config_dev.yml配置，解析配置，得到实体，以及对应的ftp客户端.  能获取所有实体、ftp客户端，也能根据code获取特定实体及ftp客户端



### 目标与方案总览

在任意 Java 程序中（不依赖 Spring），通过启动参数 `-DappEnv=dev` 或 `-DappEnv=test` 等，自动定位 `config_{env}.yml`，静态或懒加载解析为实体对象，并基于配置创建可复用的 FTP 客户端；支持获取全部实体与客户端，也可按 `code` 精确获取；支持热重载

### 最小 YAML 示例

```yaml
ftp:
  servers:
    - code: FGLS
      name: 辅助系统 FTP
      desc: 用于上传财务相关文件
      host: ftp.fgls.com
      port: 21
      user: u1
      password: p1
      passiveMode: true
    - code: NC
      name: NC 财务 FTP
      desc: 对接 NC 系统文件交换
      host: ftp.nc.com
      port: 21
      user: u2
      password: p2
      passiveMode: false
```

### 单文件可运行示例与工具类

```java
/**
 * 依赖说明
 * 1 解析 YAML 使用 SnakeYAML 1.33 及以上
 *    <dependency>
 *      <groupId>org.yaml</groupId>
 *      <artifactId>snakeyaml</artifactId>
 *      <version>1.33</version>
 *    </dependency>
 * 2 FTP 客户端使用 Apache Commons Net
 *    <dependency>
 *      <groupId>commons-net</groupId>
 *      <artifactId>commons-net</artifactId>
 *      <version>3.11.1</version>
 *    </dependency>
 */
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 概述
 * FTP 配置实体
 * 功能清单
 * 1 基础属性承载 code name desc host port user password passiveMode
 * 2 toString 脱敏输出
 * 使用示例
 * 由 FtpCenter 解析并返回实例
 * 注意事项
 * 避免将密码写入日志
 * 入参与出参与异常说明
 * 标准 POJO 无异常
 */
class FtpServer {
    private String code;
    private String name;
    private String desc;
    private String host;
    private int port;
    private String user;
    private String password;
    private boolean passiveMode;
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDesc() { return desc; }
    public void setDesc(String desc) { this.desc = desc; }
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public boolean isPassiveMode() { return passiveMode; }
    public void setPassiveMode(boolean passiveMode) { this.passiveMode = passiveMode; }
    @Override
    public String toString() {
        return "FtpServer{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", user='" + user + '\'' +
                ", passiveMode=" + passiveMode +
                '}';
    }
}

/**
 * 概述
 * 通用 YAML 加载器 支持类路径与文件路径 加点路径取子树
 * 功能清单
 * 1 loadFromClasspath 2 loadFromFile 3 getByPath 4 基础取值校验
 * 使用示例
 * FtpCenter 在静态初始化或热重载时调用
 * 注意事项
 * YAML 根必须为 Map
 * 入参与出参与异常说明
 * 读取失败抛 RuntimeException 缺字段抛 IllegalArgumentException
 */
final class YamlLoader {
    private YamlLoader() {}
    @SuppressWarnings("unchecked")
    static Map<String, Object> loadFromClasspath(String resource) {
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
            if (in == null) throw new RuntimeException("Classpath resource not found " + resource);
            Object obj = new Yaml(new SafeConstructor()).load(in);
            if (obj == null) return new LinkedHashMap<String, Object>();
            if (!(obj instanceof Map)) throw new RuntimeException("Root must be a map " + resource);
            return (Map<String, Object>) obj;
        } catch (Exception e) {
            throw new RuntimeException("Load YAML failed " + resource, e);
        }
    }
    @SuppressWarnings("unchecked")
    static Map<String, Object> loadFromFile(String filePath) {
        File f = new File(filePath);
        if (!f.exists()) throw new RuntimeException("File not found " + filePath);
        try (InputStream in = new FileInputStream(f)) {
            Object obj = new Yaml(new SafeConstructor()).load(in);
            if (obj == null) return new LinkedHashMap<String, Object>();
            if (!(obj instanceof Map)) throw new RuntimeException("Root must be a map " + filePath);
            return (Map<String, Object>) obj;
        } catch (Exception e) {
            throw new RuntimeException("Load YAML failed " + filePath, e);
        }
    }
    @SuppressWarnings("unchecked")
    static Object getByPath(Map<String, Object> root, String path) {
        Object cur = root;
        for (String p : path.split("\\.")) {
            if (!(cur instanceof Map)) return null;
            cur = ((Map<String, Object>) cur).get(p);
            if (cur == null) return null;
        }
        return cur;
    }
    static String reqStr(Map<String, Object> m, String k) {
        Object v = m.get(k);
        if (v == null) throw new IllegalArgumentException("Missing " + k);
        String s = String.valueOf(v).trim();
        if (s.isEmpty()) throw new IllegalArgumentException("Empty " + k);
        return s;
    }
    static int reqInt(Map<String, Object> m, String k) {
        Object v = m.get(k);
        if (v == null) throw new IllegalArgumentException("Missing " + k);
        if (v instanceof Number) return ((Number) v).intValue();
        try { return Integer.parseInt(String.valueOf(v).trim()); }
        catch (Exception e) { throw new IllegalArgumentException("Invalid int " + k + " val=" + v); }
    }
    static boolean reqBool(Map<String, Object> m, String k, boolean defVal) {
        Object v = m.get(k);
        if (v == null) return defVal;
        if (v instanceof Boolean) return (Boolean) v;
        String s = String.valueOf(v).trim().toLowerCase(Locale.ROOT);
        if ("true".equals(s)) return true;
        if ("false".equals(s)) return false;
        throw new IllegalArgumentException("Invalid boolean " + k + " val=" + v);
    }
}

/**
 * 概述
 * FTPClient 工厂 依据 FtpServer 构建已连接可用的 Apache Commons Net FTPClient
 * 功能清单
 * 1 buildAndConnect 根据配置创建并连接 可选被动模式 设置二进制传输
 * 2 safeClose 安全断开
 * 使用示例
 * FtpCenter.getClientByCode 内部调用并缓存
 * 注意事项
 * 建立连接后建议放入连接池或集中管理 这里提供每 code 单例复用
 * 入参与出参与异常说明
 * 连接失败抛 RuntimeException
 */
final class FtpClientFactory {
    private FtpClientFactory() {}
    public static FTPClient buildAndConnect(FtpServer cfg) {
        FTPClient c = new FTPClient();
        try {
            c.connect(cfg.getHost(), cfg.getPort());
            boolean ok = c.login(cfg.getUser(), cfg.getPassword());
            if (!ok) {
                safeClose(c);
                throw new RuntimeException("FTP login failed code=" + cfg.getCode());
            }
            if (cfg.isPassiveMode()) c.enterLocalPassiveMode();
            c.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
            c.setBufferSize(1024 * 64);
            c.setAutodetectUTF8(true);
            return c;
        } catch (SocketException e) {
            safeClose(c);
            throw new RuntimeException("FTP socket error code=" + cfg.getCode() + " msg=" + e.getMessage(), e);
        } catch (Exception e) {
            safeClose(c);
            throw new RuntimeException("FTP connect error code=" + cfg.getCode() + " msg=" + e.getMessage(), e);
        }
    }
    public static void safeClose(FTPClient c) {
        if (c == null) return;
        try { if (c.isConnected()) { c.logout(); c.disconnect(); } } catch (Exception ignore) {}
    }
}

/**
 * 概述
 * FtpCenter 负责 1 解析 config_{env}.yml 2 提供实体与客户端访问 3 支持热重载
 * 功能清单
 * 1 静态初始化 根据 -DappEnv 与可选 -DappConfigDir 决定配置路径
 * 2 getAllServers getServerByCode 获取实体
 * 3 getAllClients getClientByCode 获取 FTPClient 单例 并按需延迟创建
 * 4 reload 可在运行期重载配置 并安全替换客户端
 * 使用示例
 * 见 main 演示
 * 注意事项
 * 客户端按 code 单例复用 使用完不主动关闭 由中心统一管理 reload 时旧连接统一关闭
 * 入参与出参与异常说明
 * 初始化或解析失败抛 RuntimeException code 未找到抛 NoSuchElementException
 */
public final class FtpCenter {
    private static volatile Map<String, FtpServer> SERVERS = new LinkedHashMap<String, FtpServer>();
    private static final ConcurrentHashMap<String, FTPClient> CLIENTS = new ConcurrentHashMap<String, FTPClient>();
    private static volatile String CURRENT_ENV;
    private static volatile String CURRENT_SOURCE;
    static {
        initFromSystemProperties();
    }
    private FtpCenter() {}
    /**
     * 概述
     * 初始化 读取系统属性 构造配置文件名 并装载
     * 功能清单
     * 1 支持类路径优先 2 支持外部目录覆盖
     * 使用示例
     * 应用启动自动调用 也可主动调用 reload 切换
     * 注意事项
     * -DappEnv 缺省为 dev -DappConfigDir 指向外部目录可覆盖类路径
     * 入参与出参与异常说明
     * 无入参 异常同 reload
     */
    public static void initFromSystemProperties() {
        String env = System.getProperty("appEnv", "dev").trim();
        String dir = System.getProperty("appConfigDir", "").trim();
        String fileName = "config_" + env + ".yml";
        Map<String, Object> root;
        String source;
        if (!dir.isEmpty()) {
            String path = dir.endsWith("/") || dir.endsWith("\\") ? dir + fileName : dir + File.separator + fileName;
            root = YamlLoader.loadFromFile(path);
            source = path;
        } else {
            root = YamlLoader.loadFromClasspath(fileName);
            source = "classpath:" + fileName;
        }
        Map<String, FtpServer> parsed = parseServers(root, "ftp.servers");
        swapConfig(parsed, env, source);
    }
    /**
     * 概述
     * 运行期热重载 指定环境与可选目录
     * 功能清单
     * 1 重新解析 2 原子替换 3 关闭旧客户端
     * 使用示例
     * FtpCenter.reload("test", "/opt/app/conf")
     * 注意事项
     * 重载成功前异常不影响现有服务
     * 入参与出参与异常说明
     * env 目标环境 dir 可为空 解析失败抛 RuntimeException
     */
    public static void reload(String env, String dir) {
        if (env == null || env.trim().isEmpty()) throw new IllegalArgumentException("env empty");
        String fileName = "config_" + env + ".yml";
        Map<String, Object> root;
        String source;
        if (dir != null && !dir.trim().isEmpty()) {
            String path = dir.endsWith("/") || dir.endsWith("\\") ? dir + fileName : dir + File.separator + fileName;
            root = YamlLoader.loadFromFile(path);
            source = path;
        } else {
            root = YamlLoader.loadFromClasspath(fileName);
            source = "classpath:" + fileName;
        }
        Map<String, FtpServer> parsed = parseServers(root, "ftp.servers");
        swapConfig(parsed, env, source);
    }
    /**
     * 概述
     * 获取所有 FTP 配置实体
     * 功能清单
     * 返回不可变列表
     * 使用示例
     * FtpCenter.getAllServers()
     * 注意事项
     * 实体为不可变视图 修改请复制
     * 入参与出参与异常说明
     * 无入参 无异常
     */
    public static List<FtpServer> getAllServers() {
        return Collections.unmodifiableList(new ArrayList<FtpServer>(SERVERS.values()));
    }
    /**
     * 概述
     * 按 code 获取 FTP 配置实体
     * 功能清单
     * 精确匹配 大小写敏感
     * 使用示例
     * FtpCenter.getServerByCode("FGLS")
     * 注意事项
     * code 不存在将抛出异常
     * 入参与出参与异常说明
     * code 编码 返回 FtpServer 未找到抛 NoSuchElementException
     */
    public static FtpServer getServerByCode(String code) {
        FtpServer s = SERVERS.get(code);
        if (s == null) throw new NoSuchElementException("FTP not found code=" + code);
        return s;
    }
    /**
     * 概述
     * 获取所有 FTP 客户端 实例按 code 单例 需要时会连接并缓存
     * 功能清单
     * 1 懒加载构建并连接 2 返回不可变 Map 视图
     * 使用示例
     * FtpCenter.getAllClients()
     * 注意事项
     * 列表化访问将触发批量连接 仅在确需时使用
     * 入参与出参与异常说明
     * 无入参 连接失败抛 RuntimeException
     */
    public static Map<String, FTPClient> getAllClients() {
        Map<String, FTPClient> out = new LinkedHashMap<String, FTPClient>();
        for (String code : SERVERS.keySet()) {
            out.put(code, getClientByCode(code));
        }
        return Collections.unmodifiableMap(out);
    }
    /**
     * 概述
     * 按 code 获取 FTP 客户端 单例并懒连接
     * 功能清单
     * 1 computeIfAbsent 创建并连接 2 复用连接
     * 使用示例
     * FTPClient c = FtpCenter.getClientByCode("FGLS")
     * 注意事项
     * 客户端由中心统一管理 请勿手动 disconnect
     * 入参与出参与异常说明
     * code 编码 返回 FTPClient 连接失败抛 RuntimeException
     */
    public static FTPClient getClientByCode(String code) {
        FtpServer cfg = getServerByCode(code);
        return CLIENTS.computeIfAbsent(code, k -> FtpClientFactory.buildAndConnect(cfg));
    }
    /**
     * 概述
     * 当前环境与来源信息
     * 功能清单
     * 便于日志与排障
     * 使用示例
     * FtpCenter.currentEnv()
     * 注意事项
     * 只读
     * 入参与出参与异常说明
     * 无入参 无异常
     */
    public static String currentEnv() { return CURRENT_ENV; }
    public static String currentSource() { return CURRENT_SOURCE; }

    private static Map<String, FtpServer> parseServers(Map<String, Object> root, String path) {
        Object node = YamlLoader.getByPath(root, path);
        if (!(node instanceof List)) throw new RuntimeException("Path not a list " + path);
        List<?> arr = (List<?>) node;
        LinkedHashMap<String, FtpServer> map = new LinkedHashMap<String, FtpServer>();
        for (Object o : arr) {
            if (!(o instanceof Map)) throw new RuntimeException("Item not a map " + o);
            @SuppressWarnings("unchecked")
            Map<String, Object> m = (Map<String, Object>) o;
            FtpServer s = new FtpServer();
            s.setCode(YamlLoader.reqStr(m, "code"));
            s.setName(YamlLoader.reqStr(m, "name"));
            s.setDesc(String.valueOf(m.getOrDefault("desc", "")));
            s.setHost(YamlLoader.reqStr(m, "host"));
            s.setPort(YamlLoader.reqInt(m, "port"));
            s.setUser(YamlLoader.reqStr(m, "user"));
            s.setPassword(YamlLoader.reqStr(m, "password"));
            s.setPassiveMode(YamlLoader.reqBool(m, "passiveMode", false));
            if (map.containsKey(s.getCode())) throw new RuntimeException("Duplicate code " + s.getCode());
            map.put(s.getCode(), s);
        }
        return Collections.unmodifiableMap(map);
    }
    private static synchronized void swapConfig(Map<String, FtpServer> newServers, String env, String source) {
        Map<String, FTPClient> old = new LinkedHashMap<String, FTPClient>(CLIENTS);
        CLIENTS.clear();
        SERVERS = newServers;
        CURRENT_ENV = env;
        CURRENT_SOURCE = source;
        for (FTPClient c : old.values()) {
            FtpClientFactory.safeClose(c);
        }
    }
    /**
     * 概述
     * 演示用 main
     * 功能清单
     * 1 打印当前环境与来源 2 列表实体 3 按 code 获取并测试 NOOP
     * 使用示例
     * java -DappEnv=dev -cp ... Main
     * 注意事项
     * 需要在类路径或外部目录提供 config_dev.yml
     * 入参与出参与异常说明
     * 无入参 连接失败将抛异常
     */
    public static void main(String[] args) throws Exception {
        System.out.println("Env=" + currentEnv() + " Source=" + currentSource());
        for (FtpServer s : getAllServers()) {
            System.out.println("CFG " + s);
        }
        // 示例 仅在可达的 FTP 环境下运行
        // FTPClient client = getClientByCode("FGLS");
        // System.out.println("NOOP reply=" + client.noop());
    }
}
```

### 使用方法

1. 启动参数

- 使用类路径配置：在资源目录放置 `config_dev.yml`、`config_test.yml`，启动时加 `-DappEnv=dev`
- 使用外部目录：`-DappEnv=test -DappConfigDir=/opt/app/conf` 并在该目录放置 `config_test.yml`

1. 代码调用

- 获取实体列表：`List<FtpServer> all = FtpCenter.getAllServers()`
- 获取单个实体：`FtpServer s = FtpCenter.getServerByCode("FGLS")`
- 获取 FTP 客户端：`FTPClient c = FtpCenter.getClientByCode("FGLS")`
- 热重载：`FtpCenter.reload("prod", "/opt/app/conf")`

1. 线程安全与资源管理

- 客户端按 `code` 单例并懒连接，集中由 `FtpCenter` 管理；调用方不应主动 `disconnect`
- 调用 `reload` 时，旧客户端会被安全关闭，避免泄露

### 设计要点与可选增强

1. 环境推断顺序

- 先读 `-DappEnv`，再读 `APP_ENV` 环境变量（如需，可在 `initFromSystemProperties` 中补充），最后默认 `dev`

1. 健康检查与重连

- 可在 `getClientByCode` 中检测 `isAvailable`，若断开则重建；也可增加定时器做保活 `NOOP`

1. 连接池化

- 若高并发上传下载，建议为每个 `code` 引入连接池（如 Commons Pool 包装 FTPClient），此处给出单例简化版

1. 安全

- 支持密码解密：在 `parseServers` 时对 `password` 应用解密器接口
- 日志脱敏：`toString` 已不输出密码，如需更严格可拦截所有包含 `password` 的日志输出

1. 配置校验

- 可增加端口范围校验、host 格式校验，以及必需字段的更详细错误上下文信息

### 启动与排障清单

1. 确认 `-DappEnv` 与目标文件名一致，如 `dev` 对应 `config_dev.yml`
2. 使用外部目录时必须设置 `-DappConfigDir` 并确保进程有读权限
3. 连接失败多半为网络、防火墙或凭据问题；本示例默认二进制模式并可选被动模式，可按实际环境调整

## 进一步优化

优化点: 

1. 环境推断顺序 : 先读 -DappEnv，再读 APP_ENV 环境变量（如需，可在 initFromSystemProperties 中补充），最后默认 dev 	
2. 健康检查与重连: 可在 getClientByCode 中检测 isAvailable，若断开则重建；也可增加定时器做保活 NOOP

### 优化点一 环境推断顺序与定位策略

```java
/**
 * 概述
 * 解析运行环境与配置定位 优先级支持覆盖 单测友好
 * 功能清单
 * 1 resolveEnv 解析环境 appEnv 系统属性优先 其次 APP_ENV 环境变量 最后默认 dev
 * 2 resolveConfig 解析配置来源 支持单文件与目录两种模式
 * 使用示例
 * String env = EnvResolver.resolveEnv();
 * EnvResolver.ConfigLoc loc = EnvResolver.resolveConfig(env);
 * 注意事项
 * 允许通过 -DappConfigFile 精确指定文件 优先级高于目录模式
 * 入参与出参与异常说明
 * 入参 env 环境名 返回 ConfigLoc 包含 source 与 isClasspath 抛出 IllegalArgumentException 当文件不存在
 */
final class EnvResolver {
    private EnvResolver() {}
    public static String resolveEnv() {
        String fromProp = System.getProperty("appEnv");
        if (fromProp != null && !fromProp.trim().isEmpty()) return fromProp.trim();
        String fromEnv = System.getenv("APP_ENV");
        if (fromEnv != null && !fromEnv.trim().isEmpty()) return fromEnv.trim();
        return "dev";
    }
    public static ConfigLoc resolveConfig(String env) {
        String explicitFile = System.getProperty("appConfigFile"); // 绝对或相对路径 优先
        if (explicitFile != null && !explicitFile.trim().isEmpty()) {
            java.io.File f = new java.io.File(explicitFile);
            if (!f.exists() || !f.isFile()) throw new IllegalArgumentException("Config file not found " + explicitFile);
            return new ConfigLoc(explicitFile, false);
        }
        String dir = System.getProperty("appConfigDir"); // 外部目录次之
        String fileName = "config_" + env + ".yml";
        if (dir != null && !dir.trim().isEmpty()) {
            String path = dir.endsWith("/") || dir.endsWith("\\") ? dir + fileName : dir + java.io.File.separator + fileName;
            java.io.File f = new java.io.File(path);
            if (!f.exists() || !f.isFile()) throw new IllegalArgumentException("Config file not found " + path);
            return new ConfigLoc(path, false);
        }
        // 最后回退类路径
        java.io.InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        if (in == null) throw new IllegalArgumentException("Classpath resource not found " + fileName);
        try { in.close(); } catch (Exception ignore) {}
        return new ConfigLoc("classpath:" + fileName, true);
    }
    public static final class ConfigLoc {
        private final String source;
        private final boolean classpath;
        public ConfigLoc(String source, boolean classpath) { this.source = source; this.classpath = classpath; }
        public String getSource() { return source; }
        public boolean isClasspath() { return classpath; }
    }
}
```

### 优化点二 健康检查 保活与自动重连

```java
import org.apache.commons.net.ftp.FTPClient;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 概述
 * 在获取客户端时做健康检查 失败则原子重建 另提供定时 NOOP 保活
 * 功能清单
 * 1 getClientByCode 带健康检查与自动重连
 * 2 isHealthy 判定可用性 noop 与 isConnected 双重校验
 * 3 startKeepAlive 启动全局定时 NOOP 低成本保活 可选
 * 使用示例
 * FTPClient c = FtpCenter.getClientByCode("FGLS")
 * FtpCenter.startKeepAlive(30000) // 可选 每 30s 保活一次
 * 注意事项
 * 重连过程对调用方透明 compute 逻辑避免并发重复重连
 * 入参与出参与异常说明
 * code 编码 返回可用客户端 连接失败抛 RuntimeException
 */
public final class FtpCenter {
    private static final ConcurrentHashMap<String, FTPClient> CLIENTS = new ConcurrentHashMap<String, FTPClient>();
    private static volatile Map<String, FtpServer> SERVERS = new java.util.LinkedHashMap<String, FtpServer>();
    private static volatile Timer KEEPALIVE_TIMER;

    public static FTPClient getClientByCode(String code) {
        FtpServer cfg = getServerByCode(code);
        return CLIENTS.compute(code, (k, existing) -> {
            if (isHealthy(existing)) return existing;
            safeClose(existing);
            return FtpClientFactory.buildAndConnect(cfg);
        });
    }

    private static boolean isHealthy(FTPClient c) {
        if (c == null) return false;
        if (!c.isConnected()) return false;
        try { return c.sendNoOp(); } catch (Exception e) { return false; }
    }

    public static void startKeepAlive(long periodMillis) {
        if (periodMillis <= 0) return;
        synchronized (FtpCenter.class) {
            if (KEEPALIVE_TIMER != null) return;
            KEEPALIVE_TIMER = new Timer("ftp-keepalive", true);
            KEEPALIVE_TIMER.scheduleAtFixedRate(new TimerTask() {
                @Override public void run() {
                    for (Map.Entry<String, FTPClient> e : CLIENTS.entrySet()) {
                        String code = e.getKey();
                        FTPClient c = e.getValue();
                        if (!isHealthy(c)) {
                            try {
                                // 尝试透明重连
                                CLIENTS.compute(code, (k, cur) -> {
                                    if (isHealthy(cur)) return cur;
                                    safeClose(cur);
                                    FtpServer cfg = SERVERS.get(k);
                                    return cfg == null ? null : FtpClientFactory.buildAndConnect(cfg);
                                });
                            } catch (Throwable ignore) {}
                        } else {
                            try { c.sendNoOp(); } catch (Throwable ignore) {}
                        }
                    }
                }
            }, periodMillis, periodMillis);
        }
    }

    public static void stopKeepAlive() {
        synchronized (FtpCenter.class) {
            if (KEEPALIVE_TIMER != null) { KEEPALIVE_TIMER.cancel(); KEEPALIVE_TIMER = null; }
        }
    }

    private static void safeClose(FTPClient c) { FtpClientFactory.safeClose(c); }

    public static FtpServer getServerByCode(String code) {
        FtpServer s = SERVERS.get(code);
        if (s == null) throw new NoSuchElementException("FTP not found code=" + code);
        return s;
    }

    // 你现有的解析装载与 reload 成员保持不变 仅在成功解析后原子替换 SERVERS 并关闭旧 CLIENTS
}
```

### 接入指引与测试建议

1. 启动参数范式

- 单文件优先: `-DappConfigFile=/opt/app/conf/config_test.yml`
- 目录模式: `-DappEnv=test -DappConfigDir=/opt/app/conf`
- 类路径回退: 仅 `-DappEnv=prod` 且打包内含 `config_prod.yml`

1. 健康检查与重连

- 默认在 `getClientByCode` 做一次性校验与重连
- 需要长期存活时再启用保活: `FtpCenter.startKeepAlive(30000)`

1. 线程安全

- 采用 `ConcurrentHashMap.compute` 原子替换 避免并发重连风暴

1. 观测与排障

- 在 `FtpClientFactory.buildAndConnect` 中添加最小化日志 记录 code host port 与异常摘要
- 建议在保活线程中对连续重连失败做限速或计数报警 可再加指数退避策略

# 是启动时时就加载配置及提前准备好client好，还是每次请求准备好？

###### 

是启动时时就加载配置及提前准备好client好，还是每次请求准备好？

###### 

### 短答

先启动时加载配置并做全面校验，但客户端不要一股脑建好。用“按需连接 + 小规模预热 + 连接池”的混合策略更稳：
 启动即解析并校验 `config_{env}.yml`，关键通道可做有限预热；其余在首用时建立连接并放入每个 `code` 的池中，配合健康检查与自动重连

### 原因与取舍

- 启动即建好全部客户端
- 优点
- 失败早暴露、关键链路首包延迟低
- 缺点
- FTPClient 非线程安全且易掉线，长驻连接易失活；占用端口与资源；无用连接也被创建
- 每次请求现建现用
- 优点
- 资源友好、稳妥、失败仅影响当次请求
- 缺点
- 首次连接有明显握手开销；高频场景重复建连成本高
- 混合策略（推荐）
- 启动：加载与校验配置、可选预热关键 `code`
- 运行：按需从池获取，不可用则自动重连；定时保活
- 效果：在可靠性、冷启动、资源占用之间取得最佳平衡

### 实操建议与开关

1. 启动阶段

- 必做
- 解析 `config_{env}.yml`；校验必填项、端口范围、重复 `code`；可做 DNS 预解析
- 可选
- 对“关键 `code`”执行连接预热与目录可达性检查

1. 运行阶段

- 每个 `code` 维护一个小型连接池（例如 minIdle=0~~1，maxTotal=2~~5），借还即用；池内连接失效检测与重连
- 定时保活 `NOOP`，失败则移除并重建
- 注意 FTPClient 不线程安全，严禁多线程共享同一实例

1. 开关设计

```java
public final class FtpOptions {
    public static boolean eagerLoadConfig = true;       // 启动即加载配置
    public static boolean eagerWarmupCritical = true;   // 启动预热关键 code
    public static Set<String> criticalCodes = new java.util.HashSet<>();
    public static int poolMaxPerCode = 4;
    public static int poolMinIdlePerCode = 1;           // 0 表示纯懒加载
    public static long keepAliveMillis = 30000;         // 0 关闭保活
}
```

### 代码骨架对照（你现有 FtpCenter 的最小改造）

```java
// 1 启动即加载并校验配置
static {
    if (FtpOptions.eagerLoadConfig) {
        FtpCenter.initFromSystemProperties(); // 仅解析配置与构建索引, 不批量建连
        if (FtpOptions.eagerWarmupCritical && !FtpOptions.criticalCodes.isEmpty()) {
            for (String code : FtpOptions.criticalCodes) {
                try { FtpPool.borrow(code).close(); } catch (Exception ignore) {}
            }
        }
        if (FtpOptions.keepAliveMillis > 0) {
            FtpCenter.startKeepAlive(FtpOptions.keepAliveMillis);
        }
    }
}

// 2 按需获取连接 改为从池里借出
public static FTPClient getClientForUse(String code) {
    return FtpPool.borrow(code); // 借到的是可用连接, 可能是新建或复用
}

// 3 用完务必归还
public static void returnClient(String code, FTPClient client) {
    FtpPool.giveBack(code, client);
}
```

### 一个极简池化实现思路

- 结构：`Map<String, Deque<FTPClient>> idle` + `Map<String, AtomicInteger> total`
- 借出
- 先弹出空闲，健康检查失败则丢弃并尝试新建
- 不足且未达 `maxTotal` 则新建并连接
- 超限则等待或直接新建短连接（看你的业务 SLA）
- 归还
- 健康则入栈；超出 `minIdle` 时可主动关闭裁剪
- 健康检查
- 借出与保活双重保障；`sendNoOp()` 失败判不健康

### 何时选择“启动就建好全部客户端”

- 系统为短时批处理任务，启动后立即高强度批量传输，且代码明确在短窗口内使用所有 `code`
- FTP 侧网络稳定（同局域网）、账号限并发连接数极大且不敏感资源占用

### 何时选择“每次请求现建现用”

- 低频、偶发传输任务；或运行环境多变（NAT、容器漂移）导致连接易断；或安全策略要求“用完即断”

### 推荐默认

- 启动：加载与校验配置；仅对关键 `code` 预热
- 运行：小池化按需借还 + 保活 + 自动重连
- 监控：统计借还次数、重连次数、保活失败次数，超阈值告警

如你愿意，我可以把你现有的 `FtpCenter` 改造成上述“每 code 小池化 + 关键预热”的完整可替换类，并保留你当前的 API 形态。

# 重构

重构架构： 启动时，先根据入参，加载类路径下的配置，得到所有的实体、并能根据code获取特定实体. 每次请求时，根据code，建立ftp client

###### 

### 架构目标

启动阶段只做“配置加载与实体就绪”，运行阶段“按请求即时建连”。类路径下按 `-DappEnv=dev|test|prod` 选择 `config_{env}.yml`，启动时解析得到全部 `FtpServer` 实体与按 `code` 的索引；每次请求时基于 `code` 临时建立 `FTPClient`，用完即断开

### 最小配置示例

```yaml
ftp:
  servers:
    - code: FGLS
      name: 辅助系统 FTP
      desc: 财务相关文件
      host: 10.0.0.10
      port: 21
      user: u1
      password: p1
      passiveMode: true
    - code: NC
      name: NC 系统 FTP
      desc: NC 交换
      host: 10.0.0.20
      port: 21
      user: u2
      password: p2
      passiveMode: false
```

### 单文件实现示例（JDK8 兼容）

```java
/**
 * 依赖
 * 1 SnakeYAML
 *   <dependency>
 *     <groupId>org.yaml</groupId>
 *     <artifactId>snakeyaml</artifactId>
 *     <version>1.33</version>
 *   </dependency>
 * 2 Apache Commons Net
 *   <dependency>
 *     <groupId>commons-net</groupId>
 *     <artifactId>commons-net</artifactId>
 *     <version>3.11.1</version>
 *   </dependency>
 */
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import java.io.InputStream;
import java.net.SocketException;
import java.util.*;
/**
 * 概述
 * FtpServer 实体承载单个 FTP 节点配置
 * 功能清单
 * code name desc host port user password passiveMode 基础访问方法
 * 使用示例
 * FtpRuntime.getServerByCode("FGLS")
 * 注意事项
 * toString 不输出密码 避免泄露
 * 入参与出参与异常说明
 * 标准 POJO 无异常
 */
class FtpServer {
    private String code;
    private String name;
    private String desc;
    private String host;
    private int port;
    private String user;
    private String password;
    private boolean passiveMode;
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDesc() { return desc; }
    public void setDesc(String desc) { this.desc = desc; }
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public boolean isPassiveMode() { return passiveMode; }
    public void setPassiveMode(boolean passiveMode) { this.passiveMode = passiveMode; }
    @Override
    public String toString() {
        return "FtpServer{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", user='" + user + '\'' +
                ", passiveMode=" + passiveMode +
                '}';
    }
}
/**
 * 概述
 * 环境解析工具 按优先级解析 env 并返回对应类路径文件名
 * 功能清单
 * resolveEnv 解析 -DappEnv 或环境变量 APP_ENV 缺省 dev
 * resolveClasspathResource 生成类路径文件名 config_{env}.yml 并校验存在
 * 使用示例
 * String env = EnvResolver.resolveEnv()
 * String res = EnvResolver.resolveClasspathResource(env)
 * 注意事项
 * 仅查找类路径资源 不读取外部目录
 * 入参与出参与异常说明
 * 资源不存在抛 IllegalArgumentException
 */
final class EnvResolver {
    private EnvResolver() {}
    public static String resolveEnv() {
        String p = System.getProperty("appEnv");
        if (p != null && !p.trim().isEmpty()) return p.trim();
        String e = System.getenv("APP_ENV");
        if (e != null && !e.trim().isEmpty()) return e.trim();
        return "dev";
    }
    public static String resolveClasspathResource(String env) {
        String name = "config_" + env + ".yml";
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
        if (in == null) throw new IllegalArgumentException("Classpath resource not found " + name);
        try { in.close(); } catch (Exception ignore) {}
        return name;
    }
}
/**
 * 概述
 * YAML 加载小工具 解析根 Map 与按点路径取子树
 * 功能清单
 * loadFromClasspath 加载 YAML 为 Map
 * getByPath 使用 a.b.c 取子树
 * reqStr reqInt reqBool 基本校验与转换
 * 使用示例
 * Map root = YamlLoader.loadFromClasspath("config_dev.yml")
 * 注意事项
 * 根必须为 Map
 * 入参与出参与异常说明
 * 解析失败抛 RuntimeException 缺字段抛 IllegalArgumentException
 */
final class YamlLoader {
    private YamlLoader() {}
    @SuppressWarnings("unchecked")
    public static Map<String, Object> loadFromClasspath(String resource) {
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
            if (in == null) throw new RuntimeException("Classpath resource not found " + resource);
            Object obj = new Yaml(new SafeConstructor()).load(in);
            if (obj == null) return new LinkedHashMap<String, Object>();
            if (!(obj instanceof Map)) throw new RuntimeException("Root must be a map: " + resource);
            return (Map<String, Object>) obj;
        } catch (Exception e) {
            throw new RuntimeException("Load YAML failed: " + resource, e);
        }
    }
    @SuppressWarnings("unchecked")
    public static Object getByPath(Map<String, Object> root, String path) {
        Object cur = root;
        for (String p : path.split("\\.")) {
            if (!(cur instanceof Map)) return null;
            cur = ((Map<String, Object>) cur).get(p);
            if (cur == null) return null;
        }
        return cur;
    }
    public static String reqStr(Map<String, Object> m, String k) {
        Object v = m.get(k);
        if (v == null) throw new IllegalArgumentException("Missing " + k);
        String s = String.valueOf(v).trim();
        if (s.isEmpty()) throw new IllegalArgumentException("Empty " + k);
        return s;
    }
    public static int reqInt(Map<String, Object> m, String k) {
        Object v = m.get(k);
        if (v == null) throw new IllegalArgumentException("Missing " + k);
        if (v instanceof Number) return ((Number) v).intValue();
        try { return Integer.parseInt(String.valueOf(v).trim()); }
        catch (Exception e) { throw new IllegalArgumentException("Invalid int " + k + " val=" + v); }
    }
    public static boolean optBool(Map<String, Object> m, String k, boolean defVal) {
        Object v = m.get(k);
        if (v == null) return defVal;
        if (v instanceof Boolean) return (Boolean) v;
        String s = String.valueOf(v).trim().toLowerCase(Locale.ROOT);
        if ("true".equals(s)) return true;
        if ("false".equals(s)) return false;
        throw new IllegalArgumentException("Invalid boolean " + k + " val=" + v);
    }
}
/**
 * 概述
 * 运行期单例 FtpRuntime
 * 启动时解析类路径配置 得到所有实体与按 code 的索引
 * 每次请求按 code 即时创建 FTPClient 用完即断
 * 功能清单
 * initFromClasspath 根据 appEnv 解析 config_{env}.yml 并构建索引
 * getAllServers 列出所有实体
 * getServerByCode 按 code 获取实体 不存在抛异常
 * createClientByCode 基于实体即时建立 FTP 连接
 * safeClose 安全断开连接
 * 使用示例
 * FtpRuntime.initFromClasspath()
 * FTPClient c = FtpRuntime.createClientByCode("FGLS"); try { ... } finally { FtpRuntime.safeClose(c); }
 * 注意事项
 * FTPClient 非线程安全 每次请求需独立实例 用完务必关闭
 * 入参与出参与异常说明
 * 初始化失败抛 RuntimeException 获取不存在 code 抛 NoSuchElementException 建连失败抛 RuntimeException
 */
public final class FtpRuntime {
    private static volatile Map<String, FtpServer> BY_CODE = new LinkedHashMap<String, FtpServer>();
    private static volatile String CURRENT_ENV;
    private static volatile String CURRENT_RESOURCE;
    private FtpRuntime() {}
    static {
        initFromClasspath();
    }
    public static void initFromClasspath() {
        String env = EnvResolver.resolveEnv();
        String res = EnvResolver.resolveClasspathResource(env);
        Map<String, Object> root = YamlLoader.loadFromClasspath(res);
        Map<String, FtpServer> parsed = parseServers(root, "ftp.servers");
        BY_CODE = parsed;
        CURRENT_ENV = env;
        CURRENT_RESOURCE = res;
    }
    public static List<FtpServer> getAllServers() {
        return Collections.unmodifiableList(new ArrayList<FtpServer>(BY_CODE.values()));
    }
    public static FtpServer getServerByCode(String code) {
        FtpServer s = BY_CODE.get(code);
        if (s == null) throw new NoSuchElementException("FTP not found code=" + code);
        return s;
    }
    public static FTPClient createClientByCode(String code) {
        FtpServer cfg = getServerByCode(code);
        return createClient(cfg);
    }
    public static FTPClient createClient(FtpServer cfg) {
        FTPClient c = new FTPClient();
        try {
            c.connect(cfg.getHost(), cfg.getPort());
            boolean ok = c.login(cfg.getUser(), cfg.getPassword());
            if (!ok) {
                safeClose(c);
                throw new RuntimeException("FTP login failed code=" + cfg.getCode());
            }
            if (cfg.isPassiveMode()) c.enterLocalPassiveMode();
            c.setFileType(FTP.BINARY_FILE_TYPE);
            c.setBufferSize(64 * 1024);
            c.setAutodetectUTF8(true);
            return c;
        } catch (SocketException e) {
            safeClose(c);
            throw new RuntimeException("FTP socket error code=" + cfg.getCode() + " msg=" + e.getMessage(), e);
        } catch (Exception e) {
            safeClose(c);
            throw new RuntimeException("FTP connect error code=" + cfg.getCode() + " msg=" + e.getMessage(), e);
        }
    }
    public static void safeClose(FTPClient c) {
        if (c == null) return;
        try { if (c.isConnected()) { c.logout(); c.disconnect(); } } catch (Exception ignore) {}
    }
    public static String currentEnv() { return CURRENT_ENV; }
    public static String currentResource() { return CURRENT_RESOURCE; }
    @SuppressWarnings("unchecked")
    private static Map<String, FtpServer> parseServers(Map<String, Object> root, String path) {
        Object node = YamlLoader.getByPath(root, path);
        if (!(node instanceof List)) throw new RuntimeException("Path not a list " + path);
        List<?> arr = (List<?>) node;
        LinkedHashMap<String, FtpServer> map = new LinkedHashMap<String, FtpServer>();
        for (Object o : arr) {
            if (!(o instanceof Map)) throw new RuntimeException("Item not a map " + o);
            Map<String, Object> m = (Map<String, Object>) o;
            FtpServer s = new FtpServer();
            s.setCode(YamlLoader.reqStr(m, "code"));
            s.setName(YamlLoader.reqStr(m, "name"));
            s.setDesc(String.valueOf(m.getOrDefault("desc", "")));
            s.setHost(YamlLoader.reqStr(m, "host"));
            s.setPort(YamlLoader.reqInt(m, "port"));
            s.setUser(YamlLoader.reqStr(m, "user"));
            s.setPassword(YamlLoader.reqStr(m, "password"));
            s.setPassiveMode(YamlLoader.optBool(m, "passiveMode", false));
            if (map.containsKey(s.getCode())) throw new RuntimeException("Duplicate code " + s.getCode());
            map.put(s.getCode(), s);
        }
        return Collections.unmodifiableMap(map);
    }
}
/**
 * 概述
 * 演示入口
 * 功能清单
 * 1 打印当前环境与配置来源
 * 2 列出所有实体
 * 3 按 code 临时建立连接并 NOOP 测试
 * 使用示例
 * java -DappEnv=dev -cp ... DemoMain
 * 注意事项
 * 请在类路径提供 config_dev.yml 并确保目标 FTP 可达
 * 入参与出参与异常说明
 * 连接失败将抛 RuntimeException
 */
class DemoMain {
    public static void main(String[] args) throws Exception {
        System.out.println("env=" + FtpRuntime.currentEnv() + " resource=" + FtpRuntime.currentResource());
        for (FtpServer s : FtpRuntime.getAllServers()) {
            System.out.println("CFG " + s);
        }
        // 示例 调用时请根据实际 code 替换
        // FTPClient c = FtpRuntime.createClientByCode("FGLS");
        // try { System.out.println("NOOP=" + c.sendNoOp()); } finally { FtpRuntime.safeClose(c); }
    }
}
```

### 设计说明

1. 启动即加载配置与实体索引在 `FtpRuntime` 静态块完成 支持 `-DappEnv` 或 `APP_ENV` 选择类路径下 `config_{env}.yml`
2. 运行时每次请求创建独立 `FTPClient` 用完即通过 `safeClose` 断开 保证线程隔离与资源可控
3. 如需切换环境可在运行期调用 `FtpRuntime.initFromClasspath()` 前先设置新的 `-DappEnv` 值或通过配置中心刷新资源
4. 可选增强

- 加入白名单字段校验与端口范围校验
- 提供 `createClientWithTimeouts` 设置 `connectTimeout` 与 `dataTimeout`
- 若后续需要降本提效 可以在此架构上引入小型连接池 但与本需求“每次请求建连”相矛盾 请按业务选择

# 重构

重构架构： 启动时，先根据入参，加载类路径下的配置，得到所有的实体、并能根据code获取特定实体. 每次请求时，根据code，建立ftp client



### 架构说明

启动阶段仅完成“配置解析与实体索引”；运行阶段按请求基于`code`即时创建`FTPClient`，用完即断开。通过`-DappEnv=dev|test|prod`从类路径选择`config_{env}.yml`；可列出全部实体，也可按`code`获取特定实体；不依赖 Spring，JDK8 可用

### 关键点

1. 启动即加载: 静态初始化解析 YAML，校验字段，建立`code→实体`只读索引
2. 运行时建连: 每次请求使用`FtpClientFactory.create()`按`code`新建并登录`FTPClient`，调用方用完后关闭
3. 易扩展: 支持超时、被动模式、二进制传输、UTF‑8 自动检测；可平滑扩展到连接池而不改调用方

### 单文件实现（JDK8 兼容）

```java
/**
 * 概述
 * 以 -DappEnv=dev|test|prod 选择类路径下 config_{env}.yml
 * 启动时加载配置为实体索引；每次请求按 code 临时建连并返回 FTPClient
 * 功能清单
 * 1 EnvResolver 解析环境与资源名
 * 2 YamlLoader 加载 YAML 与字段校验
 * 3 FtpServer 配置实体
 * 4 FtpConfigRegistry 启动期加载并索引实体
 * 5 FtpClientFactory 按 code 创建 FTPClient
 * 使用示例
 * FtpServer s = FtpConfigRegistry.getServerByCode("FGLS");
 * FTPClient c = FtpClientFactory.create("FGLS"); try { ... } finally { FtpClientFactory.safeClose(c); }
 * 注意事项
 * 1 FTPClient 非线程安全 每次请求独立实例 用完务必关闭
 * 2 请在类路径提供 config_{env}.yml
 * 入参与出参与异常说明
 * 解析失败与连接失败抛 RuntimeException 未找到 code 抛 NoSuchElementException
 */
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import java.io.InputStream;
import java.net.SocketException;
import java.util.*;

/** 环境解析工具 */
final class EnvResolver {
    private EnvResolver() {}
    /**
     * 概述
     * 解析运行环境 优先系统属性 appEnv 其次环境变量 APP_ENV 默认 dev
     * 功能清单
     * 1 读取系统属性与环境变量
     * 2 返回标准化 env 字符串
     * 使用示例
     * String env = EnvResolver.resolveEnv();
     * 注意事项
     * 仅用于选择类路径资源
     * 入参与出参与异常说明
     * 无入参 无异常
     */
    public static String resolveEnv() {
        String p = System.getProperty("appEnv");
        if (p != null && !p.trim().isEmpty()) return p.trim();
        String e = System.getenv("APP_ENV");
        if (e != null && !e.trim().isEmpty()) return e.trim();
        return "dev";
    }
    /**
     * 概述
     * 生成类路径资源名并校验存在
     * 功能清单
     * 1 组装 config_{env}.yml
     * 2 检查资源是否存在
     * 使用示例
     * String res = EnvResolver.resolveClasspathResource("dev");
     * 注意事项
     * 仅校验存在性 不解析
     * 入参与出参与异常说明
     * 资源不存在抛 IllegalArgumentException
     */
    public static String resolveClasspathResource(String env) {
        String name = "config_" + env + ".yml";
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
        if (in == null) throw new IllegalArgumentException("Classpath resource not found " + name);
        try { in.close(); } catch (Exception ignore) {}
        return name;
    }
}

/** YAML 解析与取值校验 */
final class YamlLoader {
    private YamlLoader() {}
    /**
     * 概述
     * 从类路径读取 YAML 为 Map
     * 功能清单
     * 1 以 SafeConstructor 解析
     * 2 根节点必须为 Map
     * 使用示例
     * Map root = YamlLoader.loadFromClasspath("config_dev.yml");
     * 注意事项
     * 禁止将敏感值写入日志
     * 入参与出参与异常说明
     * 读取失败或结构错误抛 RuntimeException
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> loadFromClasspath(String resource) {
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
            if (in == null) throw new RuntimeException("Classpath resource not found " + resource);
            Object obj = new Yaml(new SafeConstructor()).load(in);
            if (obj == null) return new LinkedHashMap<String, Object>();
            if (!(obj instanceof Map)) throw new RuntimeException("Root must be a map: " + resource);
            return (Map<String, Object>) obj;
        } catch (Exception e) {
            throw new RuntimeException("Load YAML failed: " + resource, e);
        }
    }
    /** 点路径子树获取 */
    @SuppressWarnings("unchecked")
    public static Object getByPath(Map<String, Object> root, String path) {
        Object cur = root;
        for (String p : path.split("\\.")) {
            if (!(cur instanceof Map)) return null;
            cur = ((Map<String, Object>) cur).get(p);
            if (cur == null) return null;
        }
        return cur;
    }
    /** 必填字符串 */
    public static String reqStr(Map<String, Object> m, String k) {
        Object v = m.get(k);
        if (v == null) throw new IllegalArgumentException("Missing " + k);
        String s = String.valueOf(v).trim();
        if (s.isEmpty()) throw new IllegalArgumentException("Empty " + k);
        return s;
    }
    /** 必填整型 */
    public static int reqInt(Map<String, Object> m, String k) {
        Object v = m.get(k);
        if (v == null) throw new IllegalArgumentException("Missing " + k);
        if (v instanceof Number) return ((Number) v).intValue();
        try { return Integer.parseInt(String.valueOf(v).trim()); }
        catch (Exception e) { throw new IllegalArgumentException("Invalid int " + k + " val=" + v); }
    }
    /** 可选布尔 */
    public static boolean optBool(Map<String, Object> m, String k, boolean defVal) {
        Object v = m.get(k);
        if (v == null) return defVal;
        if (v instanceof Boolean) return (Boolean) v;
        String s = String.valueOf(v).trim().toLowerCase(Locale.ROOT);
        if ("true".equals(s)) return true;
        if ("false".equals(s)) return false;
        throw new IllegalArgumentException("Invalid boolean " + k + " val=" + v);
    }
}

/** FTP 配置实体 */
class FtpServer {
    private String code;
    private String name;
    private String desc;
    private String host;
    private int port;
    private String user;
    private String password;
    private boolean passiveMode;
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDesc() { return desc; }
    public void setDesc(String desc) { this.desc = desc; }
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public boolean isPassiveMode() { return passiveMode; }
    public void setPassiveMode(boolean passiveMode) { this.passiveMode = passiveMode; }
    @Override public String toString() {
        return "FtpServer{code='" + code + "', name='" + name + "', desc='" + desc + "', host='" + host + "', port=" + port + ", user='" + user + "', passiveMode=" + passiveMode + "}";
    }
}

/**
 * 概述
 * FtpConfigRegistry 在应用启动时加载类路径配置并建立只读索引
 * 功能清单
 * 1 静态块按 appEnv 选择 config_{env}.yml 解析 ftp.servers
 * 2 getAllServers 返回全部实体
 * 3 getServerByCode 按 code 获取实体
 * 使用示例
 * List<FtpServer> all = FtpConfigRegistry.getAllServers();
 * FtpServer s = FtpConfigRegistry.getServerByCode("FGLS");
 * 注意事项
 * 仅承担配置到实体的加载与索引 不涉及连接
 * 入参与出参与异常说明
 * 缺字段或重复 code 抛 RuntimeException 未找到 code 抛 NoSuchElementException
 */
final class FtpConfigRegistry {
    private static volatile Map<String, FtpServer> BY_CODE = new LinkedHashMap<String, FtpServer>();
    private static volatile String ENV;
    private static volatile String RESOURCE;
    static { init(); }
    private FtpConfigRegistry() {}
    public static void init() {
        String env = EnvResolver.resolveEnv();
        String res = EnvResolver.resolveClasspathResource(env);
        Map<String, Object> root = YamlLoader.loadFromClasspath(res);
        BY_CODE = parseServers(root, "ftp.servers");
        ENV = env; RESOURCE = res;
    }
    public static List<FtpServer> getAllServers() {
        return Collections.unmodifiableList(new ArrayList<FtpServer>(BY_CODE.values()));
    }
    public static FtpServer getServerByCode(String code) {
        FtpServer s = BY_CODE.get(code);
        if (s == null) throw new NoSuchElementException("FTP not found code=" + code);
        return s;
    }
    public static String env() { return ENV; }
    public static String resource() { return RESOURCE; }
    @SuppressWarnings("unchecked")
    private static Map<String, FtpServer> parseServers(Map<String, Object> root, String path) {
        Object node = YamlLoader.getByPath(root, path);
        if (!(node instanceof List)) throw new RuntimeException("Path not a list " + path);
        List<?> arr = (List<?>) node;
        LinkedHashMap<String, FtpServer> map = new LinkedHashMap<String, FtpServer>();
        for (Object o : arr) {
            if (!(o instanceof Map)) throw new RuntimeException("Item not a map " + o);
            Map<String, Object> m = (Map<String, Object>) o;
            FtpServer s = new FtpServer();
            s.setCode(YamlLoader.reqStr(m, "code"));
            s.setName(YamlLoader.reqStr(m, "name"));
            s.setDesc(String.valueOf(m.getOrDefault("desc", "")));
            s.setHost(YamlLoader.reqStr(m, "host"));
            s.setPort(YamlLoader.reqInt(m, "port"));
            s.setUser(YamlLoader.reqStr(m, "user"));
            s.setPassword(YamlLoader.reqStr(m, "password"));
            s.setPassiveMode(YamlLoader.optBool(m, "passiveMode", false));
            if (map.containsKey(s.getCode())) throw new RuntimeException("Duplicate code " + s.getCode());
            map.put(s.getCode(), s);
        }
        return Collections.unmodifiableMap(map);
    }
}

/**
 * 概述
 * 每次请求时按 code 建立 FTPClient 并返回 调用方用后必须关闭
 * 功能清单
 * 1 create(String code) 基于注册表实体新建连接 登录 设置二进制与被动模式
 * 2 safeClose 安全断开连接
 * 3 可选超时设置 connectTimeout dataTimeout controlKeepAliveTimeout
 * 使用示例
 * FTPClient c = FtpClientFactory.create("FGLS"); try { ... } finally { FtpClientFactory.safeClose(c); }
 * 注意事项
 * FTPClient 非线程安全 不要跨线程共享
 * 入参与出参与异常说明
 * 登录失败或网络异常抛 RuntimeException 未找到 code 抛 NoSuchElementException
 */
final class FtpClientFactory {
    private FtpClientFactory() {}
    public static FTPClient create(String code) {
        FtpServer cfg = FtpConfigRegistry.getServerByCode(code);
        return create(cfg);
    }
    public static FTPClient create(FtpServer cfg) {
        FTPClient c = new FTPClient();
        // 可按需设置超时 c.setConnectTimeout(10000); c.setDataTimeout(20000);
        try {
            c.connect(cfg.getHost(), cfg.getPort());
            boolean ok = c.login(cfg.getUser(), cfg.getPassword());
            if (!ok) { safeClose(c); throw new RuntimeException("FTP login failed code=" + cfg.getCode()); }
            if (cfg.isPassiveMode()) c.enterLocalPassiveMode();
            c.setFileType(FTP.BINARY_FILE_TYPE);
            c.setBufferSize(64 * 1024);
            c.setAutodetectUTF8(true);
            return c;
        } catch (SocketException e) {
            safeClose(c);
            throw new RuntimeException("FTP socket error code=" + cfg.getCode() + " msg=" + e.getMessage(), e);
        } catch (Exception e) {
            safeClose(c);
            throw new RuntimeException("FTP connect error code=" + cfg.getCode() + " msg=" + e.getMessage(), e);
        }
    }
    public static void safeClose(FTPClient c) {
        if (c == null) return;
        try { if (c.isConnected()) { c.logout(); c.disconnect(); } } catch (Exception ignore) {}
    }
}

/** 演示入口 可按需删除 */
class DemoMain {
    /**
     * 概述
     * 展示启动加载与按 code 建连流程
     * 功能清单
     * 1 打印当前 env 与资源
     * 2 列出全部实体
     * 3 建立一次连接并 NOOP
     * 使用示例
     * java -DappEnv=dev -cp ... DemoMain
     * 注意事项
     * 确保目标 FTP 可达
     * 入参与出参与异常说明
     * 连接失败抛出 RuntimeException
     */
    public static void main(String[] args) throws Exception {
        System.out.println("env=" + FtpConfigRegistry.env() + " resource=" + FtpConfigRegistry.resource());
        for (FtpServer s : FtpConfigRegistry.getAllServers()) System.out.println("CFG " + s);
        // 示例: 按需建立连接
        // FTPClient c = FtpClientFactory.create("FGLS");
        // try { System.out.println("NOOP=" + c.sendNoOp()); } finally { FtpClientFactory.safeClose(c); }
    }
}
```

### 配置示例

```
ftp:
  servers:
    - code: FGLS
      name: 辅助系统 FTP
      desc: 财务相关
      host: 10.0.0.10
      port: 21
      user: u1
      password: p1
      passiveMode: true
    - code: NC
      name: NC 系统 FTP
      desc: 交换
      host: 10.0.0.20
      port: 21
      user: u2
      password: p2
      passiveMode: false
```

### 落地与扩展

1. 超时与稳健性: 在`FtpClientFactory.create`中设置`connectTimeout dataTimeout`并在业务侧包装重试策略
2. 认证兼容: 若需 FTPS 或 SFTP，请新建对应工厂类，保持注册表与实体不变
3. 运行期刷新: 如需切换环境，可在安全点调用`FtpConfigRegistry.init()`前先调整`-DappEnv`或替换类路径资源
4. 迁移到连接池: 当 QPS 提升时，仅替换`FtpClientFactory`为池化版本（借还接口不变），其余代码零改动

# 关于池化, 暂时不考虑

迁移到连接池: 当 QPS 提升时，仅替换FtpClientFactory为池化版本（借还接口不变），其余代码零改动

### 代码总览

下面是一套可直接落地的实现：启动时按`-DappEnv=dev|test|prod`加载类路径下`config_{env}.yml`生成实体索引；运行时每次请求基于`code`建立`FTPClient`。同时提供“接口不变”的可插拔实现：默认是“每次请求直连版”，切到连接池只需设置`-DftpPool.enabled=true`，其余业务代码零改动

```java
/**
 * 依赖
 * 1 SnakeYAML 1.33+
 *    <dependency>
 *      <groupId>org.yaml</groupId>
 *      <artifactId>snakeyaml</artifactId>
 *      <version>1.33</version>
 *    </dependency>
 * 2 Apache Commons Net 3.11.1+
 *    <dependency>
 *      <groupId>commons-net</groupId>
 *      <artifactId>commons-net</artifactId>
 *      <version>3.11.1</version>
 *    </dependency>
 */
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.InputStream;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** ==================== 环境与 YAML 工具 ==================== */
final class EnvResolver {
    private EnvResolver() {}
    /**
     * 概述: 解析运行环境
     * 功能清单: 优先 -DappEnv 其次 APP_ENV 环境变量 默认 dev
     * 使用示例: String env = EnvResolver.resolveEnv()
     * 注意事项: 用于选择类路径配置文件
     * 入参与出参与异常说明: 无入参 无异常
     */
    public static String resolveEnv() {
        String p = System.getProperty("appEnv");
        if (p != null && !p.trim().isEmpty()) return p.trim();
        String e = System.getenv("APP_ENV");
        if (e != null && !e.trim().isEmpty()) return e.trim();
        return "dev";
    }
    /**
     * 概述: 生成并校验类路径资源名
     * 功能清单: 组装 config_{env}.yml 并校验存在
     * 使用示例: String res = EnvResolver.resolveClasspathResource("dev")
     * 注意事项: 仅校验存在性
     * 入参与出参与异常说明: 资源不存在抛 IllegalArgumentException
     */
    public static String resolveClasspathResource(String env) {
        String name = "config_" + env + ".yml";
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
        if (in == null) throw new IllegalArgumentException("Classpath resource not found " + name);
        try { in.close(); } catch (Exception ignore) {}
        return name;
    }
}

final class YamlLoader {
    private YamlLoader() {}
    /**
     * 概述: 从类路径读取 YAML 为 Map
     * 功能清单: SafeConstructor 解析 根为 Map
     * 使用示例: Map root = YamlLoader.loadFromClasspath("config_dev.yml")
     * 注意事项: 避免记录敏感字段日志
     * 入参与出参与异常说明: 读取失败或结构错误抛 RuntimeException
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> loadFromClasspath(String resource) {
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
            if (in == null) throw new RuntimeException("Classpath resource not found " + resource);
            Object obj = new Yaml(new SafeConstructor()).load(in);
            if (obj == null) return new LinkedHashMap<String, Object>();
            if (!(obj instanceof Map)) throw new RuntimeException("Root must be a map: " + resource);
            return (Map<String, Object>) obj;
        } catch (Exception e) {
            throw new RuntimeException("Load YAML failed: " + resource, e);
        }
    }
    /** 点路径取子树 */
    @SuppressWarnings("unchecked")
    public static Object getByPath(Map<String, Object> root, String path) {
        Object cur = root;
        for (String p : path.split("\\.")) {
            if (!(cur instanceof Map)) return null;
            cur = ((Map<String, Object>) cur).get(p);
            if (cur == null) return null;
        }
        return cur;
    }
    /** 必填取值与校验 */
    public static String reqStr(Map<String, Object> m, String k) {
        Object v = m.get(k);
        if (v == null) throw new IllegalArgumentException("Missing " + k);
        String s = String.valueOf(v).trim();
        if (s.isEmpty()) throw new IllegalArgumentException("Empty " + k);
        return s;
    }
    public static int reqInt(Map<String, Object> m, String k) {
        Object v = m.get(k);
        if (v == null) throw new IllegalArgumentException("Missing " + k);
        if (v instanceof Number) return ((Number) v).intValue();
        try { return Integer.parseInt(String.valueOf(v).trim()); }
        catch (Exception e) { throw new IllegalArgumentException("Invalid int " + k + " val=" + v); }
    }
    public static boolean optBool(Map<String, Object> m, String k, boolean defVal) {
        Object v = m.get(k);
        if (v == null) return defVal;
        if (v instanceof Boolean) return (Boolean) v;
        String s = String.valueOf(v).trim().toLowerCase(Locale.ROOT);
        if ("true".equals(s)) return true;
        if ("false".equals(s)) return false;
        throw new IllegalArgumentException("Invalid boolean " + k + " val=" + v);
    }
}

/** ==================== 配置实体与注册表 ==================== */
class FtpServer {
    private String code;
    private String name;
    private String desc;
    private String host;
    private int port;
    private String user;
    private String password;
    private boolean passiveMode;
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDesc() { return desc; }
    public void setDesc(String desc) { this.desc = desc; }
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public boolean isPassiveMode() { return passiveMode; }
    public void setPassiveMode(boolean passiveMode) { this.passiveMode = passiveMode; }
    @Override public String toString() {
        return "FtpServer{code='" + code + "', name='" + name + "', desc='" + desc + "', host='" + host + "', port=" + port + ", user='" + user + "', passiveMode=" + passiveMode + "}";
    }
}

final class FtpConfigRegistry {
    private static volatile Map<String, FtpServer> BY_CODE = new LinkedHashMap<String, FtpServer>();
    private static volatile String ENV;
    private static volatile String RESOURCE;
    static { init(); }
    private FtpConfigRegistry() {}
    /**
     * 概述: 启动期加载类路径配置并建立索引
     * 功能清单: 解析 config_{env}.yml → ftp.servers → Map<code,FtpServer>
     * 使用示例: FtpConfigRegistry.init()
     * 注意事项: 仅负责实体与索引 不涉及连接
     * 入参与出参与异常说明: 缺字段或重复 code 抛 RuntimeException
     */
    public static void init() {
        String env = EnvResolver.resolveEnv();
        String res = EnvResolver.resolveClasspathResource(env);
        Map<String, Object> root = YamlLoader.loadFromClasspath(res);
        BY_CODE = parseServers(root, "ftp.servers");
        ENV = env; RESOURCE = res;
    }
    public static List<FtpServer> getAllServers() {
        return Collections.unmodifiableList(new ArrayList<FtpServer>(BY_CODE.values()));
    }
    public static FtpServer getServerByCode(String code) {
        FtpServer s = BY_CODE.get(code);
        if (s == null) throw new NoSuchElementException("FTP not found code=" + code);
        return s;
    }
    public static String env() { return ENV; }
    public static String resource() { return RESOURCE; }
    @SuppressWarnings("unchecked")
    private static Map<String, FtpServer> parseServers(Map<String, Object> root, String path) {
        Object node = YamlLoader.getByPath(root, path);
        if (!(node instanceof List)) throw new RuntimeException("Path not a list " + path);
        List<?> arr = (List<?>) node;
        LinkedHashMap<String, FtpServer> map = new LinkedHashMap<String, FtpServer>();
        for (Object o : arr) {
            if (!(o instanceof Map)) throw new RuntimeException("Item not a map " + o);
            Map<String, Object> m = (Map<String, Object>) o;
            FtpServer s = new FtpServer();
            s.setCode(YamlLoader.reqStr(m, "code"));
            s.setName(YamlLoader.reqStr(m, "name"));
            s.setDesc(String.valueOf(m.getOrDefault("desc", "")));
            s.setHost(YamlLoader.reqStr(m, "host"));
            s.setPort(YamlLoader.reqInt(m, "port"));
            s.setUser(YamlLoader.reqStr(m, "user"));
            s.setPassword(YamlLoader.reqStr(m, "password"));
            s.setPassiveMode(YamlLoader.optBool(m, "passiveMode", false));
            if (map.containsKey(s.getCode())) throw new RuntimeException("Duplicate code " + s.getCode());
            map.put(s.getCode(), s);
        }
        return Collections.unmodifiableMap(map);
    }
}

/** ==================== 客户端提供器 SPI 与门面 ==================== */
/**
 * 概述: 统一的 FTP 客户端获取与释放接口
 * 功能清单: acquire 获取可用客户端 release 归还或关闭
 * 使用示例: FTPClient c = FtpClients.acquire("FGLS"); try { ... } finally { FtpClients.release("FGLS", c); }
 * 注意事项: 保证成对调用 acquire 与 release
 * 入参与出参与异常说明: 未找到 code 或连接失败抛 RuntimeException
 */
interface FtpClientProvider {
    FTPClient acquire(String code);
    void release(String code, FTPClient client);
}

/**
 * 概述: 门面类 按开关选择实现
 * 功能清单: -DftpPool.enabled=true 切换到池化实现
 * 使用示例: FtpClients.acquire(code) 与 FtpClients.release(code, client)
 * 注意事项: 业务方只依赖本类 即可在直连与池化间零改动切换
 * 入参与出参与异常说明: 同具体实现
 */
final class FtpClients {
    private static final FtpClientProvider PROVIDER;
    static {
        boolean pool = Boolean.parseBoolean(System.getProperty("ftpPool.enabled", "false"));
        PROVIDER = pool ? new PooledClientProvider() : new PerRequestClientProvider();
    }
    private FtpClients() {}
    public static FTPClient acquire(String code) { return PROVIDER.acquire(code); }
    public static void release(String code, FTPClient client) { PROVIDER.release(code, client); }

    /** 函数式模板 帮你自动释放 */
    public static <R> R withClient(String code, FtpFunction<FTPClient, R> fn) {
        FTPClient c = acquire(code);
        try { return fn.apply(c); } finally { release(code, c); }
    }
    public interface FtpFunction<T, R> { R apply(T t); }
}

/** ==================== 直连版实现 每次请求现建现断 ==================== */
final class PerRequestClientProvider implements FtpClientProvider {
    /**
     * 概述: 获取可用客户端
     * 功能清单: 按 code 查实体 建立连接 登录 配置被动与二进制
     * 使用示例: FTPClient c = acquire("FGLS")
     * 注意事项: 用完务必 release
     * 入参与出参与异常说明: 连接失败抛 RuntimeException
     */
    public FTPClient acquire(String code) {
        FtpServer cfg = FtpConfigRegistry.getServerByCode(code);
        return create(cfg);
    }
    /**
     * 概述: 释放客户端
     * 功能清单: 直连版释放即关闭
     * 使用示例: release("FGLS", client)
     * 注意事项: 可重复调用
     * 入参与出参与异常说明: 无异常
     */
    public void release(String code, FTPClient client) {
        safeClose(client);
    }
    private static FTPClient create(FtpServer cfg) {
        FTPClient c = new FTPClient();
        // 可按需调整超时
        String ct = System.getProperty("ftp.connectTimeoutMs", "10000");
        String dt = System.getProperty("ftp.dataTimeoutMs", "20000");
        try {
            c.setConnectTimeout(Integer.parseInt(ct));
            c.setDataTimeout(Integer.parseInt(dt));
        } catch (Exception ignore) {}
        try {
            c.connect(cfg.getHost(), cfg.getPort());
            boolean ok = c.login(cfg.getUser(), cfg.getPassword());
            if (!ok) { safeClose(c); throw new RuntimeException("FTP login failed code=" + cfg.getCode()); }
            if (cfg.isPassiveMode()) c.enterLocalPassiveMode();
            c.setFileType(FTP.BINARY_FILE_TYPE);
            c.setBufferSize(64 * 1024);
            c.setAutodetectUTF8(true);
            return c;
        } catch (SocketException e) {
            safeClose(c);
            throw new RuntimeException("FTP socket error code=" + cfg.getCode() + " msg=" + e.getMessage(), e);
        } catch (Exception e) {
            safeClose(c);
            throw new RuntimeException("FTP connect error code=" + cfg.getCode() + " msg=" + e.getMessage(), e);
        }
    }
    static void safeClose(FTPClient c) {
        if (c == null) return;
        try { if (c.isConnected()) { c.logout(); c.disconnect(); } } catch (Exception ignore) {}
    }
}

/** ==================== 池化版实现 可通过 -DftpPool.enabled=true 启用 ==================== */
final class PooledClientProvider implements FtpClientProvider {
    /** 每个 code 一个子池 */
    private final ConcurrentHashMap<String, SubPool> pools = new ConcurrentHashMap<String, SubPool>();
    private final int maxTotal = Integer.getInteger("ftpPool.maxPerCode", 4);
    private final int minIdle = Integer.getInteger("ftpPool.minIdlePerCode", 0);
    private final int connectTimeout = Integer.getInteger("ftp.connectTimeoutMs", 10000);
    private final int dataTimeout = Integer.getInteger("ftp.dataTimeoutMs", 20000);

    public FTPClient acquire(String code) {
        SubPool pool = pools.computeIfAbsent(code, k -> new SubPool(k, maxTotal, minIdle, connectTimeout, dataTimeout));
        return pool.borrow();
    }
    public void release(String code, FTPClient client) {
        SubPool pool = pools.get(code);
        if (pool == null) {
            PerRequestClientProvider.safeClose(client);
            return;
        }
        pool.giveBack(client);
    }

    /** 子池实现 */
    static final class SubPool {
        private final String code;
        private final int maxTotal;
        private final int minIdle;
        private final int connectTimeout;
        private final int dataTimeout;
        private final Deque<FTPClient> idle = new ArrayDeque<FTPClient>();
        private final AtomicInteger total = new AtomicInteger(0);
        SubPool(String code, int maxTotal, int minIdle, int connectTimeout, int dataTimeout) {
            this.code = code; this.maxTotal = Math.max(1, maxTotal); this.minIdle = Math.max(0, minIdle);
            this.connectTimeout = connectTimeout; this.dataTimeout = dataTimeout;
        }
        /**
         * 概述: 借出一个可用连接
         * 功能清单: 先取闲置 健康校验失败丢弃 不足则新建 超限抛异常
         * 使用示例: FTPClient c = pool.borrow()
         * 注意事项: 非阻塞 超限时抛异常或可自行扩展等待策略
         * 入参与出参与异常说明: 超限或连接失败抛 RuntimeException
         */
        public FTPClient borrow() {
            FTPClient c;
            synchronized (idle) {
                while ((c = idle.pollFirst()) != null) {
                    if (isHealthy(c)) return c; // 复用健康连接
                    PerRequestClientProvider.safeClose(c); // 不健康丢弃
                    total.decrementAndGet();
                }
                if (total.get() < maxTotal) {
                    total.incrementAndGet();
                } else {
                    throw new RuntimeException("FTP pool exhausted code=" + code + " maxTotal=" + maxTotal);
                }
            }
            // 构建新连接（同步块外）
            try {
                FtpServer cfg = FtpConfigRegistry.getServerByCode(code);
                FTPClient nc = new FTPClient();
                nc.setConnectTimeout(connectTimeout);
                nc.setDataTimeout(dataTimeout);
                nc.connect(cfg.getHost(), cfg.getPort());
                boolean ok = nc.login(cfg.getUser(), cfg.getPassword());
                if (!ok) throw new RuntimeException("FTP login failed code=" + cfg.getCode());
                if (cfg.isPassiveMode()) nc.enterLocalPassiveMode();
                nc.setFileType(FTP.BINARY_FILE_TYPE);
                nc.setBufferSize(64 * 1024);
                nc.setAutodetectUTF8(true);
                return nc;
            } catch (Throwable t) {
                total.decrementAndGet();
                throw new RuntimeException("FTP connect error code=" + code + " msg=" + t.getMessage(), t);
            }
        }
        /**
         * 概述: 归还连接
         * 功能清单: 健康则回池 超过 minIdle 进行裁剪
         * 使用示例: pool.giveBack(client)
         * 注意事项: 连接可能在业务侧已断开 此处做健康判断
         * 入参与出参与异常说明: 无异常
         */
        public void giveBack(FTPClient c) {
            if (c == null) return;
            if (!isHealthy(c)) {
                PerRequestClientProvider.safeClose(c);
                total.decrementAndGet();
                return;
            }
            synchronized (idle) {
                idle.offerFirst(c);
                while (idle.size() > minIdle) {
                    FTPClient drop = idle.pollLast();
                    if (drop == null) break;
                    PerRequestClientProvider.safeClose(drop);
                    total.decrementAndGet();
                }
            }
        }
        /** 健康检查 sendNoOp + isConnected */
        private static boolean isHealthy(FTPClient c) {
            if (c == null) return false;
            if (!c.isConnected()) return false;
            try { return c.sendNoOp(); } catch (Exception e) { return false; }
        }
    }
}

/** ==================== 使用示例 ==================== */
class DemoMain {
    /**
     * 概述: 展示启动加载与按 code 获取客户端
     * 功能清单: 打印环境与资源 列出实体 执行一次 NOOP
     * 使用示例: java -DappEnv=dev -cp ... DemoMain
     * 注意事项: 直连版默认 可加 -DftpPool.enabled=true 切池化
     * 入参与出参与异常说明: 连接失败抛 RuntimeException
     */
    public static void main(String[] args) throws Exception {
        System.out.println("env=" + FtpConfigRegistry.env() + " resource=" + FtpConfigRegistry.resource());
        for (FtpServer s : FtpConfigRegistry.getAllServers()) System.out.println("CFG " + s);
        // 业务侧推荐模板：自动借还
        // String reply = FtpClients.withClient("FGLS", c -> {
        //     try { return "NOOP=" + c.sendNoOp(); } catch (Exception e) { throw new RuntimeException(e); }
        // });
        // System.out.println(reply);
        // 或手动借还
        // FTPClient c = FtpClients.acquire("FGLS");
        // try { System.out.println("NOOP=" + c.sendNoOp()); } finally { FtpClients.release("FGLS", c); }
    }
}
```

### 关键开关

- 直连版与池化版切换`-DftpPool.enabled=true|false`
- 池参数`-DftpPool.maxPerCode=4 -DftpPool.minIdlePerCode=0`
- 超时`-Dftp.connectTimeoutMs=10000 -Dftp.dataTimeoutMs=20000`

### 使用要点

- 启动即完成配置与索引，`FtpConfigRegistry.getServerByCode(code)`可随时取到实体
- 运行时统一通过`FtpClients.acquire/release`获取与释放客户端；或使用`withClient`模板自动借还
- 需要池化时仅改启动参数，业务代码不变

