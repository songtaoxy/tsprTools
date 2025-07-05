# 功能说明

 `JsonPathUtils` 工具类，支持以下增强功能：

------

### 功能增强点

1. **支持 `users[0].name`、`users.0.name` 两种路径风格**
2. **支持返回结果自动转换为常用类型**：`String`、`int`、`boolean`、`double`
3. **提供类型安全的封装方法**：如 `getIntByPath(...)`, `getBooleanByPath(...)`
4. **错误路径容错，返回默认值而非抛异常**
5. **路径中支持混合对象、数组嵌套任意层**

------

### 完整工具类：`JsonPathUtils`

```java
import com.fasterxml.jackson.databind.JsonNode;

public class JsonPathUtils {

    public static JsonNode getByPath(JsonNode root, String path) {
        if (root == null || path == null || path.isEmpty()) return null;

        String[] parts = path.replaceAll("\\[(\\d+)]", ".$1").split("\\.");
        JsonNode current = root;

        for (String part : parts) {
            if (current == null || current.isMissingNode()) return null;

            if (part.matches("\\d+")) {
                if (current.isArray()) {
                    current = current.path(Integer.parseInt(part));
                } else {
                    return null;
                }
            } else {
                current = current.path(part);
            }
        }

        return current.isMissingNode() ? null : current;
    }

    public static String getTextByPath(JsonNode root, String path, String defaultValue) {
        JsonNode node = getByPath(root, path);
        return node != null && node.isValueNode() ? node.asText() : defaultValue;
    }

    public static Integer getIntByPath(JsonNode root, String path, Integer defaultValue) {
        JsonNode node = getByPath(root, path);
        return node != null && node.isInt() ? node.asInt() : defaultValue;
    }

    public static Double getDoubleByPath(JsonNode root, String path, Double defaultValue) {
        JsonNode node = getByPath(root, path);
        return node != null && node.isNumber() ? node.asDouble() : defaultValue;
    }

    public static Boolean getBooleanByPath(JsonNode root, String path, Boolean defaultValue) {
        JsonNode node = getByPath(root, path);
        return node != null && node.isBoolean() ? node.asBoolean() : defaultValue;
    }
}
```

------

### 支持的路径示例

| 路径表达式              | 等价形式              | 说明                   |
| ----------------------- | --------------------- | ---------------------- |
| `users[0].name`         | `users.0.name`        | 支持数组中括号或点语法 |
| `user.address.city`     | —                     | 对象嵌套               |
| `data[2].items[1].name` | `data.2.items.1.name` | 混合嵌套               |

------

###  示例用法

```java
ObjectMapper mapper = new ObjectMapper();
JsonNode root = mapper.readTree(jsonString);

String name = JsonPathUtils.getTextByPath(root, "users[1].name", "未知");
Integer age = JsonPathUtils.getIntByPath(root, "users[0].age", 0);
Boolean active = JsonPathUtils.getBooleanByPath(root, "users[0].active", false);
Double score = JsonPathUtils.getDoubleByPath(root, "meta.score", 0.0);
```

------

### 输出安全、无需手动判断类型

| 方法               | 返回类型  | 自动判断类型 | 支持默认值 |
| ------------------ | --------- | ------------ | ---------- |
| `getTextByPath`    | `String`  | ✅            | ✅          |
| `getIntByPath`     | `Integer` | ✅            | ✅          |
| `getBooleanByPath` | `Boolean` | ✅            | ✅          |
| `getDoubleByPath`  | `Double`  | ✅            | ✅          |

------

如你希望进一步：

- 支持路径表达式缓存
- 支持 JsonPath 表达式（如 `$..user.name`，依赖 `JsonPath` 第三方库）
- 支持从字符串 JSON 输入直接提取路径值

我可以继续提供补充。是否继续？

# 测试案例

以下是基于 `JsonPathUtils` 工具类的**所有可能的测试场景与覆盖案例**，涵盖**对象、数组、混合结构、类型转换、路径错误等各种边界情况**，确保路径解析、类型判断、默认值逻辑、结构容错等完全可测。

------

### 测试结构说明

假设 JSON 如下（结构涵盖值节点、数组、对象、嵌套、空值等）：

```json
{
  "user": {
    "name": "张三",
    "age": 30,
    "active": true,
    "score": 88.5,
    "address": {
      "city": "北京",
      "zip": null
    }
  },
  "users": [
    {
      "name": "李四",
      "age": 28,
      "active": false
    },
    {
      "name": "王五",
      "address": {
        "city": "上海"
      }
    }
  ],
  "meta": {
    "tags": ["开发", "测试"],
    "emptyArray": [],
    "emptyObject": {}
  }
}
```

------

###  测试场景与案例

| 场景编号 | 场景描述              | 路径示例                         | 期望结果                          |
| -------- | --------------------- | -------------------------------- | --------------------------------- |
| 1        | 正常读取对象字段      | `user.name`                      | `"张三"`                          |
| 2        | 读取 int 类型字段     | `user.age`                       | `30`                              |
| 3        | 读取 boolean 类型字段 | `user.active`                    | `true`                            |
| 4        | 读取 double 类型字段  | `user.score`                     | `88.5`                            |
| 5        | 嵌套字段读取          | `user.address.city`              | `"北京"`                          |
| 6        | 读取 null 值字段      | `user.address.zip`               | `null` 或默认值                   |
| 7        | 读取数组元素值        | `users[0].name` / `users.0.name` | `"李四"`                          |
| 8        | 数组中嵌套对象        | `users[1].address.city`          | `"上海"`                          |
| 9        | 数组下标越界          | `users[99].name`                 | `null`                            |
| 10       | 非数组结构读取下标    | `user[0].name`                   | `null`                            |
| 11       | 非对象结构读取字段    | `user.age.name`                  | `null`                            |
| 12       | 不存在路径字段        | `user.unknown.key`               | `null`                            |
| 13       | 空数组读取            | `meta.emptyArray[0]`             | `null`                            |
| 14       | 空对象字段读取        | `meta.emptyObject.key`           | `null`                            |
| 15       | 多层数组嵌套          | `meta.tags[1]`                   | `"测试"`                          |
| 16       | 非值节点读取 text     | `user.address`                   | JSON 对象字符串（或 null）        |
| 17       | 非值节点读取 int      | `user.address`                   | 默认值返回                        |
| 18       | 非值节点读取 boolean  | `users`                          | 默认值返回                        |
| 19       | 数字路径混合语法      | `users.1.address.city`           | `"上海"`                          |
| 20       | 数组语法兼容性        | `users.1["address"]["city"]`     | 不支持（需规范路径）              |
| 21       | 根节点空/null         | `getByPath(null, "a.b")`         | `null`                            |
| 22       | 空路径或空字符串      | `getByPath(json, "")`            | `null`                            |
| 23       | 不合法路径            | `"users...name"`                 | 安全返回 `null`                   |
| 24       | 值为字符串 "null"     | 显示为 `"null"` 字符串           | `"null"`（非 JSON null）          |
| 25       | 返回数组节点          | `meta.tags`                      | 是数组节点，`.isArray() == true`  |
| 26       | 返回对象节点          | `user.address`                   | 是对象节点，`.isObject() == true` |

------

### 建议测试用例方法结构

```java
@Test
public void testSimpleStringPath() {
    assertEquals("张三", JsonPathUtils.getTextByPath(root, "user.name", null));
}

@Test
public void testNestedArrayObject() {
    assertEquals("上海", JsonPathUtils.getTextByPath(root, "users[1].address.city", null));
}

@Test
public void testInvalidArrayIndex() {
    assertNull(JsonPathUtils.getTextByPath(root, "users[10].name", null));
}

@Test
public void testNonExistingPath() {
    assertNull(JsonPathUtils.getTextByPath(root, "user.nonexistent.key", null));
}

@Test
public void testArrayStylePathCompatible() {
    assertEquals("李四", JsonPathUtils.getTextByPath(root, "users.0.name", null));
}

@Test
public void testDefaultValueFallback() {
    assertEquals("默认", JsonPathUtils.getTextByPath(root, "user.unknown", "默认"));
}

@Test
public void testGetBoolean() {
    assertTrue(JsonPathUtils.getBooleanByPath(root, "user.active", false));
}

@Test
public void testGetDouble() {
    assertEquals(88.5, JsonPathUtils.getDoubleByPath(root, "user.score", 0.0), 0.01);
}
```

------

### 其他建议测试点

- 使用 `ObjectMapper.readTree("")` 测试空 JSON
- 使用 `ObjectMapper.readTree("{}")` 测试空对象
- 验证边界字符，如 `users[0].name` vs `users.0.name`
- 对 `JsonNode` 返回值判断 `.isObject()`, `.isArray()`, `.isValueNode()` 的行为

------

如你希望我生成一整套 JUnit5 单元测试类模板，或支持 AssertJ 风格断言，我可以继续输出。是否需要？